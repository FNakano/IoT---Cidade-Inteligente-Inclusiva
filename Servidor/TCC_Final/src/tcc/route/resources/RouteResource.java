package tcc.route.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.omwg.ontology.Ontology;
import org.wsmo.common.TopEntity;
import org.wsmo.service.Goal;

import tcc.keywords.ServerFilenameRepository;
import tcc.models.ResourceModel;
import tcc.route.Route;
import tcc.wsml.WSMLParser;


@Path("/route")
public class RouteResource {

	private final int NODES_QUANTITY = 6;
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getTrackInJSON() {

		String desc = "adfServiço de rota. Inserir ponto inicial e final.";

		return desc;

	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String createTrackInJSON(ResourceModel queryJSON) {
		
		if (!queryJSON.getWsmlEntity().equals("goal")) {
			return "Send a goal!";
		}
		
		
		Ontology graphOntology = WSMLParser.loadOntology(ServerFilenameRepository.GRAPH_ONTOLOGY_FILE);
		Ontology shoppingProcess = WSMLParser.loadOntology(ServerFilenameRepository.SHOPPING_PROCESS_FILE);
		TopEntity[] entities = WSMLParser.getEntities(queryJSON.getWsmlCode());
		Goal goal = WSMLParser.goalTopEntity(entities);
		Ontology jsonOntology = WSMLParser.ontologyTopEntity(entities);
		String resp = Route.findRoute(graphOntology, shoppingProcess, goal, jsonOntology, NODES_QUANTITY);
		return resp;
		 
	}

}
