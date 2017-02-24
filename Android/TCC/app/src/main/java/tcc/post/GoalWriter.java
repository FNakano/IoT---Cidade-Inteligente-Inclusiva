package tcc.post;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.ComplexDataValue;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.SimpleDataValue;
import org.omwg.ontology.Value;
import org.wsmo.common.IRI;
import org.wsmo.common.Identifier;
import org.wsmo.common.Namespace;
import org.wsmo.common.TopEntity;
import org.wsmo.common.WSML;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.common.exception.SynchronisationException;
import org.wsmo.factory.DataFactory;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.service.Capability;
import org.wsmo.service.Goal;
import org.wsmo.wsml.Serializer;

import com.ontotext.wsmo4j.common.IRIImpl;
import com.ontotext.wsmo4j.common.NamespaceImpl;
import com.ontotext.wsmo4j.ontology.AxiomImpl;
import com.ontotext.wsmo4j.ontology.ConceptImpl;
import com.ontotext.wsmo4j.ontology.InstanceImpl;
import com.ontotext.wsmo4j.ontology.OntologyImpl;
import com.ontotext.wsmo4j.service.CapabilityImpl;
import com.ontotext.wsmo4j.service.GoalImpl;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Set;

import tcc.keywords.KeywordsRepository;

/**
 * Created by minato on 20/11/16.
 */

public class GoalWriter {

	private static WsmoFactory factory;
	private static LogicalExpressionFactory leFactory;
	private static DataFactory dataFactory;

	/*
	 * private static IRI graphIRI; private static IRI discoveryIRI; private
	 * static IRI shoppingIRI; private static IRI shoppingProcessIRI;
	 */

	private static void initFactories() {
		if (factory == null) {
			factory = Factory.createWsmoFactory(null);
		}
		if (leFactory == null) {
			leFactory = Factory.createLogicalExpressionFactory(null);
		}
		if (dataFactory == null) {
			dataFactory = Factory.createDataFactory(null);
		}
	}

	public GoalWriter() {
		factory = Factory.createWsmoFactory(null);
		leFactory = Factory.createLogicalExpressionFactory(null);
	}

