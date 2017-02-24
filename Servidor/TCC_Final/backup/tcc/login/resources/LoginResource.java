package tcc.login.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;

import tcc.login.LoginCreator;
import tcc.login.models.LoginModel;

@Path("/login")
public class LoginResource {
	private final String USERPATH = System.getProperty("user.home");
	private final String USER_FILE = USERPATH+"/.user-info/info.txt";

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getSearchInfo() {	
		String desc = "login service";				
		return desc;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String searchShoppingJSON(LoginModel sensor) {
		
		
		LoginCreator.saveLogin(sensor.getIp(), sensor.getPort(), USER_FILE);

		JSONObject json = new JSONObject();
		json.put("success", sensor.getIp());
		return json.toString();
		
	}
}
