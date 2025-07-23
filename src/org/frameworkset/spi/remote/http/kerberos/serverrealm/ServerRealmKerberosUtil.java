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

import com.frameworkset.util.SimpleStringUtil;
import org.frameworkset.spi.remote.http.ClientConfiguration;
import org.frameworkset.spi.remote.http.HttpMethodName;
import org.frameworkset.spi.remote.http.HttpRequestProxy;
import org.frameworkset.spi.remote.http.kerberos.KerberosConfig;
import org.frameworkset.spi.remote.http.kerberos.KerberosHelper;
import org.ietf.jgss.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.kerberos.KerberosTicket;
import javax.security.auth.login.AppConfigurationEntry;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.PrivilegedAction;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author biaoping.yin
 * @Date 2025/2/16
 */
public class ServerRealmKerberosUtil {
    private static final Logger logger = LoggerFactory.getLogger(ServerRealmKerberosUtil.class);
    private String serverRealm;
    private Subject subject = null;
    private final Object subjectLock = new Object();
    private ClientConfiguration clientConfiguration; 
    private KerberosConfig kerberosConfig;
    private RefreshTgtTool refreshTgtTool;
    private String serverRealmPath = "/elasticsearch/serverrealm";
    private String serverRealmHttpMethod = HttpMethodName.HTTP_GET;

    public ServerRealmKerberosUtil(ClientConfiguration clientConfiguration){
        this.clientConfiguration = clientConfiguration;
        this.kerberosConfig = clientConfiguration.getKerberosConfig();
        if(SimpleStringUtil.isNotEmpty(kerberosConfig.getServerRealmPath()))
            this.serverRealmPath = kerberosConfig.getServerRealmPath();
        if(SimpleStringUtil.isNotEmpty(kerberosConfig.getServerRealmHttpMethod())){
            this.serverRealmHttpMethod = kerberosConfig.getServerRealmHttpMethod();
        }
        if(SimpleStringUtil.isNotEmpty(kerberosConfig.getServerRealm())){
            handleRealm(this.kerberosConfig.getServerRealm());
        }
        

    }
    private final Map<String, String> kerberosOptions = new HashMap(6);

    public Subject getSubject() {
        return subject;
    }

    public String getServerRealm() {
        return serverRealm;
    }
    private GSSCredential credentials = null;

    public void setCredentials(GSSCredential credentials) {
        this.credentials = credentials;
    }

    private GSSContext getGssContext(String servicePrincipalName) throws GSSException {
        GSSManager manager = GSSManager.getInstance();
        GSSName serverName = manager.createName(servicePrincipalName, new Oid("1.2.840.113554.1.2.2.1"));
        Oid oid = new Oid("1.3.6.1.5.5.2");
        GSSContext context = manager.createContext(serverName.canonicalize(oid), oid, credentials, 0);
        return context;
    }

    public synchronized byte[] initiateSecurityContext(Subject subject, final String servicePrincipalName) {
        byte[] token = (byte[])Subject.doAs(subject, new PrivilegedAction<byte[]>() {
            public byte[] run() {
                GSSContext context = null;

                try {
                    context = getGssContext(servicePrincipalName);
                    context.requestMutualAuth(true);
                    context.requestCredDeleg(true);
                    byte[] tokenNew = new byte[0];
                    byte[] token = context.initSecContext(tokenNew, 0, tokenNew.length);
                    return token;
                } catch (GSSException gssException) {
                    if(logger.isErrorEnabled())
                        logger.error("Init secure context failed.", gssException);
                } finally {
                    if (context != null) {
                        try {
                            context.dispose();
                        } catch (GSSException gssException) {
                            if(logger.isErrorEnabled())
                                logger.error("Dispose secure context failed.", gssException);
                        }
                    }

                }

                return (byte[])null;
            }
        });
        return token;
    }
    private void initKerberosOptions(KerberosConfig kerberosConfig) {
        kerberosOptions.clear();
        AppConfigurationEntry[] appConfigurationEntries = getAppConfigurationEntry(kerberosConfig);
        if (appConfigurationEntries != null && appConfigurationEntries.length > 0) {
            int length = appConfigurationEntries.length;

            for(int i = 0; i < length; ++i) {
                AppConfigurationEntry entry = appConfigurationEntries[i];
                kerberosOptions.putAll((Map<String, String>) entry.getOptions());
            }

        } else {
            if(logger.isErrorEnabled())
                logger.error("Can not get kerberos app configuration entry from jaas conf file.");
            throw new IllegalArgumentException("Can not get kerberos app configuration entry from jaas conf file.");
        }
    }

 

