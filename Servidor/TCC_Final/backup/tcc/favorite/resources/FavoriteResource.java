package tcc.favorite.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;
import org.omwg.ontology.Ontology;
import org.wsmo.service.Goal;

import tc.favorite.Favorite;
import tcc.favorite.models.FavoriteModel;
import tcc.wsml.WSMLParser;

@Path("/favorite")
public class FavoriteResource {

	private final String USER_PATH = System.getProperty("user.home");
	private final String SHOPPING_FILE = USER_PATH+"/.wsml/Shopping/Ontologies/Shopping.wsml";


	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getSearchInfo() {	
		String desc = "XxXShoppng ontology search service. Post a JSON with a format {\"query\": <query>}";				
		return desc;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String searchShoppingJSON(FavoriteModel queryJSON) {

		if(!queryJSON.getWsmlEntity().equals("goal")){
			return "Send a goal!";
		}
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("response", "false");

		String result = jsonObj.toString();
		
		Ontology ontology = WSMLParser.loadOntology(SHOPPING_FILE);
		Goal goal = WSMLParser.getGoal(queryJSON.getWsmlCode());
		
		if(!WSMLParser.preconditionValidator(ontology, goal)){
			return result;
		}

		result = Favorite.favoriteLocation(SHOPPING_FILE, ontology, queryJSON.getLocation(), queryJSON.getIsFavorite());
		return result;
		
	}
	
}
