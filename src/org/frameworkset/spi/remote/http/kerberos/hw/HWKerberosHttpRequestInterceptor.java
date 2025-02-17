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

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author biaoping.yin
 * @Date 2025/2/16
 */
public class HWKerberosHttpRequestInterceptor implements HttpRequestInterceptor {
    private HWClientHelper hwClientHelper;
    public HWKerberosHttpRequestInterceptor(HWClientHelper hwClientHelper){
        this.hwClientHelper = hwClientHelper;
    }
    /**
     * Processes a request.
     * On the client side, this step is performed before the request is
     * sent to the server. On the server side, this step is performed
     * on incoming messages before the message body is evaluated.
     *
     * @param request the request to preprocess
     * @param context the context for the request
     * @throws HttpException in case of an HTTP protocol violation
     * @throws IOException   in case of an I/O error
     */
    @Override
    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        if(HWKerberosThreadLocal.getAuthenticateLocal() == null) { // 安全接口无需添加认证头
            hwClientHelper.addSecurityHeaders(request);
        }
    }
}
