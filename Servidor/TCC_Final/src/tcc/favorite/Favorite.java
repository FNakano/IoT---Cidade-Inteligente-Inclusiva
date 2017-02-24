package tcc.favorite;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.omwg.logicalexpression.LogicalExpression;
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
import org.wsmo.common.TopEntity;
import org.wsmo.factory.DataFactory;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.service.Goal;
import org.wsmo.wsml.Serializer;

import tcc.wsml.WSMLParser;

public class Favorite {

	private static DefaultWSMLReasonerFactory f;
	private static WSMLRuleReasoner reasoner;
	private static boolean isFavorite = false;
	
	public static Set<Map<Variable, Term>> favoriteLocation(Ontology shopping, Ontology shoppingProcess, Goal goal,
			Ontology jsonOntology, String filename) {
		initReasoner();
		Set<Map<Variable, Term>> s = new HashSet<Map<Variable, Term>>();

		Instance i = validateFavoriteReq(shoppingProcess, jsonOntology);
		if (i != null) {
			System.out.println(i.getIdentifier().toString());
			System.out.println(isFavorite);
		} else {
			System.out.println("nao achou");
			return s;
		}
		if(validateFavoriteLocation(shopping, i)){
			System.out.println("validou tudo");
			favoriteLocation(filename, shopping, i);
			s = searchFavorite(shopping, i);
		}else{
			System.out.println("nao validou tudo");
			
		}
		
		
		
		return s;
	}

	private static void initReasoner() {
		f = new DefaultWSMLReasonerFactory();
		reasoner = f.createWSMLRuleReasoner();
	}
	
