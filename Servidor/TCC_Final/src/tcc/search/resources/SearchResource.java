package tcc.search.resources;

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

import tcc.keywords.ServerFilenameRepository;
import tcc.models.ResourceModel;
import tcc.search.Search;
import tcc.wsml.WSMLParser;

@Path("/search")
public class SearchResource {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getSearchInfo() {
		String desc = "Shoppng ontology search service. Post a JSON with a format {\"query\": <query>}";
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
		resp = Search.searchShopping(shopping, shoppingProcess, goal, jsonOntology);

		String result = "foi";
		result = Search.printJson(resp);
		return result;
	}

}
