package org.frameworkset.spi.remote.http.kerberos.hw;
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

import org.apache.http.Header;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.message.AbstractHttpMessage;
import org.frameworkset.spi.remote.http.ClientConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author biaoping.yin
 * @Date 2025/2/16
 */
public class HWClientHelper {
    private Logger logger = LoggerFactory.getLogger(HWClientHelper.class);
    private String cookieNow;
    private Object cookieLock = new Object();
    private long tokenValidityPeriod;
    private long tokenWillExpireTime;
    private long createSTTime;
    private HWKerberosUtil hwKerberosUtil;
    public HWClientHelper(ClientConfiguration clientConfiguration){
        hwKerberosUtil = new HWKerberosUtil(clientConfiguration);
    }
    public void authenticate() {
        hwKerberosUtil.authenticate();
    }
    public String getCookie() {
        synchronized (cookieLock) {
            return cookieNow;
        }
    }
    public Subject getSubj() {
        return hwKerberosUtil.getSubj();
    }
    public void setCookie(String cookie) {
        synchronized (cookieLock) {
            this.cookieNow = cookie;
        }
    }

    private long getTokenExpireTime(String cookie) {
        long expireTime = -1L;
        if (null == cookie) {
            logger.debug("Cookie is null.");
            return expireTime;
        } else {
            String[] cookieSplit = cookie.split("&e=");
            if (cookieSplit.length < 2) {
                logger.error("Cookie format is wrong.");
                return expireTime;
            } else {
                try {
                    expireTime = Long.parseLong(cookieSplit[1].split("&s")[0]);
                } catch (Exception var6) {
                    logger.error("Cookie format is wrong," + var6);
                }

                return expireTime;
            }
        }
    }

    public void setCookie(HttpResponse response) {
        Header header = response.getFirstHeader("set-cookie");
        String cookie = header == null ? null : header.getValue(); 
        if (cookie != null && !cookie.isEmpty()) {
            this.tokenWillExpireTime = this.getTokenExpireTime(cookie);
            if (System.currentTimeMillis() < this.tokenWillExpireTime - 30000L) {
                this.setCookie(cookie);
            }

            this.tokenValidityPeriod = this.tokenWillExpireTime - this.createSTTime;
        }
    }

    private boolean tokenWillExpire() {
        if (this.tokenValidityPeriod > 0L && this.tokenWillExpireTime > 0L && this.tokenWillExpireTime > System.currentTimeMillis()) {
            double leftTime = (double)this.tokenValidityPeriod * 0.25;
            double tokenLeftTime = 7200000.0 > leftTime ? 7200000.0 : leftTime;
            boolean tokenExpire = (double)(this.tokenWillExpireTime - System.currentTimeMillis()) < tokenLeftTime;
            if (tokenExpire) {
                logger.debug("Token will expire.");
            }

            return tokenExpire;
        } else {
            return true;
        }
    }
    public void addSecurityHeaders(HttpMessage abstractHttpMessage) {
       

            String cookieNow = this.getCookie();
            if (cookieNow != null && !this.tokenWillExpire()) {
                abstractHttpMessage.addHeader("Authorization", "Negotiate ");
                abstractHttpMessage.addHeader("cookie", cookieNow);
                logger.debug("Success to add securityToken and cookie in request header.");
            } else {
                String tokenNew = this.getNewToken();
                abstractHttpMessage.removeHeaders("Authorization");
                abstractHttpMessage.removeHeaders("cookie");
                abstractHttpMessage.addHeader("Authorization", "Negotiate " + tokenNew);
                logger.debug("Success to add new securityToken in request header.");
            }
        

    }

    private String getNewToken() {
        byte[] tokenN = hwKerberosUtil.initiateSecurityContext(hwKerberosUtil.getSubj(), this.hwKerberosUtil.getServerRealm());

        for(int times = 0; null == tokenN && times < 3; ++times) {
            logger.debug("InitiateSecurityContext again.");
            tokenN = hwKerberosUtil.initiateSecurityContext(hwKerberosUtil.getSubj(), this.hwKerberosUtil.getServerRealm());
        }

        if (null == tokenN) {
            throw new IllegalArgumentException("Get security token failed.");
        } else {
            this.createSTTime = System.currentTimeMillis();
            return new String(Base64.getEncoder().encode(tokenN), StandardCharsets.UTF_8);
        }
    }



}