    private AppConfigurationEntry[] getAppConfigurationEntry(KerberosConfig kerberosConfig) {
        if(logger.isInfoEnabled())
            logger.info(String.format(Locale.ENGLISH, "Try to read the jaas configuration entry again, app name is %s.", kerberosConfig.getLoginContextName()));
        AppConfigurationEntry[] entries = KerberosHelper.getAppConfigurationEntry(kerberosConfig);;
        if(logger.isInfoEnabled())
            logger.info("Read application configuration entry from kerberos jaas conf file.");

        return entries;
    }

 
    private synchronized KerberosTicket getKerberosTicket(Subject subject) {
        KerberosTicket kerberosTicket = null;
        if (null == subject) {
            if(logger.isDebugEnabled())
                logger.debug("The subject is invalid.");
            return null;
        } else {
            Set<Object> privateCredentials = subject.getPrivateCredentials();
            if (null == privateCredentials) {
                if(logger.isDebugEnabled())
                    logger.debug("The privateCredentials is null.");
                return null;
            } else {
                Iterator iterator = privateCredentials.iterator();

                Object privateCredential;
                do {
                    if (!iterator.hasNext()) {
                        return kerberosTicket;
                    }

                    privateCredential = iterator.next();
                } while(!(privateCredential instanceof KerberosTicket));

                kerberosTicket = (KerberosTicket)privateCredential;
                return kerberosTicket;
            }
        }
    }

    private long getTgtValidityPeriod(KerberosTicket kerberosTicket) {
        Date endTime = kerberosTicket.getEndTime();
        Date startTime = kerberosTicket.getStartTime();
        return null != endTime && null != startTime ? endTime.getTime() - startTime.getTime() : -1L;
    }
    public synchronized boolean subjectWillExpire(Subject subject) {
        if (null != subject && null != subject.getPrincipals() && null != subject.getPrivateCredentials()) {
            KerberosTicket kerberosTicket = getKerberosTicket(subject);
            if (null == kerberosTicket) {
                if(logger.isDebugEnabled())
                    logger.debug("The kerberosTicket is null.");
                return true;
            } else {
                long tgtWillExpireTime = null == kerberosTicket.getEndTime() ? -1L : kerberosTicket.getEndTime().getTime();
                long tgtValidityPeriod = getTgtValidityPeriod(kerberosTicket);
                if (tgtWillExpireTime > 0L && tgtWillExpireTime >= System.currentTimeMillis() && tgtValidityPeriod > 0L) {
                    boolean willExpired = (double)(tgtWillExpireTime - System.currentTimeMillis()) < (double)tgtValidityPeriod * 0.25;
                    if (willExpired) {
                        if(logger.isDebugEnabled())
                            logger.debug("TGT will expire!");
                    }

                    return willExpired;
                } else {
                    if(logger.isDebugEnabled())
                        logger.debug("TgtWillExpireTime is invalid.");
                    return true;
                }
            }
        } else {
            if(logger.isDebugEnabled())
                logger.debug("The subject is invalid.");
            return true;
        }
    }


