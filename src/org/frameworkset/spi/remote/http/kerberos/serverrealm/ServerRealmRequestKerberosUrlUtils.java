package org.frameworkset.spi.remote.http.kerberos.serverrealm;
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

import org.apache.http.impl.client.HttpClientBuilder;
import org.frameworkset.spi.remote.http.ClientConfiguration;
import org.frameworkset.spi.remote.http.kerberos.BaseRequestKerberosUrlUtils;
import org.frameworkset.spi.remote.http.kerberos.KerberosCallException;
import org.frameworkset.spi.remote.http.kerberos.KerberosCallback;
import org.frameworkset.spi.remote.http.kerberos.KerberosConfig;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author biaoping.yin
 * @Date 2025/2/7
 */
public  class ServerRealmRequestKerberosUrlUtils extends BaseRequestKerberosUrlUtils {

    private ServerRealmClientHelper serverRealmClientHelper;
    public ServerRealmRequestKerberosUrlUtils(KerberosConfig kerberosConfig, ClientConfiguration clientConfiguration) {
        super(kerberosConfig,  clientConfiguration);
        serverRealmClientHelper = new ServerRealmClientHelper( clientConfiguration);
    }
 
    @Override
    protected Subject getSubject() throws LoginException {
        return serverRealmClientHelper.getSubj();
    }

    @Override
    public HttpClientBuilder customizeHttpClient(HttpClientBuilder builder , ClientConfiguration clientConfiguration) throws Exception{
        builder.addInterceptorLast(new ServerRealmKerberosHttpRequestInterceptor(this.serverRealmClientHelper));
        builder.addInterceptorLast(new ServerRealmKerberosHttpResponseInterceptor(this.serverRealmClientHelper));
        return builder;
    }


    @Override
    public <T> T callRestUrl(KerberosCallback<T> kerberosCallback) throws Exception {

        try {
 
           return kerberosCallback.call();
                  
        } catch (KerberosCallException le) {
            throw (Exception) le.getCause();
        } catch (Exception le) {
            throw le;
        }

    }

    @Override
    public void afterStart() {
        this.serverRealmClientHelper.authenticate();
    }
    @Override
    public void close() {
        
        this.serverRealmClientHelper.close();
    }
 
}
