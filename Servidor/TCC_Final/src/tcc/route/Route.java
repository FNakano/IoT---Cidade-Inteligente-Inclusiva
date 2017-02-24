package tcc.route;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Value;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.WSMLRuleReasoner;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsmo.common.Identifier;
import org.wsmo.common.Namespace;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.service.Goal;
import org.wsmo.wsml.ParserException;

import tcc.keywords.ServerFilenameRepository;

public class Route {

	public static boolean[][] route;

	private static DefaultWSMLReasonerFactory f;
	private static WSMLRuleReasoner reasoner;
	
	private static int startingNode;
	private static int endingNode;
	
	public static String findRoute(Ontology graphOntology, Ontology shoppingProcess, Goal goal, Ontology jsonOntology, int nodesQuantity){
		initReasoner();
		
		String resp = "nao foi";
		
		boolean b = validateRoute(shoppingProcess, jsonOntology);
		System.out.println(startingNode+" "+endingNode);
		System.out.println(b);
		
		if(!b){
			return resp;
		}
		
		boolean[][] r =  findRoute(ServerFilenameRepository.COMMAND, ServerFilenameRepository.ROUTE_INPUT_FILE, startingNode, endingNode,
				ServerFilenameRepository.ROUTE_OUTPUT_ADV_FILE, nodesQuantity);
		
		resp = createRouteInfo(r, startingNode, endingNode, graphOntology); 
		return resp;
	}
	
	private static void initReasoner() {
		f = new DefaultWSMLReasonerFactory();
		reasoner = f.createWSMLRuleReasoner();
	}
	
	public static boolean[][] findRoute(String command, String input, int startingPoint, int endingPoint, String outputAdv,
			int nNodes){
		calculateRoutePRISM(ServerFilenameRepository.COMMAND, ServerFilenameRepository.ROUTE_INPUT_FILE, ServerFilenameRepository.ROUTE_OUTPUT_ADV_FILE);
		verifyRoute(outputAdv, nNodes, startingPoint, endingPoint);
		return route;
	}


