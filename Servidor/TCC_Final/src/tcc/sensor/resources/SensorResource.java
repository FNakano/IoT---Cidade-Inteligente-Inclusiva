package tcc.sensor.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;

import tcc.sensor.ClientMessageSender;
import tcc.sensor.models.SensorModel;


@Path("/sensor")
public class SensorResource {

	private final String USERPATH = System.getProperty("user.home");
	private final String USER_FILE = USERPATH+"/.user-info/info.txt";

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getSearchInfo() {	
		String desc = "sensor only service";				
		return desc;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	//@Produces(MediaType.TEXT_PLAIN)
	public void searchShoppingJSON(SensorModel sensor) {
		
		System.out.println("foi");
		
		JSONObject client = ClientMessageSender.getClient(USER_FILE);
		String ip = client.getString("ip");
		int port = client.getInt("port");
		
		System.out.println(ip+port);
		
		ClientMessageSender.sendClientNodeInfo(ip, port, sensor.getNodeId());
		
	}

}