    public synchronized void getTGT() {
        try {
            if (kerberosOptions.isEmpty()) {
                initKerberosOptions(kerberosConfig);
                if (kerberosOptions.isEmpty()) {
                    if(logger.isErrorEnabled())
                        logger.error("Please generate KerberosClient loginContext in jaas.conf file for kerberos to get TGT.");
                    throw new IllegalArgumentException("KerberosClient loginContext is not configured properly in jaas.conf file,please set the correct content.");
                }
            }

            Subject loginSubject = new Subject();
            boolean isIbmJdk = System.getProperty("java.vendor").contains("IBM");
            Class<?> clazz = null;
            String className = null;
            if (isIbmJdk) {
                className = "com.ibm.security.auth.module.Krb5LoginModule";
                if(logger.isInfoEnabled())
                    logger.info("JDK version is IBM");
            } else {
                className = "com.sun.security.auth.module.Krb5LoginModule";
                if(logger.isInfoEnabled())
                    logger.info("JDK version is SUN");
            }

            clazz = Class.forName(className);
            Method initialize = clazz.getDeclaredMethod("initialize", Subject.class, CallbackHandler.class, Map.class, Map.class);
            Method login = clazz.getDeclaredMethod("login");
            Method commit = clazz.getDeclaredMethod("commit");
            Object krb5LoginModule = clazz.newInstance();
            initialize.invoke(krb5LoginModule, loginSubject, null, null, kerberosOptions);
            login.invoke(krb5LoginModule);
            commit.invoke(krb5LoginModule);
            subject = loginSubject;
            if(logger.isInfoEnabled())
                logger.info("Get kerberos TGT successfully.");
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException | ClassNotFoundException exception) {
            if(logger.isErrorEnabled())
                logger.error("Get kerberos TGT failed." , exception);
            throw new RuntimeException(exception);
        }
    }
    private long getRefreshTime(KerberosTicket tgt) {
        long start = tgt.getStartTime().getTime();
        long expires = tgt.getEndTime().getTime();
        // 定义日期时间格式化器
        DateFormat formatter = getDateFormat();
        if(logger.isInfoEnabled())
            logger.info("TGT valid starting at:        {}\r\nTGT expires:                  {}" , formatter.format(tgt.getStartTime()),formatter.format(tgt.getEndTime()));
        long proposedRefresh = start + (long)((float)(expires - start) * 0.8F);
        return proposedRefresh > expires ? System.currentTimeMillis() : proposedRefresh;
    }
    private DateFormat getDateFormat(){
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    }
    public long getNextRefreshTime(long localCurrentTime, Subject subj) throws Exception {
        KerberosTicket kerberosTicket = getKerberosTicket(subj);
        DateFormat formatter = getDateFormat();
        if (kerberosTicket == null) {
            Date nextRefreshDate = new Date(localCurrentTime);
            if(logger.isWarnEnabled())
                logger.warn("No TGT found: will try again at {}" , formatter.format(nextRefreshDate));
            return localCurrentTime;
        } else {
            long nextRefreshTime = getRefreshTime(kerberosTicket);
            long expiry = kerberosTicket.getEndTime().getTime();
            Date expiryDate = new Date(expiry);
            if (kerberosTicket.getEndTime().equals(kerberosTicket.getRenewTill())) {
                String timeStr = formatter.format(expiryDate);
                if(logger.isErrorEnabled())
                    logger.error("The TGT cannot be renewed beyond the next expiry date: {}.This process will not be able to authenticate new SASL connections after that time (for example, it will not be authenticate a new connection with a Zookeeper Quorum member).  Ask your system administrator to either increase the 'renew until' time by doing : 'modprinc -maxrenewlife username within kadmin, or instead, to generate a keytab for username. Because the TGT's expiry cannot be further extended by refreshing",timeStr);
                throw new Exception("The TGT cannot be renewed beyond the next expiry date: " + timeStr);
            } else {
                if (nextRefreshTime < localCurrentTime + 60000L) {
                    Date until = new Date(nextRefreshTime);
                    Date newuntil = new Date(localCurrentTime + 60000L);
                    nextRefreshTime = localCurrentTime + 60000L;
                    if(logger.isWarnEnabled())
                        logger.warn("TGT refresh thread time adjusted from : {} to : {} since the former is sooner than the minimum refresh interval ({} seconds) from now.",  formatter.format(until) ,formatter.format(newuntil),60L );
                }

                if (nextRefreshTime > expiry) {
                    if(logger.isInfoEnabled())
                        logger.info("refreshing now because expiry is before next scheduled refresh time.");
                    return localCurrentTime;
                } else {
                    return nextRefreshTime;
                }
            }
        }
    }

    private void getTGTwithRetry() throws InterruptedException {
        int count = 1;
        DateFormat formatter = getDateFormat();
        while(count <= 3) {
            try {
                getTGT(  );
                if(logger.isInfoEnabled())
                    logger.info("TGT refresh at: {}", formatter.format(new Date(System.currentTimeMillis())));
                break;
            } catch (Exception var2) {
                if (count < 3) {
                    ++count;
                    Thread.sleep(10000L);
                } else {
                    if(logger.isErrorEnabled())
                        logger.error("Could not refresh TGT ", var2);
                }
            }
        }

    }

    public void close() {
        if(refreshTgtTool != null)
            refreshTgtTool.shutdown();
    }

    public class RefreshTgtThread implements Runnable 
    {
        private KerberosConfig kerberosConfig;
        RefreshTgtThread(KerberosConfig kerberosConfig) {
            this.kerberosConfig = kerberosConfig;
        }

