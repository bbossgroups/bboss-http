package org.frameworkset.spi.remote.http;

import com.frameworkset.util.SimpleStringUtil;
import org.apache.http.HttpEntity;

import java.io.IOException;
import java.io.InputStream;

import static org.frameworkset.spi.remote.http.HttpRequestProxy.entityEmpty;

public abstract class BaseResponseHandler extends StatusResponseHandler {
	protected <T> T converJson(HttpEntity entity,Class<T> clazz) throws IOException {
		InputStream inputStream = null;
		try {

			inputStream = entity.getContent();

			if(entityEmpty(entity,inputStream)){
				return null;
			}
			return SimpleStringUtil.json2Object(inputStream, clazz);
		}
		finally {
			inputStream.close();
		}
	}
}
