package org.frameworkset.spi.remote.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class StringResponseHandler extends StatusResponseHandler<String> implements URLResponseHandler<String> {

	public StringResponseHandler() {
		// TODO Auto-generated constructor stub
	}
	
	 @Override
     public String handleResponse(final HttpResponse response)
             throws ClientProtocolException, IOException {
         int status = initStatus(  response);

         if (org.frameworkset.spi.remote.http.ResponseUtil.isHttpStatusOK( status)) {
             HttpEntity entity = response.getEntity();

             return entity != null ? EntityUtils.toString(entity) : null;
         } else {
             HttpEntity entity = response.getEntity();
//             if (entity != null )
//                 throw new HttpRuntimeException(EntityUtils.toString(entity),status);
//             else
//                 throw new HttpRuntimeException("Unexpected response status: " + status,status);
             throw super.throwException(status,entity);
         }
     }

}
