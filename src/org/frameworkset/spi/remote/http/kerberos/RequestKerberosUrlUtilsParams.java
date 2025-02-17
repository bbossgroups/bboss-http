package org.frameworkset.spi.remote.http.kerberos;

import com.frameworkset.util.SimpleStringUtil;
import org.frameworkset.spi.remote.http.ClientConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

public class RequestKerberosUrlUtilsParams extends BaseRequestKerberosUrlUtils{
    public static Logger logger = LoggerFactory.getLogger(RequestKerberosUrlUtilsParams.class);

 

    public RequestKerberosUrlUtilsParams(KerberosConfig kerberosConfig, ClientConfiguration clientConfiguration) {
        super(  kerberosConfig,  clientConfiguration);
       
        configuration = new Configuration() {
            @SuppressWarnings("serial")
            @Override
            public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
                
                 return KerberosHelper.getAppConfigurationEntry(kerberosConfig);
                 
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