	public static void calculateRoutePRISM(String command, String input, String outputAdv){

		String constArgs = "-const";
		String constValue = "x="+startingNode;
		String propertyArgs = "-pf";
		String propertyValue = "R min=?[F s="+endingNode+"]";
		String exportAdvArgs = "-exportadv";
		String args[] = {command, input, 
				constArgs, constValue, propertyArgs, propertyValue, exportAdvArgs, outputAdv};

		ProcessBuilder pb = new ProcessBuilder(args);
		System.out.println(pb.command());

		try {
			Process p = pb.start();
			while(p.isAlive());
			System.out.println(p.exitValue());
			System.out.println(pb);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void verifyRoute(String outputAdv, int nNodes, int startingPoint, int endingPoint){
		route = new boolean[nNodes][nNodes];
		readAdv(outputAdv);
		System.out.println(findRouteAdv(nNodes, startingPoint-1, startingPoint-1, endingPoint-1, new boolean[nNodes]));
		printRoute();
	}

	private static void readAdv(String outputAdv){
		try {
			BufferedReader bf = new BufferedReader(new FileReader(outputAdv));
			String l;
			while((l = bf.readLine()) != null){
				if(l.contains("a")){
					String[]s = l.substring(l.indexOf("a")+1, l.length()).split("_");
					int a = Integer.parseInt(s[0]);
					int b= Integer.parseInt(s[1]);

					route[a-1][b-1] = true;
					route[b-1][a-1] = true;
				}
			}
			bf.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static boolean findRouteAdv(int nNodes, int previousNode, int currentNode, int endingPoint, boolean[]visited){
		visited[currentNode] = true;
		if(currentNode == endingPoint){
			route = new boolean[nNodes][nNodes];
			route[previousNode][currentNode] = true;
			return true;
		}

		for(int i=0; i<nNodes; i++){
			if((route[currentNode][i]) && (!visited[i])){
				if(findRouteAdv(nNodes, currentNode, i, endingPoint, visited)){
					if(previousNode != currentNode){
						route[previousNode][currentNode] = true;
					}
					return true;
				}
			}
		}

		return false;
	}

	public static void printRoute(){
		for(int i=0; i<route.length; i++){
			for (int j=0; j<route.length; j++){
				System.out.print(route[i][j]+"  ");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public static String createRouteInfo(boolean[][]r, int startNode, int endNode, Ontology o){
		String resp = "";

		
		initReasoner();
		
		try {
			reasoner.registerOntology(o);
		} catch (InconsistencyException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		LogicalExpressionFactory leFactory = Factory.createLogicalExpressionFactory(null);

		int currentNode = startNode-1;
		JSONArray jArray = new JSONArray();
		for(int i=0; i<r[currentNode].length; i++){
			
			if(r[currentNode][i]){
				
				if(currentNode == i){
					JSONObject json = new JSONObject();
					json.put("desc", "You are already here.");
					json.put("endNode", "current");
					jArray.put(json);
					break;
				}

				String query = "?start[hasId hasValue "+(currentNode+1)+"] memberOf Node"
						+ " and ?end[hasId hasValue "+(i+1)+"] memberOf Node"
						+ " and ?edge memberOf Edge and ?edge[hasStartingNode hasValue ?start]"
						+ " and ?edge[hasEndingNode hasValue ?end]"
						+ " and ?edge[hasDescription hasValue ?desc].";

				Set<Map<Variable, Term>> re = null;
				try {
					re = reasoner.executeQuery(leFactory.createLogicalExpression(query, o));
				} catch (ParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				jArray.put(putQueryResult(re));
				currentNode = i;
				i = -1;
			}
		}
		resp = jArray.toString();
		return resp;
	}

	private static JSONObject putQueryResult(Set<Map<Variable, Term>> re) {

		JSONObject json = new JSONObject();
		
		if(re.size() >1){
			json.put("desc", "You are already here.");
			json.put("endNode", "current");
			return json;
		}
		for (Map<Variable, Term> elems : re){
			for (Variable key : elems.keySet()){
				if(key.toString().equals("?desc")){
					json.put("desc", elems.get(key));
				}
				if(key.toString().equals("?end")){
					json.put("endNode", elems.get(key));
				}
			}
		}
		
		return json;
	}
	
	public static boolean validateRoute(Ontology shoppingProcess, Ontology jsonOntology) {
		Set<Ontology> ontologies = new HashSet<Ontology>();
		ontologies.add(shoppingProcess);
		ontologies.add(jsonOntology);
		try {
			reasoner.registerOntologies(ontologies);
		} catch (InconsistencyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Namespace nsp = shoppingProcess.getDefaultNamespace();
		Set<Concept> concepts = shoppingProcess.listConcepts();
		Concept validConcept = null;
		boolean conceptExists = false;
		for (Concept c : concepts) {
			if (c.getIdentifier().toString().equals(nsp.getIRI().toString() + "RouteRequest")) {
				validConcept = c;
				conceptExists = true;
				break;
			}
		}
		if (!conceptExists) {
			return false;
		}
		Set<Instance> instances = jsonOntology.listInstances();
		Instance instance = null;
		for (Instance i : instances) {
			concepts = i.listConcepts();
			if (reasoner.isMemberOf(i, validConcept)) {
				System.out.println("achou");
				instance = i;
			}
		}
		if (instance != null) {
			Map<Identifier, Set<Value>> map = instance.listAttributeValues();
			for (Identifier id : map.keySet()) {
				if (id.toString().equals(nsp.getIRI().toString() + "hasStartingNode")) {
					Set<Value> values = map.get(id);
					if (values.size() > 1) {
						System.out.println("valores nao batem");
						return false;
					}
					for (Value v : values) {
						Instance i = (Instance) v;
						String nodeId = i.getIdentifier().toString();
						nodeId = nodeId.substring(nodeId.lastIndexOf("N")+1, nodeId.length());
						startingNode = Integer.parseInt(nodeId);
					}
				}
				if (id.toString().equals(nsp.getIRI().toString() + "hasEndingNode")) {
					Set<Value> values = map.get(id);
					if (values.size() > 1) {
						System.out.println("valores nao batem");
						return false;
					}
					for (Value v : values) {
						Instance i = (Instance) v;
						String nodeId = i.getIdentifier().toString();
						nodeId = nodeId.substring(nodeId.lastIndexOf("N")+1, nodeId.length());
						endingNode = Integer.parseInt(nodeId);
					}
				}
			}
		}

		return true;
	}
}


/*
./prism ../examples/felipe_tcc/rotas.pm -const x=2 -pf 'R min=?[F s=5]' -exportadv nonmdp.adv
 */