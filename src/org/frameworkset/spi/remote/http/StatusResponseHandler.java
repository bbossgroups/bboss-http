package org.frameworkset.spi.remote.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.frameworkset.spi.remote.http.callback.ExecuteIntercepter;
import org.frameworkset.spi.remote.http.proxy.HttpProxyRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class StatusResponseHandler<T> implements URLResponseHandler<T>{
	private static Logger _logger =  LoggerFactory.getLogger(StatusResponseHandler.class);
	protected int reponseStatus;
	public int getReponseStatus() {
		return reponseStatus;
	}

	public void setReponseStatus(int reponseStatus) {
		this.reponseStatus = reponseStatus;
	}

	protected int initStatus(HttpResponse response){
		reponseStatus = response.getStatusLine().getStatusCode();
		return reponseStatus;
	}
	protected String url;
    private ExecuteIntercepter executeIntercepter;
	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}
    
    public ExecuteIntercepter getExecuteIntercepter(){
        return executeIntercepter;
    }
    public void setExecuteIntercepter(ExecuteIntercepter executeIntercepter){
        this.executeIntercepter = executeIntercepter;
    }

	protected RuntimeException throwException(int status, HttpEntity entity) throws IOException {

		if (entity != null ) {
			if(_logger.isErrorEnabled()) {
				_logger.error(new StringBuilder().append("Request url:").append(url).append(",status:").append(status).toString());
			}
			return new HttpProxyRequestException(EntityUtils.toString(entity));
		}
		else
			return new HttpProxyRequestException(new StringBuilder().append("Request url:").append(url).append(",Unexpected response status: ").append( status).toString());
	}
}
