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

import tcc.search.Search;
import tcc.search.models.SearchModel;
import tcc.wsml.WSMLParser;


@Path("/search")
public class SearchResource {

	private final String USER_PATH = System.getProperty("user.home");
	private final String SHOPPING_FILE = USER_PATH+"/.wsml/Shopping/Ontologies/ShoppingOntology.wsml";
	private final String SHOPPING_PROCESS_FILE = USER_PATH+"/.wsml/Shopping/Ontologies/ShoppingOntologyProcess.wsml";


	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getSearchInfo() {	
		String desc = "XadsfxXShoppng ontology search service. Post a JSON with a format {\"query\": <query>}";				
		return desc;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String searchShoppingJSON(SearchModel queryJSON) {

		if(!queryJSON.getWsmlEntity().equals("goal")){
			return "Send a goal!";
		}

		Set<Map<Variable, Term>> resp;

		Ontology ontology = WSMLParser.loadOntology(SHOPPING_FILE);
		Ontology shoppingProcess = WSMLParser.loadOntology(SHOPPING_PROCESS_FILE);
		Goal goal = WSMLParser.getGoal(queryJSON.getWsmlCode());
		//Ontology goalOntology = WSMLParser.getGoalOntology(queryJSON.getWsmlCode());
		//if(!WSMLParser.preconditionValidator(ontology, goal)){
		//	return "Pre-conditions not valid!";
		//}
		//resp = Search.searchShopping(ontology, goal);
		//String result = Search.printJson(resp);
		//System.out.println(WSMLParser.goalToString(goal));
		//System.out.println("-------------          SHOPPING      -------------------");
		//System.out.println(WSMLParser.ontologyToString(ontology));
		//System.out.println("-------------          SHOPPING      -------------------");
		//System.out.println("-------------          SHOPPING PROCESS      -------------------");
		//System.out.println(WSMLParser.ontologyToString(shoppingProcess));
		//System.out.println("-------------          SHOPPING pROCESS     -------------------");
		System.out.println("-------------          GOAL ONT      -------------------");
		//System.out.println(WSMLParser.ontologyToString(goalOntology));
		System.out.println("-------------          GOAL ONT      -------------------");
		//WSMLParser.parserInfo();
		WSMLParser.printGoalOntology();
		String result = "foi";
		return result;
	}

}
