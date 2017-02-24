package sensors;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class SensorN4 {

	public static void main(String[]args){

		String json = "{\"nodeId\": \"http://www.example.com/modelagem/ontology/Graph#N4\"}";

		System.out.println(json);
		
		try {
			Client client = Client.create();
			WebResource webResource = client
					.resource("http://localhost:8080/TCC_Teste/sensor");
			ClientResponse response = webResource.type(MediaType.APPLICATION_JSON)
					.post(ClientResponse.class, json);
			if (response.getStatus() != 204) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}
		
		} catch (Exception e) {

			e.printStackTrace();
		}
	}
	
}
