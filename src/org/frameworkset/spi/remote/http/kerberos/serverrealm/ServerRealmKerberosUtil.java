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
    private static final Logger LOG = LoggerFactory.getLogger(ServerRealmClientHelper.class);
    private String serverRealm;
    private Subject subj = null;
    private final Object SUBJECT_LOCK = new Object();
    private ClientConfiguration clientConfiguration; 
    private KerberosConfig kerberosConfig;
    private RefreshTGTSingleton refreshTGTSingleton;
    private String serverRealmPath = "/elasticsearch/serverrealm";

    public ServerRealmKerberosUtil(ClientConfiguration clientConfiguration){
        this.clientConfiguration = clientConfiguration;
        this.kerberosConfig = clientConfiguration.getKerberosConfig();
        if(SimpleStringUtil.isNotEmpty(kerberosConfig.getServerRealmPath()))
            this.serverRealmPath = kerberosConfig.getServerRealmPath();
        if(SimpleStringUtil.isNotEmpty(kerberosConfig.getServerRealm())){
            handleRealm(this.kerberosConfig.getServerRealm());
        }
    }
    private final Map<String, String> KERBEROS_OPTIONS = new HashMap(6);

    public Subject getSubj() {
        return subj;
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

                Object var3;
                try {
                    context = getGssContext(servicePrincipalName);
                    context.requestMutualAuth(true);
                    context.requestCredDeleg(true);
                    byte[] tokenNew = new byte[0];
                    byte[] var15 = context.initSecContext(tokenNew, 0, tokenNew.length);
                    return var15;
                } catch (GSSException var13) {
                    LOG.error("Init secure context failed.", var13);
                    var3 = null;
                } finally {
                    if (context != null) {
                        try {
                            context.dispose();
                        } catch (GSSException var12) {
                            LOG.error("Dispose secure context failed.", var12);
                        }
                    }

                }

                return (byte[])var3;
            }
        });
        return token;
    }
    private void initKerberosOptions(KerberosConfig kerberosConfig) {
        KERBEROS_OPTIONS.clear();
        AppConfigurationEntry[] appConfigurationEntries = getAppConfigurationEntry(kerberosConfig);
        if (appConfigurationEntries != null && appConfigurationEntries.length > 0) {
            AppConfigurationEntry[] var1 = appConfigurationEntries;
            int var2 = appConfigurationEntries.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                AppConfigurationEntry entry = var1[var3];
                KERBEROS_OPTIONS.putAll((Map<String, String>) entry.getOptions());
            }

        } else {
            LOG.error("Can not get ES app configuration entry from jaas conf file.");
            throw new IllegalArgumentException("Can not get ES app configuration entry from jaas conf file.");
        }
    }

    private AppConfigurationEntry[] getAppConfigurationEntry(KerberosConfig kerberosConfig) {
        String jaasAppNameVersionAfter6 = System.getProperty("elasticsearch.kerberos.jaas.appname", "EsClient");
        LOG.info(String.format(Locale.ENGLISH, "Get application configuration entry use by application name %s.", jaasAppNameVersionAfter6));
        AppConfigurationEntry[] entries = readAppConfigurationEntryByAppName(  kerberosConfig);
//        if (entries == null || entries.length <= 0) {
//            entries = readAppConfigurationEntryByAppName("Client");
//        }

        return entries;
    }

    private AppConfigurationEntry[] readAppConfigurationEntryByAppName(KerberosConfig kerberosConfig) {
        LOG.info(String.format(Locale.ENGLISH, "Try to read the jaas configuration entry again, app name is %s.", kerberosConfig.getLoginContextName()));
        AppConfigurationEntry[] entries = KerberosHelper.getAppConfigurationEntry(kerberosConfig);;
        LOG.info("Read application configuration entry from Es jaas conf file.");
//        if (esJaasConfFile != null && !esJaasConfFile.isEmpty()) {
//            entries = readAppConfigurationEntryFromFile(esJaasConfFile, appName);
//            LOG.info(String.format(Locale.ENGLISH, " Complete to read from Es jaas conf file, app name is %s.", appName));
//        } else {
//            LOG.warn("Fail to get application configuration entry from from Es jaas conf file, because Es jaas conf file path is empty.");
//        }
//
//        if (entries == null || entries.length <= 0) {
//            LOG.warn("Fail to get application configuration entry from esJaasConfFile.");
//            LOG.info(String.format(Locale.ENGLISH, "Get application configuration entry use by application name %s from memory.", appName));
//            entries = Configuration.getConfiguration().getAppConfigurationEntry(appName);
//        }
//
//        String securityAuthConfig = System.getProperty("java.security.auth.login.config");
//        if ((entries == null || entries.length <= 0) && securityAuthConfig != null && !securityAuthConfig.isEmpty()) {
//            LOG.info(String.format(Locale.ENGLISH, "Get application configuration entry from %s.", "java.security.auth.login.config"));
//            entries = readAppConfigurationEntryFromFile(securityAuthConfig, appName);
//        }
//
//        if (entries == null || entries.length <= 0) {
//            LOG.error(String.format(Locale.ENGLISH, "Failed to read the jaas configuration entry user by application name %s.", appName));
//        }

        return entries;
    }

 
    private synchronized KerberosTicket getKerberosTicket(Subject subject) {
        KerberosTicket kerberosTicket = null;
        if (null == subject) {
            LOG.debug("The subject is invalid.");
            return null;
        } else {
            Set<Object> privateCredentials = subject.getPrivateCredentials();
            if (null == privateCredentials) {
                LOG.debug("The privateCredentials is null.");
                return null;
            } else {
                Iterator var3 = privateCredentials.iterator();

                Object privateCredential;
                do {
                    if (!var3.hasNext()) {
                        return kerberosTicket;
                    }

                    privateCredential = var3.next();
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
                LOG.debug("The kerberosTicket is null.");
                return true;
            } else {
                long tgtWillExpireTime = null == kerberosTicket.getEndTime() ? -1L : kerberosTicket.getEndTime().getTime();
                long tgtValidityPeriod = getTgtValidityPeriod(kerberosTicket);
                if (tgtWillExpireTime > 0L && tgtWillExpireTime >= System.currentTimeMillis() && tgtValidityPeriod > 0L) {
                    boolean willExpired = (double)(tgtWillExpireTime - System.currentTimeMillis()) < (double)tgtValidityPeriod * 0.25;
                    if (willExpired) {
                        LOG.debug("TGT will expire!");
                    }

                    return willExpired;
                } else {
                    LOG.debug("TgtWillExpireTime is invalid.");
                    return true;
                }
            }
        } else {
            LOG.debug("The subject is invalid.");
            return true;
        }
    }


    public synchronized void getTGT() {
        try {
            if (KERBEROS_OPTIONS.isEmpty()) {
                initKerberosOptions(kerberosConfig);
                if (KERBEROS_OPTIONS.isEmpty()) {
                    LOG.error("Please generate EsClient loginContext in jaas.conf file for ES to get TGT.");
                    throw new IllegalArgumentException("EsClient loginContext is not configured properly in jaas.conf file,please set the correct content.");
                }
            }

            Subject loginSubject = new Subject();
            boolean isIbmJdk = System.getProperty("java.vendor").contains("IBM");
            Class<?> clazz = null;
            String className = null;
            if (isIbmJdk) {
                className = "com.ibm.security.auth.module.Krb5LoginModule";
                LOG.info("JDK version is IBM");
            } else {
                className = "com.sun.security.auth.module.Krb5LoginModule";
                LOG.info("JDK version is SUN");
            }

            clazz = Class.forName(className);
            Method initialize = clazz.getDeclaredMethod("initialize", Subject.class, CallbackHandler.class, Map.class, Map.class);
            Method login = clazz.getDeclaredMethod("login");
            Method commit = clazz.getDeclaredMethod("commit");
            Object krb5LoginModule = clazz.newInstance();
            initialize.invoke(krb5LoginModule, loginSubject, null, null, KERBEROS_OPTIONS);
            login.invoke(krb5LoginModule);
            commit.invoke(krb5LoginModule);
            subj = loginSubject;
            LOG.info("Get kerberos TGT successfully.");
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException | ClassNotFoundException var8) {
            LOG.error("Get kerberos TGT failed." + var8);
            throw new RuntimeException(var8);
        }
    }
    private long getRefreshTime(KerberosTicket tgt) {
        long start = tgt.getStartTime().getTime();
        long expires = tgt.getEndTime().getTime();
        LOG.info("TGT valid starting at:        " + tgt.getStartTime().toString());
        LOG.info("TGT expires:                  " + tgt.getEndTime().toString());
        long proposedRefresh = start + (long)((float)(expires - start) * 0.8F);
        return proposedRefresh > expires ? System.currentTimeMillis() : proposedRefresh;
    }
    public long getNextRefreshTime(long localCurrentTime, Subject subj) throws Exception {
        KerberosTicket kerberosTicket = getKerberosTicket(subj);
        if (kerberosTicket == null) {
            Date nextRefreshDate = new Date(localCurrentTime);
            LOG.warn("No TGT found: will try again at {}" + nextRefreshDate);
            return localCurrentTime;
        } else {
            long nextRefreshTime = getRefreshTime(kerberosTicket);
            long expiry = kerberosTicket.getEndTime().getTime();
            Date expiryDate = new Date(expiry);
            if (kerberosTicket.getEndTime().equals(kerberosTicket.getRenewTill())) {
                LOG.error("The TGT cannot be renewed beyond the next expiry date: " + expiryDate + ".This process will not be able to authenticate new SASL connections after that time (for example, it will not be authenticate a new connection with a Zookeeper Quorum member).  Ask your system administrator to either increase the 'renew until' time by doing : 'modprinc -maxrenewlife username within kadmin, or instead, to generate a keytab for username. Because the TGT's expiry cannot be further extended by refreshing");
                throw new Exception("The TGT cannot be renewed beyond the next expiry date: " + expiryDate);
            } else {
                if (nextRefreshTime < localCurrentTime + 60000L) {
                    Date until = new Date(nextRefreshTime);
                    Date newuntil = new Date(localCurrentTime + 60000L);
                    nextRefreshTime = localCurrentTime + 60000L;
                    LOG.warn("TGT refresh thread time adjusted from : " + until + " to : " + newuntil + " since the former is sooner than the minimum refresh interval (" + 60L + " seconds) from now.");
                }

                if (nextRefreshTime > expiry) {
                    LOG.info("refreshing now because expiry is before next scheduled refresh time.");
                    return localCurrentTime;
                } else {
                    return nextRefreshTime;
                }
            }
        }
    }

    private void getTGTwithRetry() throws InterruptedException {
        int count = 1;

        while(count <= 3) {
            try {
                getTGT(  );
                LOG.info("TGT refresh at: " + new Date(System.currentTimeMillis()));
                break;
            } catch (Exception var2) {
                if (count < 3) {
                    ++count;
                    Thread.sleep(10000L);
                } else {
                    LOG.error("Could not refresh TGT ", var2);
                }
            }
        }

    }

    public void close() {
        if(refreshTGTSingleton != null)
            refreshTGTSingleton.shutdown();
    }

    public class RefreshTgtThread implements Runnable 
    {
        private KerberosConfig kerberosConfig;
        RefreshTgtThread(KerberosConfig kerberosConfig) {
            this.kerberosConfig = kerberosConfig;
        }

        public void run() {
            LOG.info("TGT refresh thread started");

            while(true) {
                while(true) {
                    try {
                        long localCurrentTime = System.currentTimeMillis();
                        long nextRefresh = getNextRefreshTime(localCurrentTime, subj);
                        if (localCurrentTime <= nextRefresh) {
                            Date until = new Date(nextRefresh);
                            LOG.info("TGT refresh sleeping until: " + until.toString());
                            Thread.sleep(nextRefresh - localCurrentTime);
                        } else {
                            LOG.warn("nextRefresh:" + new Date(nextRefresh) + " is in the past: exiting refresh thread. Check clock sync between this host and KDC - (KDC's clock is likely ahead of this host). Manual intervention will be required for this client to successfully authenticate. In case of TGT being expiring, try to refresh TGT right now.");
                        }

                        getTGTwithRetry();
                    } catch (Exception var6) {
                        LOG.error("Failed to refresh TGT: refresh thread exiting now.", var6);
                    }
                }
            }
        }
    }
    public class RefreshTGTSingleton {
        private volatile ExecutorService esService;
        private KerberosConfig kerberosConfig;
        private RefreshTGTSingleton(  KerberosConfig kerberosConfig) {
            this.kerberosConfig = kerberosConfig;
        }
        public void shutdown(){
            if(esService != null){
                try {
                    esService.shutdown();
                }
                catch (Exception e){
                    LOG.warn("Shutdown kerberos RefreshTGTThread ThreadPool failed:",e);
                }
                
            }
        }

        public void startRefreshThread(  ) {
            if (esService == null) {
                synchronized(RefreshTGTSingleton.class) {
                    if (esService == null) {
                        esService = Executors.newFixedThreadPool(1, new ThreadFactory() {
                            public Thread newThread(Runnable r) {
                                Thread t = Executors.defaultThreadFactory().newThread(r);
                                t.setDaemon(true);
                                t.setName("RefreshTGTThread");
                                return t;
                            }
                        });
                    }

                    esService.submit(new RefreshTgtThread(kerberosConfig));
                }
            }

        }
    }
    public void authenticate() {
        int times = 0;
        synchronized(SUBJECT_LOCK) {
            if(refreshTGTSingleton == null){
                refreshTGTSingleton = new RefreshTGTSingleton(kerberosConfig);
                refreshTGTSingleton.startRefreshThread();
            }
            while(subjectWillExpire(subj) && times < 3) {
                LOG.debug("Subject is not ok ,retry get new TGT.");
                getTGT();
                ++times;
            }
        }
        if(SimpleStringUtil.isNotEmpty(serverRealm)){
            return;
        }
        
//        int index = 0;
//        int times1 = 0;
        String realm = null;
        try {
            ServerRealmKerberosThreadLocal.setAuthenticateLocal();
            realm = HttpRequestProxy.httpGetforString(clientConfiguration, this.serverRealmPath);
        }
        finally {
            ServerRealmKerberosThreadLocal.clearAuthenticateLocal();
        }
//        for(realm = null; null == realm && times1 < 3; ++times1) {
//            realm = this.getRealm(((Node)this.nodes.get(index)).getHost().toHostString());
//            int var10000;
//            if (index < this.nodes.size() - 1) {
//                ++index;
//                var10000 = index;
//            } else {
//                var10000 = 0;
//            }
//
//            index = var10000;
//        }

        if (realm != null && !realm.isEmpty()) {
//            if (realm.toLowerCase(Locale.ENGLISH).startsWith("elasticsearch/hadoop.")) {
//                this.serverRealm = realm;
//            } else {
//                this.serverRealm = "elasticsearch/hadoop." + realm.toLowerCase(Locale.ENGLISH) + "@" + realm.toUpperCase(Locale.ENGLISH);
//            }
            handleRealm(realm);
             
            LOG.info("Initialize the client successfully.");
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
