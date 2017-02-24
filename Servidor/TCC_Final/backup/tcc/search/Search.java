package tcc.search;

import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.WSMLRuleReasoner;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsmo.service.Goal;

import org.json.JSONObject;
import org.json.JSONArray;


public class Search {
	
	public static Set<Map<Variable, Term>> searchShopping(Ontology ontology, Goal goal){
		return search(ontology, goal);
	}
	
	private static Set<Map<Variable, Term>> search(Ontology ontology, Goal goal){
		
		//WSMLRuleReasoner reasoner = DefaultWSMLReasonerFactory.getFactory().;
		DefaultWSMLReasonerFactory f = new DefaultWSMLReasonerFactory();
		WSMLRuleReasoner reasoner = f.createWSMLRuleReasoner();
		try {
			reasoner.registerOntology(ontology);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Set<Map<Variable, Term>> resp = null;

		Set<Axiom> x = goal.getCapability().listPostConditions();
		for (Axiom a : x) {
			Set<LogicalExpression> le = a.listDefinitions();
			for (LogicalExpression l : le) {
				try {
					resp = reasoner.executeQuery(l);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		for (Map<Variable, Term> elems : resp) {
			for (Variable key : elems.keySet())
				System.out.println("\tVariable: " + key + " Term: " + elems.get(key));
		}

        return resp;
	}
	
	public static String printJson(Set<Map<Variable, Term>> resp){
		JSONObject jsonObj = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		for (Map<Variable, Term> elems : resp){
			jsonObj = new JSONObject();
        	for (Variable key : elems.keySet()){
        		if(key.toString().contains("location")){
        			jsonObj.put("location", elems.get(key));
				}else if(key.toString().contains("desc")){
					jsonObj.put("description", elems.get(key));
				}else if(key.toString().contains("node")){
					jsonObj.put("node", elems.get(key));
				}else if(key.toString().contains("fav")){
					if(elems.get(key).toString().indexOf("_boolean") != -1){
						if(elems.get(key).toString().indexOf("true") != -1){
							jsonObj.put("favorite", "true");
						}else if(elems.get(key).toString().indexOf("false") != -1){
							jsonObj.put("favorite", "false");;
						}
					}
				}
        		//jsonObj.put("var", key);
        		//jsonObj.put("term", elems.get(key));
        		
        	}
        	jsonArray.put(jsonObj);
        }
		return jsonArray.toString();
	}

}
