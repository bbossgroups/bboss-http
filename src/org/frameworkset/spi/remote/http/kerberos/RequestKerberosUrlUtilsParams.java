package org.frameworkset.spi.remote.http.kerberos;

import com.frameworkset.util.SimpleStringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.security.Principal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class RequestKerberosUrlUtilsParams extends BaseRequestKerberosUrlUtils{
    public static Logger logger = LoggerFactory.getLogger(RequestKerberosUrlUtilsParams.class);
 
 
 

    public RequestKerberosUrlUtilsParams(KerberosConfig kerberosConfig) {
        super(  kerberosConfig);
       
        configuration = new Configuration() {
            @SuppressWarnings("serial")
            @Override
            public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
                return new AppConfigurationEntry[]{new AppConfigurationEntry("com.sun.security.auth.module.Krb5LoginModule",
                        AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, new HashMap<String, Object>() {
                    {
                        put("useTicketCache", kerberosConfig.getUseTicketCache());
                        if(SimpleStringUtil.isNotEmpty(kerberosConfig.getKeytab()))
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
                        if(kerberosConfig.getExts() != null && !kerberosConfig.getExts().isEmpty()){
                            putAll(kerberosConfig.getExts());
                        }
                    }
                })};
            }
        };
    }


    @Override
    protected Subject getSubject() throws LoginException {
        Set<Principal> princ = new HashSet<Principal>(1);
        princ.add(new KerberosPrincipal(kerberosConfig.getPrincipal()));
        Subject sub = new Subject(false, princ, new HashSet<Object>(), new HashSet<Object>());
        
        
        //认证模块：Krb5Login
        LoginContext lc = new LoginContext(getLoginContextName(), sub, null, configuration);
        lc.login();
        Subject serviceSubject = lc.getSubject();
        return serviceSubject;
       
    }

}
