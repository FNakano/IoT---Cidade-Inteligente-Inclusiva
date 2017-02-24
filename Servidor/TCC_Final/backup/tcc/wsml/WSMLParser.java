package tcc.wsml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deri.wsmo4j.io.parser.wsml.ParserImpl;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.WSMLFlightReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.WSMLRuleReasoner;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsmo.common.TopEntity;
import org.wsmo.factory.DataFactory;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.locator.Locator;
import org.wsmo.locator.LocatorManager;
import org.wsmo.service.Capability;
import org.wsmo.service.Goal;
import org.wsmo.validator.WsmlValidator;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.Serializer;

import com.ontotext.wsmo4j.common.IRIImpl;
import com.ontotext.wsmo4j.ontology.OntologyImpl;
import com.ontotext.wsmo4j.service.GoalImpl;

public class WSMLParser {
	
	public static Ontology loadOntology(String filename) {
		File selectedFile = new File(filename);
		BufferedReader reader;
		StringBuilder doc = new StringBuilder();
		String line;
		try {
			reader = new BufferedReader(new FileReader(selectedFile));
			while ((line = reader.readLine()) != null)
				doc.append(line + "\n");
			reader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ParserImpl parser = new ParserImpl(new HashMap<String, Object>());
		TopEntity[] parsed = null;
		try {
			parsed = parser.parse(new StringReader(doc.toString()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int i = 0; i < parsed.length; i++) {
			if (parsed[i] instanceof OntologyImpl) {
				return (OntologyImpl) parsed[i];
			}
		}
		return null;
	}

	/*public static Goal getGoal(String jsonGoal) {

		ParserImpl parser = new ParserImpl(new HashMap<String, Object>());
		TopEntity[] parsed = null;
		try {
			parsed = parser.parse(new StringReader(jsonGoal));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*for (int i = 0; i < parsed.length; i++) {
			if (parsed[i] instanceof GoalImpl)
				return ((Goal) parsed[i]);
		}*
		return null;
	}*/
	
	public static Goal getGoal(String jsonGoal) {
		
		HashMap <String, Object> props = new HashMap <String, Object>();

        // use default implementation for factory
        WsmoFactory factory = Factory.createWsmoFactory(null);
        LogicalExpressionFactory leFactory = Factory.createLogicalExpressionFactory(null);
        DataFactory dataFactory = Factory.createDataFactory(null);
        
        props.put(Factory.WSMO_FACTORY, factory);
        props.put(Factory.LE_FACTORY, leFactory);
        props.put(Factory.DATA_FACTORY, dataFactory);

        props.put(Parser.CLEAR_MODEL, "true");
        
        Parser parser = Factory.createParser(props);
		
		//ParserImpl parser = new ParserImpl(new HashMap<String, Object>());
		TopEntity[] parsed = null;
		try {
			parsed = parser.parse(new StringReader(jsonGoal));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < parsed.length; i++) {
			if (parsed[i] instanceof GoalImpl)
				return ((Goal) parsed[i]);
		}
		return null;
	}
	
	public static Ontology getGoalOntology(String jsonGoal) {

		ParserImpl parser = new ParserImpl(new HashMap<String, Object>());
		TopEntity[] parsed = null;
		try {
			parsed = parser.parse(new StringReader(jsonGoal));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < parsed.length; i++) {
			if (parsed[i] instanceof OntologyImpl){
				DefaultWSMLReasonerFactory f = new DefaultWSMLReasonerFactory();
				WSMLRuleReasoner reasoner = f.createWSMLRuleReasoner();
				try {
					reasoner.registerOntology((OntologyImpl)parsed[i]);
				} catch (InconsistencyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
				return ((OntologyImpl) parsed[i]);
			}
		}
		return null;
	}

	/*public static Goal getGoal(String jsonGoal) {

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(Parser.CLEAR_MODEL, "true");
		//map.put(Parser.CACHE_LOGICALEXPRESSION_STRING, "true");
		
		Object o = null;
		
		o = map.get(Parser.CLEAR_MODEL);
        if (o != null && !o.toString().equals("false")){
            //System.out.println("mapa 1");
        }
        
       // o = map.get(Parser.CACHE_LOGICALEXPRESSION_STRING);
        //if (o != null && !o.toString().equals("false")){
         //   System.out.println("mapa2");;
       // }
		
		//ParserImpl parser = new ParserImpl(new HashMap<String, Object>());
		ParserImpl parser = new ParserImpl(map);
		TopEntity[] parsed = null;
		
		parserInfo();
		
		try {
			parsed = parser.parse(new StringReader(jsonGoal));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < parsed.length; i++) {
			if (parsed[i] instanceof GoalImpl)
				return ((Goal) parsed[i]);
		}
		return null;
	}*/
	
	public static TopEntity[] getEntities(String jsonString) {

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(Parser.CLEAR_MODEL, "true");
		//map.put(Parser.CACHE_LOGICALEXPRESSION_STRING, "true");
		Object o = null;
		
		o = map.get(Parser.CLEAR_MODEL);
        if (o != null && !o.toString().equals("false")){
            System.out.println("mapa 1");
        }
        
       // o = map.get(Parser.CACHE_LOGICALEXPRESSION_STRING);
        //if (o != null && !o.toString().equals("false")){
         //   System.out.println("mapa2");;
       // }
		
		//ParserImpl parser = new ParserImpl(new HashMap<String, Object>());
		ParserImpl parser = new ParserImpl(map);
		TopEntity[] parsed = null;
		try {
			parsed = parser.parse(new StringReader(jsonString));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return parsed;
	}
	
	public static boolean preconditionValidator(Ontology ontology, Goal goal) {
		HashMap<String, Object> reasonerParams = new HashMap<String, Object>();
		reasonerParams.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER, WSMLReasonerFactory.BuiltInReasoner.IRIS);

		WSMLFlightReasoner reasoner = DefaultWSMLReasonerFactory.getFactory().createWSMLFlightReasoner(reasonerParams);

		try {
			reasoner.registerOntology(ontology);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Set<Map<Variable, Term>> resp = null;

		Set<Axiom> x = goal.getCapability().listPreConditions();
		
		if(x.size() == 0){
			return true;
		}
		
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
		
		if (resp.size() >= 1) {
			return true;
		}

		return false;
	}
	
	public static String goalToString(Goal goal){
		String s = null;
		Serializer serializer = Factory.createSerializer(null);
		StringWriter writer = new StringWriter();
		try {
			serializer.serialize(new TopEntity[] {goal}, writer);
			s = writer.getBuffer().toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return s;
	}
	
	public static String ontologyToString(Ontology o){
		String s = null;
		Serializer serializer = Factory.createSerializer(null);
		StringWriter writer = new StringWriter();
		try {
			serializer.serialize(new TopEntity[] {o}, writer);
			s = writer.getBuffer().toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return s;
	}
	
	public static String entitiesToString(TopEntity[] entities){
		String s = null;
		Serializer serializer = Factory.createSerializer(null);
		StringWriter writer = new StringWriter();
		try {
			serializer.serialize(entities, writer);
			s = writer.getBuffer().toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return s;
	}
	
	public static void parserInfo(){
		System.out.println("-------------------------PARSER-----------------------------");
		WsmoFactory factory = Factory.createWsmoFactory(null);
		IRIImpl iri = new IRIImpl("http://www.example.com/goals/SearchLocation#searchCapability");
		Capability g = factory.getCapability(iri);
		if(g != null){
			System.out.println("NAO NULL");
			Set<Axiom> axioms = g.listPostConditions();
			if(axioms.size() > 0){
				for(Axiom a: axioms){
					System.out.println(a);
				}
			}else{
				System.out.println("EMPTY");
			}
		}else{
			System.out.println("NULL");
		}
		System.out.println("------------------------------PARSER---------------------------------");
	}
	
	public static void printGoalOntology(){
		WsmoFactory factory = Factory.createWsmoFactory(null);
		IRIImpl iri = new IRIImpl("http://www.example.com/goals/SearchLocation#GoalRequest");
		Ontology o = factory.getOntology(iri);
		String s = null;
		Serializer serializer = Factory.createSerializer(null);
		StringWriter writer = new StringWriter();
		try {
			serializer.serialize(new TopEntity[] {o}, writer);
			s = writer.getBuffer().toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("---------------------ONTOLOGY-------------------------");
		System.out.println(s);
		System.out.println("---------------------ONTOLOGY-------------------------");
	}

}
