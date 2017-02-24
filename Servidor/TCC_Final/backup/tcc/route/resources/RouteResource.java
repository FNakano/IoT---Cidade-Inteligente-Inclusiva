package tcc.route.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.omwg.ontology.Ontology;
import org.wsmo.service.Goal;

import tcc.route.Route;
import tcc.route.models.RouteModel;
import tcc.wsml.WSMLParser;


@Path("/route")
public class RouteResource {

	private  final String USERPATH = System.getProperty("user.home");
	private final String GRAPH_ONTOLOGY_FILE = USERPATH+"/.wsml/Shopping/Ontologies/Graph.wsml";
	private final String COMMAND = USERPATH+"/prism-4.3.1/bin/prism";
	private String INPUT_FILE = USERPATH+"/prism-4.3.1/Rotas/exemplo_menor_caminho.pm";
	private String OUTPUT_ADV_FILE = USERPATH+"/prism-4.3.1/Rotas/adv/adv1.adv";
	private final int N_NODES = 6;
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getTrackInJSON() {

		String desc = "adfServiço de rota. Inserir ponto inicial e final.";

		return desc;

	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String createTrackInJSON(RouteModel queryJSON) {
		

		Ontology graphOntology = WSMLParser.loadOntology(GRAPH_ONTOLOGY_FILE);
		Goal goal = WSMLParser.getGoal(queryJSON.getWsmlCode());
		
		if(!WSMLParser.preconditionValidator(graphOntology, goal)){
			return "Pre-conditions not valid!";
		}
		
		//boolean [][] r = Route.findRoute(route.getCOMMAND(), route.getINPUT_FILE(), route.getStart(), route.getEnd(), route.getOUTPUT_ADV_FILE(), route.getN_NODES());
		boolean [][] r = Route.findRoute(COMMAND, INPUT_FILE, queryJSON.getStartNode(), queryJSON.getEndNode(), OUTPUT_ADV_FILE, N_NODES);
		String result = Route.createRouteInfo(r, queryJSON.getStartNode(), queryJSON.getEndNode(), graphOntology);
		return result;
		//return Response.status(201).entity(result).build();
		 
	}

}