        public void run() {
            if(logger.isInfoEnabled())
                logger.info("TGT refresh thread started");
            DateFormat formatter = getDateFormat();
            while(true) {
                while(true) {
                    try {
                        long localCurrentTime = System.currentTimeMillis();
                        long nextRefresh = getNextRefreshTime(localCurrentTime, subject);
                        if (localCurrentTime <= nextRefresh) {
                            Date until = new Date(nextRefresh);
                            if(logger.isInfoEnabled())
                                logger.info("TGT refresh sleeping until: {}", formatter.format(until));
                            Thread.sleep(nextRefresh - localCurrentTime);
                        } else {
                            if(logger.isWarnEnabled())
                                logger.warn("nextRefresh:{} is in the past: exiting refresh thread. Check clock sync between this host and KDC - (KDC's clock is likely ahead of this host). Manual intervention will be required for this client to successfully authenticate. In case of TGT being expiring, try to refresh TGT right now.",formatter.format(new Date(nextRefresh)));
                        }

                        getTGTwithRetry();
                    } catch (Exception exception) {
                        if(logger.isErrorEnabled())
                            logger.error("Failed to refresh TGT: refresh thread exiting now.", exception);
                    }
                }
            }
        }
    }
    public class RefreshTgtTool {
        private volatile ExecutorService refreshTgtService;
        private KerberosConfig kerberosConfig;
        private RefreshTgtTool(KerberosConfig kerberosConfig) {
            this.kerberosConfig = kerberosConfig;
        }
        public void shutdown(){
            if(refreshTgtService != null){
                try {
                    refreshTgtService.shutdown();
                }
                catch (Exception e){
                    if(logger.isWarnEnabled())
                        logger.warn("Shutdown kerberos RefreshTGTThread ThreadPool failed:",e);
                }
                
            }
        }

        public void startRefreshThread(  ) {
            if (refreshTgtService == null) {
                synchronized(RefreshTgtTool.class) {
                    if (refreshTgtService == null) {
                        refreshTgtService = Executors.newFixedThreadPool(1, new ThreadFactory() {
                            public Thread newThread(Runnable r) {
                                Thread t = Executors.defaultThreadFactory().newThread(r);
                                t.setDaemon(true);
                                t.setName("RefreshTGTThread");
                                return t;
                            }
                        });
                    }

                    refreshTgtService.submit(new RefreshTgtThread(kerberosConfig));
                }
            }

        }
    }
    public void authenticate() {
        int times = 0;
        synchronized(subjectLock) {
            if(refreshTgtTool == null){
                refreshTgtTool = new RefreshTgtTool(kerberosConfig);
                refreshTgtTool.startRefreshThread();
            }
            while(subjectWillExpire(subject) && times < 3) {
                if(logger.isDebugEnabled())
                    logger.debug("Subject is not ok ,retry get new TGT.");
                getTGT();
                ++times;
            }
        }
        if(SimpleStringUtil.isNotEmpty(serverRealm)){
            return;
        }
        

        String realm = null;
        try {
            ServerRealmKerberosThreadLocal.setAuthenticateLocal();
            if(SimpleStringUtil.isEmpty(serverRealmHttpMethod ) 
                    || serverRealmHttpMethod.equalsIgnoreCase(HttpMethodName.HTTP_GET)) {
                realm = HttpRequestProxy.httpGetforString(clientConfiguration, this.serverRealmPath);
            }           
            else  if(serverRealmHttpMethod.equalsIgnoreCase(HttpMethodName.HTTP_POST)) {
                realm = HttpRequestProxy.httpPostforString(clientConfiguration, this.serverRealmPath);
            }
            else {
                realm = HttpRequestProxy.httpGetforString(clientConfiguration, this.serverRealmPath);
            }
        }
        finally {
            ServerRealmKerberosThreadLocal.clearAuthenticateLocal();
        }


        if (realm != null && !realm.isEmpty()) {
            handleRealm(realm);

            if(logger.isInfoEnabled())
                logger.info("Initialize the client successfully.");
        } else {
            throw new IllegalArgumentException("Get ServerRealm failed.");
        }
    }
    
    private void handleRealm(String realm){
        if (realm.toLowerCase(Locale.ENGLISH).indexOf("@") > 0) {
            this.serverRealm = realm;
        }
        else if (realm.toLowerCase(Locale.ENGLISH).startsWith("elasticsearch/hadoop.")) { //适配华为elasticsearch
            this.serverRealm = realm;
        }
        else { //适配华为elasticsearch
            this.serverRealm = "elasticsearch/hadoop." + realm.toLowerCase(Locale.ENGLISH) + "@" + realm.toUpperCase(Locale.ENGLISH);
        }
    }

}