	public static String writeSearchGoal(String id) {
		initFactories();
		String resp = null;
		String defaultNamespace = "http://www.example.com/goals/SearchLocation#";
		Goal g = null;
		Ontology o = null;
		try {
			g = createSearchGoal(defaultNamespace);
			o = createSearchReqOntology(defaultNamespace, id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		resp = goalToString(g, o);

		return resp;
	}

	public static String writeSearchGoal(String concept, String attId, String attValue) {
		initFactories();
		String resp = null;
		String defaultNamespace = "http://www.example.com/goals/SearchLocation#";
		Goal g = null;
		Ontology o = null;

		IRI attIdIRI = null;
		Value attValueV = null;

		if (concept.isEmpty()) {
			concept = "Location";
		}

		if (!attId.isEmpty() && !attValue.isEmpty()) {
			attIdIRI = new IRIImpl(KeywordsRepository.SHOPPING_NAMESPACE + attId);

			if (attId.equals(KeywordsRepository.IS_FAVORITE_ATT)) {
				attValueV = dataFactory.createWsmlBoolean(attValue);
			} else if (attId.equals(KeywordsRepository.HAS_NODE_ATT)) {
				attValueV = (Value) new InstanceImpl(new IRIImpl(attValue));
			} else {
				attValueV = (Value) new InstanceImpl(new IRIImpl(KeywordsRepository.SHOPPING_NAMESPACE + attValue));
			}

		}

		try {
			g = createSearchGoal(defaultNamespace);
			o = createSearchReqOntology(defaultNamespace, concept, attIdIRI, attValueV);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		resp = goalToString(g, o);

		return resp;
	}
	
	public static String writeFavoriteGoal(String id, boolean b) {
		initFactories();
		String resp = null;
		String defaultNamespace = "http://www.example.com/goals/FavoriteLocation#";
		Goal g = null;
		Ontology o = null;
		
		Value attValueV = dataFactory.createWsmlBoolean(b);
		
		try {
			g = createFavoriteGoal(defaultNamespace);
			o = createFavoriteReqOntology(defaultNamespace, id, attValueV);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		resp = goalToString(g, o);

		return resp;
	}
	
	public static String writeRouteGoal(String startingNode, String endingNode) {
		initFactories();
		String resp = null;
		String defaultNamespace = "http://example.com/goals/CalculateRoute#";
		Goal g = null;
		Ontology o = null;

		Value startingNodeValue = (Value) new InstanceImpl(new IRIImpl(startingNode));
		Value endingNodeValue = (Value) new InstanceImpl(new IRIImpl(endingNode));

		try {
			g = createRouteGoal(defaultNamespace);
			o = createRouteReqOntology(defaultNamespace, startingNodeValue, endingNodeValue);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		resp = goalToString(g, o);

		return resp;
	}

	private static Goal importOntologiesGoal(Goal goal) {

		if (goal == null) {
			System.out.println("wat");
		}

		goal.addOntology(factory
				.getOntology(new IRIImpl(KeywordsRepository.GRAPH_NAMESPACE + KeywordsRepository.GRAPH_ONTOLOGY_ID)));
		goal.addOntology(factory.getOntology(
				new IRIImpl(KeywordsRepository.SHOPPING_NAMESPACE + KeywordsRepository.SHOPPING_ONTOLOGY_ID)));
		goal.addOntology(factory.getOntology(new IRIImpl(
				KeywordsRepository.SHOPPING_PROCESS_NAMESPACE + KeywordsRepository.SHOPPING_ONTOLOGY_PROCESS_ID)));

		return goal;
	}

	private static Ontology importOntologiesOntology(Ontology o) {

		o.addOntology(factory
				.getOntology(new IRIImpl(KeywordsRepository.GRAPH_NAMESPACE + KeywordsRepository.GRAPH_ONTOLOGY_ID)));
		o.addOntology(factory.getOntology(
				new IRIImpl(KeywordsRepository.SHOPPING_NAMESPACE + KeywordsRepository.SHOPPING_ONTOLOGY_ID)));
		o.addOntology(factory.getOntology(new IRIImpl(
				KeywordsRepository.SHOPPING_PROCESS_NAMESPACE + KeywordsRepository.SHOPPING_ONTOLOGY_PROCESS_ID)));

		return o;
	}

	private static Goal addNamespaces(Goal goal) {

		Namespace graphNamespace = new NamespaceImpl("graph", new IRIImpl(KeywordsRepository.GRAPH_NAMESPACE));
		Namespace discoveryNamespace = new NamespaceImpl("discovery",
				new IRIImpl(KeywordsRepository.DISCOVERY_NAMESPACE));
		Namespace shoppingNamespace = new NamespaceImpl("shopping", new IRIImpl(KeywordsRepository.SHOPPING_NAMESPACE));
		Namespace shoppingProcessNamespace = new NamespaceImpl("shoppingProcess",
				new IRIImpl(KeywordsRepository.SHOPPING_PROCESS_NAMESPACE));

		goal.addNamespace(graphNamespace);
		goal.addNamespace(discoveryNamespace);
		goal.addNamespace(shoppingNamespace);
		goal.addNamespace(shoppingProcessNamespace);

		return goal;
	}

	private static Capability addLightweightRuleDiscovery(Capability capability)
			throws SynchronisationException, IllegalArgumentException, InvalidModelException {

		capability.addNFPValue(new IRIImpl(KeywordsRepository.DISCOVERY_NAMESPACE + "discoveryStrategy"),
				new IRIImpl(KeywordsRepository.DISCOVERY_NAMESPACE + "LightweightRuleDiscovery"));
		capability.addNFPValue(new IRIImpl(KeywordsRepository.DISCOVERY_NAMESPACE + "discoveryStrategy"),
				new IRIImpl(KeywordsRepository.DISCOVERY_NAMESPACE + "NoPreFilter"));

		return capability;
	}

	private static Goal createSearchGoal(String defaultName) throws Exception {

		String resp = null;
		String GOAL_ID = defaultName + "SearchGoal";

		IRI goalIRI = new IRIImpl(GOAL_ID);

		Goal goal = new GoalImpl(goalIRI);

		goal.setWsmlVariant(WSML.WSML_RULE);
		Namespace defaultNamespace = new NamespaceImpl("", new IRIImpl(defaultName));
		goal.setDefaultNamespace(defaultNamespace);

		goal = addNamespaces(goal);
		goal = importOntologiesGoal(goal);

		Capability capability = new CapabilityImpl(new IRIImpl(defaultName + "searchCapability"));

		capability = addLightweightRuleDiscovery(capability);

		Axiom axiom = null;
		LogicalExpression logExp = null;

		// add capability post-condition
		axiom = new AxiomImpl(new IRIImpl(defaultName + "searchPos"));
		String stringExpression = " ?resp memberOf shoppingProcess#SearchResponse";
		logExp = leFactory.createLogicalExpression(stringExpression + ".", goal);
		axiom.addDefinition(logExp);
		capability.addPostCondition(axiom);

		goal.setCapability(capability);

		return goal;
	}
	
	private static Goal createFavoriteGoal(String defaultName) throws Exception {

		String resp = null;
		String GOAL_ID = defaultName + "FavoriteGoal";

		IRI goalIRI = new IRIImpl(GOAL_ID);

		Goal goal = new GoalImpl(goalIRI);

		goal.setWsmlVariant(WSML.WSML_RULE);
		Namespace defaultNamespace = new NamespaceImpl("", new IRIImpl(defaultName));
		goal.setDefaultNamespace(defaultNamespace);

		goal = addNamespaces(goal);
		goal = importOntologiesGoal(goal);

		Capability capability = new CapabilityImpl(new IRIImpl(defaultName + "favoriteCapability"));

		capability = addLightweightRuleDiscovery(capability);

		Axiom axiom = null;
		LogicalExpression logExp = null;

		// add capability post-condition
		axiom = new AxiomImpl(new IRIImpl(defaultName + "favoritePos"));
		String stringExpression = " ?resp memberOf shoppingProcess#FavoriteResponse";
		logExp = leFactory.createLogicalExpression(stringExpression + ".", goal);
		axiom.addDefinition(logExp);
		capability.addPostCondition(axiom);

		goal.setCapability(capability);

		return goal;
	}
	
	private static Goal createRouteGoal(String defaultName) throws Exception {

		String resp = null;
		String GOAL_ID = defaultName + "CalculateRoute";

		IRI goalIRI = new IRIImpl(GOAL_ID);

		Goal goal = new GoalImpl(goalIRI);

		goal.setWsmlVariant(WSML.WSML_RULE);
		Namespace defaultNamespace = new NamespaceImpl("", new IRIImpl(defaultName));
		goal.setDefaultNamespace(defaultNamespace);

		goal = addNamespaces(goal);
		goal = importOntologiesGoal(goal);

		Capability capability = new CapabilityImpl(new IRIImpl(defaultName + "CalculateRouteCapability"));

		capability = addLightweightRuleDiscovery(capability);

		Axiom axiom = null;
		LogicalExpression logExp = null;

		// add capability post-condition
		axiom = new AxiomImpl(new IRIImpl(defaultName + "CalculateRoutePos"));
		String stringExpression = " ?resp memberOf shoppingProcess#RouteResponse";
		logExp = leFactory.createLogicalExpression(stringExpression + ".", goal);
		axiom.addDefinition(logExp);
		capability.addPostCondition(axiom);

		goal.setCapability(capability);

		return goal;
	}

	private static Ontology createSearchReqOntology(String defaultNamespace, String locationId)
			throws SynchronisationException, InvalidModelException {

		Ontology o = new OntologyImpl(new IRIImpl(defaultNamespace + "SearchGoalRequest"));

		o = importOntologiesOntology(o);

		IRI requestIRI = new IRIImpl(defaultNamespace + "SearchRequest");
		IRI searchRequestIRI = new IRIImpl(KeywordsRepository.SHOPPING_PROCESS_NAMESPACE + "SearchRequest");
		IRI searchRequestAtt = new IRIImpl(KeywordsRepository.SHOPPING_PROCESS_NAMESPACE + "hasLocation");

		IRI locIRI = new IRIImpl(KeywordsRepository.SHOPPING_NAMESPACE + locationId);

		Instance i = new InstanceImpl(requestIRI);
		i.addConcept(factory.getConcept(searchRequestIRI));
		Instance loc = new InstanceImpl(locIRI);
		i.addAttributeValue(searchRequestAtt, loc);

		o.addInstance(i);
		//searchValidator(o);

		return o;
	}

	private static Ontology createSearchReqOntology(String defaultNamespace, String concept, IRI attIdIRI,
			Value attValue) throws SynchronisationException, InvalidModelException {

		Ontology o = new OntologyImpl(new IRIImpl(defaultNamespace + "SearchGoalRequest"));

		o = importOntologiesOntology(o);

		IRI requestIRI = new IRIImpl(defaultNamespace + "SearchRequest");
		IRI searchRequestIRI = new IRIImpl(KeywordsRepository.SHOPPING_PROCESS_NAMESPACE + "SearchRequest");
		IRI searchRequestAtt = new IRIImpl(KeywordsRepository.SHOPPING_PROCESS_NAMESPACE + "hasLocation");

		IRI locIRI = new IRIImpl(KeywordsRepository.SHOPPING_NAMESPACE + "locationPlaceholder");

		IRI locationConceptIRI = new IRIImpl(KeywordsRepository.SHOPPING_NAMESPACE + concept);

		Instance i = new InstanceImpl(requestIRI);
		i.addConcept(factory.getConcept(searchRequestIRI));

		Instance loc = new InstanceImpl(locIRI);
		loc.addConcept(factory.getConcept(locationConceptIRI));
		if (attIdIRI != null && attValue != null) {
			loc.addAttributeValue(attIdIRI, attValue);
		}
		i.addAttributeValue(searchRequestAtt, loc);

		o.addInstance(i);
		o.addInstance(loc);
		//searchValidator(o);

		return o;
	}
	
	private static Ontology createFavoriteReqOntology(String defaultNamespace, String locationId,
			Value b) throws SynchronisationException, InvalidModelException {

		Ontology o = new OntologyImpl(new IRIImpl(defaultNamespace + "SearchGoalRequest"));

		o = importOntologiesOntology(o);

		IRI requestIRI = new IRIImpl(defaultNamespace + "FavoriteRequest");
		IRI favoriteRequestIRI = new IRIImpl(KeywordsRepository.SHOPPING_PROCESS_NAMESPACE + "FavoriteRequest");
		IRI favoriteRequestLocationAtt = new IRIImpl(KeywordsRepository.SHOPPING_PROCESS_NAMESPACE + "hasLocation");
		IRI favoriteRequestBooleanAtt = new IRIImpl(KeywordsRepository.SHOPPING_PROCESS_NAMESPACE + "isFavorite");

		IRI locIRI = new IRIImpl(KeywordsRepository.SHOPPING_NAMESPACE + locationId);

		Instance i = new InstanceImpl(requestIRI);
		i.addConcept(factory.getConcept(favoriteRequestIRI));
		Instance loc = new InstanceImpl(locIRI);
		i.addAttributeValue(favoriteRequestLocationAtt, loc);
		i.addAttributeValue(favoriteRequestBooleanAtt, b);

		o.addInstance(i);
		//searchValidator(o);

		return o;
	}
	
	private static Ontology createRouteReqOntology(String defaultNamespace, Value startingNodeValue,
			Value endingNodeValue) throws SynchronisationException, InvalidModelException {

		Ontology o = new OntologyImpl(new IRIImpl(defaultNamespace + "CalculateRouteRequest"));

		o = importOntologiesOntology(o);

		IRI requestIRI = new IRIImpl(defaultNamespace + "RouteRequest");
		IRI routeRequestIRI = new IRIImpl(KeywordsRepository.SHOPPING_PROCESS_NAMESPACE + "RouteRequest");
		IRI startingNodeAtt = new IRIImpl(KeywordsRepository.SHOPPING_PROCESS_NAMESPACE + "hasStartingNode");
		IRI endingNodeAtt = new IRIImpl(KeywordsRepository.SHOPPING_PROCESS_NAMESPACE + "hasEndingNode");

		Instance i = new InstanceImpl(requestIRI);
		i.addConcept(factory.getConcept(routeRequestIRI));

		i.addAttributeValue(startingNodeAtt, startingNodeValue);
		i.addAttributeValue(endingNodeAtt, endingNodeValue);

		o.addInstance(i);
		//searchValidator(o);

		return o;
	}

	public static String goalToString(Goal goal, Ontology o) {
		String s = null;
		Serializer serializer = Factory.createSerializer(null);
		StringWriter writer = new StringWriter();
		try {
			serializer.serialize(new TopEntity[] { goal, o }, writer);
			s = writer.getBuffer().toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return s;
	}

	/*public static boolean searchValidator(Ontology jsonOntology) {
		System.out.println("-----------VALIDATOR-------");
		Set<Instance> instances = jsonOntology.listInstances();
		for (Instance i : instances) {
			Set<Concept> concepts = i.listConcepts();
			for (Concept c : concepts) {
				// System.out.println(c.getIdentifier());
				IRI iri = (IRI) c.getIdentifier();
				System.out.println(iri.getLocalName());
			}
		}
		System.out.println("-----------VALIDATOR-------");
		return false;
	}*/
}
