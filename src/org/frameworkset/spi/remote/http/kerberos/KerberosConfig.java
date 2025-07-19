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

import org.frameworkset.spi.remote.http.HttpMethodName;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author biaoping.yin
 * @Date 2025/2/7
 */
public class KerberosConfig {
    public static final int CONFIG_MODE_PARAMS = 1;
    public static final int CONFIG_MODE_JAAS_LOGIN_CONFIG = 2;

    /**
    http.kerberos.principal=elastic/admin@BBOSSGROUPS.COM
    http.kerberos.keytab=C:/environment/es/8.13.2/elasticsearch-8.13.2/config/elastic.keytab
    http.kerberos.krb5Location=C:/environment/es/8.13.2/elasticsearch-8.13.2/config/krb5.conf
    http.kerberos.useTicketCache=false
    http.kerberos.useKeyTab=true

            #Krb5 in GSS API needs to be refreshed so it does not throw the error
#Specified version of key is not available
    http.kerberos.refreshKrb5Config=true

    http.kerberos.storeKey=true
    http.kerberos.doNotPrompt=true
    http.kerberos.isInitiator=true
    http.kerberos.debug=true
     */
    private String loginContextName = "Krb5Login";
    private String principal;
    private String keytab;
    private String krb5Location;
    private String useTicketCache = "false";
    private String useKeyTab = "true";

//            #Krb5 in GSS API needs to be refreshed so it does not throw the error
//#Specified version of key is not available
    private String refreshKrb5Config = "true";

    private String storeKey = "true";
    private String doNotPrompt = "true";
    private String isInitiator = "true";
    private String debug = "false";



    private String serverRealmPath;
    private String serverRealmHttpMethod = HttpMethodName.HTTP_GET;
    private String serverRealm;
    private String useSubjectCredsOnly ;
    private Map<String,String> exts;

    /**
     * java.security.auth.login.config
     */
    private String loginConfig;
    
    private int configMode  ;

    public String getPrincipal() {
        return principal;
    }
    public String getUseSubjectCredsOnly() {
        return useSubjectCredsOnly;
    }

    public void setUseSubjectCredsOnly(String useSubjectCredsOnly) {
        this.useSubjectCredsOnly = useSubjectCredsOnly;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getKeytab() {
        return keytab;
    }

    public void setKeytab(String keytab) {
        this.keytab = keytab;
    }

    public String getKrb5Location() {
        return krb5Location;
    }

    public void setKrb5Location(String krb5Location) {
        this.krb5Location = krb5Location;
    }

    public String getUseTicketCache() {
        return useTicketCache;
    }
    public String getServerRealmPath() {
        return serverRealmPath;
    }

    public void setServerRealmPath(String serverRealmPath) {
        this.serverRealmPath = serverRealmPath;
    }
    public void setUseTicketCache(String useTicketCache) {
        this.useTicketCache = useTicketCache;
    }

    public String getUseKeyTab() {
        return useKeyTab;
    }

    public void setUseKeyTab(String useKeyTab) {
        this.useKeyTab = useKeyTab;
    }

    public String getRefreshKrb5Config() {
        return refreshKrb5Config;
    }

    public void setRefreshKrb5Config(String refreshKrb5Config) {
        this.refreshKrb5Config = refreshKrb5Config;
    }

    public String getStoreKey() {
        return storeKey;
    }

    public void setStoreKey(String storeKey) {
        this.storeKey = storeKey;
    }

    public String getDoNotPrompt() {
        return doNotPrompt;
    }

    public void setDoNotPrompt(String doNotPrompt) {
        this.doNotPrompt = doNotPrompt;
    }

    public String getIsInitiator() {
        return isInitiator;
    }

    public void setIsInitiator(String isInitiator) {
        this.isInitiator = isInitiator;
    }

    public String getDebug() {
        return debug;
    }

    public void setDebug(String debug) {
        this.debug = debug;
    }

    public Map<String, String> getExts() {
        return exts;
    }

    public void setExts(Map<String, String> exts) {
        this.exts = exts;
    }
    
    public KerberosConfig addProperty(String name,String value){
        if(exts == null){
            exts = new LinkedHashMap<>();
        }
        exts.put(name,value);
        return this;
    }


    public String getLoginConfig() {
        return loginConfig;
    }

    public void setLoginConfig(String loginConfig) {
        this.loginConfig = loginConfig;
    }

    public int getConfigMode() {
        return configMode;
    }

    public void setConfigMode(int configMode) {
        this.configMode = configMode;
    }

    public String getLoginContextName() {
        return loginContextName;
    }

    public void setLoginContextName(String loginContextName) {
        this.loginContextName = loginContextName;
    }

    public String getServerRealm() {
        return serverRealm;
    }

    public void setServerRealm(String serverRealm) {
        this.serverRealm = serverRealm;
    }

    public String getServerRealmHttpMethod() {
        return serverRealmHttpMethod;
    }

    public void setServerRealmHttpMethod(String serverRealmHttpMethod) {
        this.serverRealmHttpMethod = serverRealmHttpMethod;
    }
}
