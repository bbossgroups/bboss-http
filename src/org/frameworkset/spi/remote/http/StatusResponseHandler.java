package org.frameworkset.spi.remote.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.frameworkset.spi.remote.http.proxy.HttpProxyRequestException;

import java.io.IOException;

public abstract class StatusResponseHandler {
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

	public void setUrl(String url) {
		this.url = url;
	}
	protected RuntimeException throwException(int status, HttpEntity entity) throws IOException {
		if (entity != null )
			return new HttpProxyRequestException(new StringBuilder().append("Request url:").append(url).append(",").append(EntityUtils.toString(entity)).toString());
		else
			return new HttpProxyRequestException(new StringBuilder().append("Request url:").append(url).append(",Unexpected response status: ").append( status).toString());
	}
}
