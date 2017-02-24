package tcc.route;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.WSMLRuleReasoner;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.wsml.ParserException;

public class Route {

	public static boolean[][] route;

	public static boolean[][] findRoute(String command, String input, int startingPoint, int endingPoint, String outputAdv,
			int nNodes){
		calculateRoute(command, input, startingPoint, endingPoint, outputAdv);
		verifyRoute(outputAdv, nNodes, startingPoint, endingPoint);
		return route;
	}


	public static void calculateRoute(String command, String input, int startingPoint, int endingPoint, String outputAdv){

		String constArgs = "-const";
		String constValue = "x="+startingPoint;
		String propertyArgs = "-pf";
		String propertyValue = "R min=?[F s="+endingPoint+"]";
		String exportAdvArgs = "-exportadv";
		String args[] = {command, input, constArgs, constValue, propertyArgs, propertyValue, exportAdvArgs, outputAdv};

		ProcessBuilder pb = new ProcessBuilder(args);
		System.out.println(pb.command());
		//pb.redirectOutput(new File("/home/minato/TCC/prism-4.3.1/Rotas/teste/output.txt"));
		//pb.redirectErrorStream(true);

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

		HashMap<String, Object> reasonerParams = new HashMap<String, Object>();
		reasonerParams.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER, 
				WSMLReasonerFactory.BuiltInReasoner.IRIS);
		WSMLRuleReasoner reasoner = DefaultWSMLReasonerFactory.getFactory().createWSMLRuleReasoner();
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
				i = 0;
			}
			//break;
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

		//System.out.println(json);
		
		return json;
	}


	/*public static String printJson(boolean[][]r){
		String result = "[";
		for(int i=0; i<r.length; i++){
			result +=  "{\n\"Node\": "+i+",\n";
			result += "\"Edge\": [";
			for(int j=0; j<r[i].length; j++){
				result += r[i][j]+",";
			}
			result = result.substring(0, result.length()-1);
			result += "]\n},";
		}
		result = result.substring(0, result.length()-1);
		result += "]";
		return result;
	}*/



	public static String printJson(boolean[][]r){
		String result = "[";
		for(int i=0; i<r.length; i++){
			result +=  "{\n\"Node\": "+i+",\n";
			result += "\"Edge\": [";
			for(int j=0; j<r[i].length; j++){
				result += r[i][j]+",";
			}
			result = result.substring(0, result.length()-1);
			result += "]\n},";
		}
		result = result.substring(0, result.length()-1);
		result += "]";
		return result;
	}

	public static String printArray(int[]pos){
		String resp = null;
		//for(int i)
		return resp;
	}
}


/*
./prism ../examples/felipe_tcc/rotas.pm -const x=2 -pf 'R min=?[F s=5]' -exportadv nonmdp.adv
 */