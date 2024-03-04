package org.frameworkset.spi.remote.http.proxy;
/**
 * Copyright 2024 bboss
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

import org.apache.http.Consts;
import org.apache.http.entity.ContentType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author biaoping.yin
 * @Date 2024/3/3
 */
public class BBossContentType {

    private static final Map<String, ContentType> CONTENT_TYPE_MAP;
    public static final ContentType EVENT_STREAM = ContentType.create(
            "text/event-stream", Consts.UTF_8);
    static {

        final ContentType[] contentTypes = {
                EVENT_STREAM };
        final HashMap<String, ContentType> map = new HashMap<String, ContentType>();
        for (final ContentType contentType: contentTypes) {
            map.put(contentType.getMimeType(), contentType);
        }
        CONTENT_TYPE_MAP = Collections.unmodifiableMap(map);
    }


    /**
     * Returns {@code Content-Type} for the given MIME type.
     *
     * @param mimeType MIME type
     * @return content type or {@code null} if not known.
     *
     * @since 4.5
     */
    public static ContentType getByMimeType(final String mimeType) {
        if (mimeType == null) {
            return null;
        }
        ContentType defaultContentType = ContentType.getByMimeType(mimeType);
        if(defaultContentType == null){
            defaultContentType = CONTENT_TYPE_MAP.get(mimeType);
        }
        return defaultContentType;
    }

}
