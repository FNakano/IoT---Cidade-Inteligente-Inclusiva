package tcc.search;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.service.Goal;
import org.wsmo.wsml.ParserException;

import tcc.wsml.WSMLParser;

import org.json.JSONObject;
import org.json.JSONArray;

public class Search {

	private static DefaultWSMLReasonerFactory f;
	private static WSMLRuleReasoner reasoner;

	public static Set<Map<Variable, Term>> searchShopping(Ontology shopping, Ontology shoppingProcess, Goal goal,
			Ontology jsonOntology) {
		initReasoner();
		Set<Map<Variable, Term>> s = new HashSet<Map<Variable, Term>>();

		Instance i = validateSearch(shoppingProcess, jsonOntology);
		if (i != null) {
			System.out.println(i.getIdentifier().toString());
			return search(shopping, jsonOntology, i);
		} else {
			System.out.println("nao achou");
		}
		return s;
	}

	private static void initReasoner() {
		f = new DefaultWSMLReasonerFactory();
		reasoner = f.createWSMLRuleReasoner();
	}

	private static Set<Map<Variable, Term>> search(Ontology shopping, Ontology jsonOntology, Instance instance) {

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

		if (!resp.isEmpty()) {
			return resp;
		}

		Set<Concept> concept = instance.listConcepts();
		Map<Identifier, Set<Value>> map = instance.listAttributeValues();

		for (Concept c : concept) {
			q += " and ?location memberOf " + WSMLParser.defaultIRI(c.getIdentifier().toString()) + " \n";
		}

		for (Identifier i : map.keySet()) {
			for (Value v : map.get(i)) {
				if (v instanceof Instance) {
					Instance inst = (Instance) v;
					q += " and ?location[" + WSMLParser.defaultIRI(i.toString()) + " hasValue "
							+ WSMLParser.defaultIRI(inst.getIdentifier().toString()) + "] \n";
				} else {
					q += " and ?location[" + WSMLParser.defaultIRI(i.toString()) + " hasValue "
							+ v.toString() + "] \n";
				}
			}
		}

		System.out.println(q);

		try {
			query = leFactory.createLogicalExpression(q);
			resp = reasoner.executeQuery(query);
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
				} else if (key.toString().contains("fav")) {
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

	public static Instance validateSearch(Ontology shoppingProcess, Ontology jsonOntology) {
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
			if (c.getIdentifier().toString().equals(nsp.getIRI().toString() + "SearchRequest")) {
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
						return (Instance) v;
					}
				}
			}
		}

		return null;
	}

}
