package org.frameworkset.spi.remote.http;

import com.frameworkset.util.SimpleStringUtil;
import org.apache.http.HttpEntity;

import java.io.IOException;
import java.io.InputStream;

import static org.frameworkset.spi.remote.http.ResponseUtil.entityEmpty;

public abstract class BaseResponseHandler extends StatusResponseHandler {


    /**
     * 标记是否在响应对象中放置请求报文，便于在异常处理中放置请求报文数据
     */
    protected boolean enableSetRequestBody;
    protected String requestBody;



    protected boolean truncateLogBody;
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

    public boolean isEnableSetRequestBody() {
        return enableSetRequestBody;
    }

    public void setEnableSetRequestBody(boolean enableSetRequestBody) {
        this.enableSetRequestBody = enableSetRequestBody;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }
    public boolean isTruncateLogBody() {
        return truncateLogBody;
    }

    public void setTruncateLogBody(boolean truncateLogBody) {
        this.truncateLogBody = truncateLogBody;
    }
}
