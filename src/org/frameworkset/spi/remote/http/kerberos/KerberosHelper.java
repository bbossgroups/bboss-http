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

import com.frameworkset.util.SimpleStringUtil;
import com.sun.security.auth.login.ConfigFile;
import org.frameworkset.spi.assemble.GetProperties;
import org.frameworkset.spi.remote.http.ClientConfiguration;
import org.frameworkset.spi.remote.http.HttpRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.AppConfigurationEntry;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author biaoping.yin
 * @Date 2025/2/7
 */
public class KerberosHelper {
    private static final Logger LOG = LoggerFactory.getLogger(KerberosHelper.class);
    public static AppConfigurationEntry[] readAppConfigurationEntryFromFile(KerberosConfig kerberosConfig) {
        AppConfigurationEntry[] entries = null;
        File jaasConfigFile = new File(kerberosConfig.getLoginConfig());
        if (jaasConfigFile.exists() && jaasConfigFile.isFile()) {
            ConfigFile configFile = new ConfigFile(jaasConfigFile.toURI());
            LOG.info(String.format(Locale.ENGLISH, "Get application configuration entry use by application name %s.", kerberosConfig.getLoginContextName()));
            entries = configFile.getAppConfigurationEntry(kerberosConfig.getLoginContextName());
            if (entries == null || entries.length <= 0) {
                LOG.info(String.format(Locale.ENGLISH, "Get application configuration entry use by application name %s.", "Client"));
            }
        }

        return entries;
    }
    public static AppConfigurationEntry[] getAppConfigurationEntry(KerberosConfig kerberosConfig) {
        if(kerberosConfig.getConfigMode() == KerberosConfig.CONFIG_MODE_JAAS_LOGIN_CONFIG){
            return readAppConfigurationEntryFromFile(  kerberosConfig);
        }
        else {
            return new AppConfigurationEntry[]{new AppConfigurationEntry("com.sun.security.auth.module.Krb5LoginModule",
                    AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, new HashMap<String, Object>() {
                {
                    put("useTicketCache", kerberosConfig.getUseTicketCache());
                    if (SimpleStringUtil.isNotEmpty(kerberosConfig.getKeytab()))
                        put("useKeyTab", "true");
                    put("keyTab", kerberosConfig.getKeytab());
                    //Krb5 in GSS API needs to be refreshed so it does not throw the error
                    //Specified version of key is not available
                    put("refreshKrb5Config", kerberosConfig.getRefreshKrb5Config());
                    put("principal", kerberosConfig.getPrincipal());
                    put("storeKey", kerberosConfig.getStoreKey());
                    put("doNotPrompt", kerberosConfig.getDoNotPrompt());
                    put("isInitiator", kerberosConfig.getIsInitiator());
                    put("debug", kerberosConfig.getDebug());
                    if (kerberosConfig.getExts() != null && !kerberosConfig.getExts().isEmpty()) {
                        putAll(kerberosConfig.getExts());
                    }
                }
            })};
        }
    }
    public static KerberosConfig buildKerberosConfig(String name, GetProperties context,String healthPoolname,StringBuilder log ) throws Exception {
        Map<String,Object> kerberosConfigs = ClientConfiguration._getValuesWithPrex(name, "http.kerberos", context);
        KerberosConfig kerberosConfig = null;
        if(kerberosConfigs != null && !kerberosConfigs.isEmpty()) {
            log.append(",kerberosConfigs=").append(SimpleStringUtil.object2json(kerberosConfigs));
            kerberosConfig = new KerberosConfig();
            Iterator<Map.Entry<String, Object>> iterator = kerberosConfigs.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<String, Object> entry = iterator.next();
                String key = entry.getKey();

//            #Krb5 in GSS API needs to be refreshed so it does not throw the error
//#Specified version of key is not available

                if(key.equals("principal")){
                    kerberosConfig.setPrincipal((String)entry.getValue());
                }
                else if(key.equals("serverRealmPath")){
                    kerberosConfig.setServerRealmPath((String)entry.getValue());
                }
                else if(key.equals("serverRealmHttpMethod")){
                    kerberosConfig.setServerRealmHttpMethod((String)entry.getValue());
                }
                else if(key.equals("serverRealm")){
                    kerberosConfig.setServerRealm((String)entry.getValue());
                }
                
                else if(key.equals("loginContextName")){
                    kerberosConfig.setLoginContextName((String)entry.getValue());
                }
                else if(key.equals("useSubjectCredsOnly")){
                    kerberosConfig.setUseSubjectCredsOnly((String)entry.getValue());
                }
                else if(key.equals("keytab")){
                    kerberosConfig.setKeytab((String)entry.getValue());
                }
                else if(key.equals("krb5Location")){
                    kerberosConfig.setKrb5Location((String)entry.getValue());
                }
                else if(key.equals("useTicketCache")){
                    kerberosConfig.setUseTicketCache((String)entry.getValue());
                }
                else if(key.equals("useKeyTab")){
                    kerberosConfig.setUseKeyTab((String)entry.getValue());
                }
                else if(key.equals("refreshKrb5Config")){
                    kerberosConfig.setRefreshKrb5Config((String)entry.getValue());
                }
                else if(key.equals("storeKey")){
                    kerberosConfig.setStoreKey((String)entry.getValue());
                }
                else if(key.equals("doNotPrompt")){
                    kerberosConfig.setDoNotPrompt((String)entry.getValue());
                }
                else if(key.equals("isInitiator")){
                    kerberosConfig.setIsInitiator((String)entry.getValue());
                }
                else if(key.equals("debug")){
                    kerberosConfig.setDebug((String)entry.getValue());
                }
                else if(key.equals("loginConfig")){
                    kerberosConfig.setLoginConfig((String)entry.getValue());
                }                
                else {
                    
                    kerberosConfig.addProperty(key,(String)entry.getValue());
                }
            }
        }
        if(kerberosConfig != null)
            validateKerberosConfig(kerberosConfig);
        return kerberosConfig;
    }
    private static void validateKerberosConfig(KerberosConfig kerberosConfig){
        if(SimpleStringUtil.isNotEmpty(kerberosConfig.getPrincipal()) 
                &&  SimpleStringUtil.isNotEmpty(kerberosConfig.getKrb5Location()) 
                && SimpleStringUtil.isNotEmpty(kerberosConfig.getKeytab())){
            kerberosConfig.setConfigMode(KerberosConfig.CONFIG_MODE_PARAMS);
            return;
        }

        if(SimpleStringUtil.isNotEmpty(kerberosConfig.getLoginConfig())
                &&  SimpleStringUtil.isNotEmpty(kerberosConfig.getKrb5Location())){
            kerberosConfig.setConfigMode(KerberosConfig.CONFIG_MODE_JAAS_LOGIN_CONFIG);
            return;
        }
        throw new HttpRuntimeException("Kerberos set check failed: \n1. Principal and Krb5Location and Keytab must been setted. \n or \n 2.LoginConfig and Krb5Location must be setted.");
    }

}
