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

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author biaoping.yin
 * @Date 2025/2/16
 */
public class ServerRealmKerberosThreadLocal {
    private static ThreadLocal<Object> authenticateLocal = new ThreadLocal<>();
    private static Object object = new Object();
    public static void setAuthenticateLocal(){
        authenticateLocal.set(object);
    }

    public static void clearAuthenticateLocal(){
        authenticateLocal.set(null);
    }
    
    public static Object getAuthenticateLocal(){
        return authenticateLocal.get();
    }
    

}
