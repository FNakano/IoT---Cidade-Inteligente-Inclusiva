package tcc.favorite.resources;

import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsmo.common.TopEntity;
import org.wsmo.service.Goal;

import tcc.favorite.Favorite;
import tcc.keywords.ServerFilenameRepository;
import tcc.models.ResourceModel;
import tcc.wsml.WSMLParser;

@Path("/favorite")
public class FavoriteResource {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getSearchInfo() {	
		String desc = "Shopping ontology search service. Post a JSON with a format {\"query\": <query>}";				
		return desc;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String searchShoppingJSON(ResourceModel queryJSON) {

		if (!queryJSON.getWsmlEntity().equals("goal")) {
			return "Send a goal!";
		}

		Set<Map<Variable, Term>> resp;

		Ontology shopping = WSMLParser.loadOntology(ServerFilenameRepository.SHOPPING_FILE);
		Ontology shoppingProcess = WSMLParser.loadOntology(ServerFilenameRepository.SHOPPING_PROCESS_FILE);
		TopEntity[] entities = WSMLParser.getEntities(queryJSON.getWsmlCode());
		Goal goal = WSMLParser.goalTopEntity(entities);
		Ontology jsonOntology = WSMLParser.ontologyTopEntity(entities);
		resp = Favorite.favoriteLocation(shopping, shoppingProcess, goal, jsonOntology, ServerFilenameRepository.SHOPPING_FILE);

		return Favorite.printJson(resp);
		
	}
	
}
