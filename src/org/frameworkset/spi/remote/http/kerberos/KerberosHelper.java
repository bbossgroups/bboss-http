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
import org.frameworkset.spi.assemble.GetProperties;
import org.frameworkset.spi.remote.http.ClientConfiguration;
import org.frameworkset.spi.remote.http.HttpRuntimeException;

import java.util.Iterator;
import java.util.Map;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author biaoping.yin
 * @Date 2025/2/7
 */
public class KerberosHelper {
    
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