	public static String favoriteLocation(String filename, Ontology o, Instance originalInstance){

		String resp = null;

		DataFactory dataFactory;
		dataFactory = Factory.createDataFactory(null);

		Set<Instance> instances = o.listInstances();
		
		Instance locationInstance = null;
		Identifier attribute = null;
		for(Instance i:instances){
			if(i.getIdentifier().toString().equals(originalInstance.getIdentifier().toString())){
				Map<Identifier, Set<Value>> values = i.listAttributeValues();
				locationInstance = i;
				for(Identifier id: values.keySet()){
					System.out.println("ID " + id + " Term: " + values.get(id));
					System.out.println(id.toString());
					System.out.println(o.getDefaultNamespace().toString()+"isFavorite");
					if(id.toString().equals(o.getDefaultNamespace().getIRI().toString()+"isFavorite")){
						attribute = id;
						break;
					}
				}
				break;
			}
		}
		try {
			locationInstance.removeAttributeValues(attribute);
			locationInstance.addAttributeValue(attribute, dataFactory.createWsmlBoolean(isFavorite));
			o.addInstance(locationInstance);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Serializer serializer = Factory.createSerializer(null);
		StringWriter writer = new StringWriter();
		try {
			serializer.serialize(new TopEntity[] { o }, writer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String s = writer.getBuffer().toString();

		BufferedWriter bf;
		try {
			bf = new BufferedWriter(new FileWriter(filename));
			bf.write(s);
			bf.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return resp;
	}
	
	private static Set<Map<Variable, Term>> searchFavorite(Ontology shopping, Instance instance) {

		Set<Map<Variable, Term>> resp = null;

		String shoppingIRI = shopping.getDefaultNamespace().getIRI().toString();
		LogicalExpressionFactory leFactory = Factory.createLogicalExpressionFactory(null);
		LogicalExpression query = null;

		try {
			reasoner.registerOntology(shopping);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String q = "?location memberOf " + WSMLParser.defaultIRI(shoppingIRI + "Location") + " and ?location["
				+ WSMLParser.defaultIRI(shoppingIRI + "hasDescription") + " hasValue ?desc] \n" + " and ?location["
				+ WSMLParser.defaultIRI(shoppingIRI + "hasNode") + " hasValue ?node] \n" + " and ?location["
				+ WSMLParser.defaultIRI(shoppingIRI + "isFavorite") + " hasValue ?favorite] \n";

		System.out.println(q);

		try {
			String instanceQuery = " and ?location = " + WSMLParser.defaultIRI(instance.getIdentifier().toString())
					+ " \n";
			query = leFactory.createLogicalExpression(q + instanceQuery);
			resp = reasoner.executeQuery(query);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return resp;
		
	}
	
	public static String printJson(Set<Map<Variable, Term>> resp) {
		JSONObject jsonObj = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		for (Map<Variable, Term> elems : resp) {
			jsonObj = new JSONObject();
			for (Variable key : elems.keySet()) {
				if (key.toString().contains("location")) {
					jsonObj.put("location", elems.get(key));
				} else if (key.toString().contains("desc")) {
					jsonObj.put("description", elems.get(key));
				} else if (key.toString().contains("node")) {
					jsonObj.put("node", elems.get(key));
				} else if (key.toString().contains("favorite")) {
					if (elems.get(key).toString().indexOf("_boolean") != -1) {
						if (elems.get(key).toString().indexOf("true") != -1) {
							jsonObj.put("favorite", "true");
						} else if (elems.get(key).toString().indexOf("false") != -1) {
							jsonObj.put("favorite", "false");
							;
						}
					}
				}
			}
			jsonArray.put(jsonObj);
		}
		return jsonArray.toString();
	}
	
	public static boolean validateFavoriteLocation(Ontology shopping, Instance instance){
		
		Set<Map<Variable, Term>> resp = null;

		String shoppingIRI = shopping.getDefaultNamespace().getIRI().toString();
		LogicalExpressionFactory leFactory = Factory.createLogicalExpressionFactory(null);
		LogicalExpression query = null;

		try {
			reasoner.registerOntology(shopping);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String q = "?location memberOf " + WSMLParser.defaultIRI(shoppingIRI + "Location") + " and ?location["
				+ WSMLParser.defaultIRI(shoppingIRI + "isFavorite") + " hasValue ?favorite] \n";
		q += " and ?location = " + WSMLParser.defaultIRI(instance.getIdentifier().toString());
		
		try {
			String instanceQuery = " and ?location = " + WSMLParser.defaultIRI(instance.getIdentifier().toString())
					+ " \n";
			query = leFactory.createLogicalExpression(q + instanceQuery);
			resp = reasoner.executeQuery(query);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(resp.isEmpty()){
			System.out.println("validate is empty");
		}else{
			/*for(Map<Variable, Term> map: resp){
				for(Variable key : map.keySet()){
					if(key.toString().contains("?favorite")){
						//revalidar se favorito ou nao, se quiser
					}
					System.out.println(key+ "    "+map.get(key));
				}
			}*/
			return true;
		}
		
		return false;
	}
	
	public static Instance validateFavoriteReq(Ontology shoppingProcess, Ontology jsonOntology) {
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
			if (c.getIdentifier().toString().equals(nsp.getIRI().toString() + "FavoriteRequest")) {
				validConcept = c;
				conceptExists = true;
				break;
			}
		}
		if (!conceptExists) {
			return null;
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
		Instance resp = null;
		if (instance != null) {
			Map<Identifier, Set<Value>> map = instance.listAttributeValues();
			for (Identifier id : map.keySet()) {
				if (id.toString().equals(nsp.getIRI().toString() + "hasLocation")) {
					Set<Value> values = map.get(id);
					if (values.size() > 1) {
						System.out.println("valores nao batem");
						return null;
					}
					for (Value v : values) {
						resp = (Instance) v;
					}
				}
				if (id.toString().equals(nsp.getIRI().toString() + "isFavorite")) {
					Set<Value> values = map.get(id);
					if (values.size() > 1) {
						System.out.println("valores nao batem bool");
						return null;
					}
					for (Value v : values) {
						if(!v.toString().contains("boolean")){
							System.out.println("valores nao batem bool2");
							return null;
						}
						if(v.toString().contains("true")){
							isFavorite = true;
						}else{
							isFavorite = false;
						}
					}
				}
			}
		}

		return resp;
	}

}
