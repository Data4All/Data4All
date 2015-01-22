package io.github.data4all.network;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;

public class OsmApiHelper {
	
	
	
	
	
	
	public static void requestChangeSetID(){
		
		
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPut request = new HttpPut (  "/api/0.6/changeset/create");
		
        try {
			HttpResponse response = httpClient.execute(request);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
