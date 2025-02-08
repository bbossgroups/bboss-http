package org.frameworkset.spi.remote.http.kerberos;
/**
 * Copyright 2025 bboss
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.config.Lookup;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.impl.auth.SPNegoSchemeFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.frameworkset.spi.remote.http.ClientConfiguration;
import org.frameworkset.spi.remote.http.HttpRequestProxy;

import javax.security.auth.Subject;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginException;
import java.security.Principal;
import java.security.PrivilegedAction;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author biaoping.yin
 * @Date 2025/2/7
 */
public abstract class BaseRequestKerberosUrlUtils {
    protected KerberosConfig kerberosConfig;
    protected Configuration configuration;

    public BaseRequestKerberosUrlUtils(KerberosConfig kerberosConfig) {
        this.kerberosConfig = kerberosConfig;
        System.setProperty("java.security.krb5.conf", kerberosConfig.getKrb5Location());
        if (kerberosConfig.getDebug() != null && kerberosConfig.getDebug().equals("true")) {
            System.setProperty("sun.security.spnego.debug", "true");
            System.setProperty("sun.security.krb5.debug", "true");
        }
    }

    //模拟curl使用kerberos认证
    public  void buildSpengoHttpClient(HttpClientBuilder builder) {
//        HttpClientBuilder builder = HttpClientBuilder.create();
        Lookup<AuthSchemeProvider> authSchemeRegistry = RegistryBuilder.<AuthSchemeProvider>create().
                register(AuthSchemes.SPNEGO, new SPNegoSchemeFactory(true)).build();
        builder.setDefaultAuthSchemeRegistry(authSchemeRegistry);
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(new AuthScope(null, -1, null), new Credentials() {
            @Override
            public Principal getUserPrincipal() {
                return null;
            }

            @Override
            public String getPassword() {
                return null;
            }
        });
        builder.setDefaultCredentialsProvider(credentialsProvider);
//        CloseableHttpClient httpClient = builder.build();
//        return httpClient;
    }

    protected abstract Subject getSubject() throws LoginException ;

    public <T> T callRestUrl(ClientConfiguration clientConfiguration, HttpClient spnegoHttpClient, String url, HttpRequestProxy.ExecuteRequest executeRequest,int triesCount) throws Exception {
       
        try {
          
            Subject serviceSubject = getSubject();
            return Subject.doAs(serviceSubject, new PrivilegedAction<T>() {

                @Override
                public T run() {
                    try {
                        return (T) executeRequest.execute(clientConfiguration, spnegoHttpClient, url, triesCount);
                    } catch (Exception e) {
                        throw new KerberosCallException(e);
                    }
                }
            });
        } catch (KerberosCallException le) {
            throw (Exception) le.getCause();
        } catch (Exception le) {
            throw le;
        }

    }

    public <T> T callRestUrl(KerberosCallback<T> kerberosCallback) throws Exception {

        try {

            Subject serviceSubject = getSubject();
            return Subject.doAs(serviceSubject, new PrivilegedAction<T>() {

                @Override
                public T run() {
                    try {
                        return kerberosCallback.call();
                    } catch (Exception e) {
                        throw new KerberosCallException(e);
                    }
                }
            });
        } catch (KerberosCallException le) {
            throw (Exception) le.getCause();
        } catch (Exception le) {
            throw le;
        }

    }
}
