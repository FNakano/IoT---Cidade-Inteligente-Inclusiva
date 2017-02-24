package tcc.wsml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.HashMap;

import org.omwg.ontology.Ontology;
import org.wsmo.common.TopEntity;
import org.wsmo.factory.DataFactory;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.service.Goal;
import org.wsmo.wsml.Parser;

public class WSMLParser {

	private static WsmoFactory factory = Factory.createWsmoFactory(null);
	private static LogicalExpressionFactory leFactory = Factory.createLogicalExpressionFactory(null);
	private static DataFactory dataFactory = Factory.createDataFactory(null);
	private static Parser parser;

	private static void initParser() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(Factory.WSMO_FACTORY, factory);
		map.put(Factory.LE_FACTORY, leFactory);
		map.put(Factory.DATA_FACTORY, dataFactory);

		map.put(Parser.CLEAR_MODEL, "true");
		parser = Factory.createParser(map);
	}

	public static Ontology loadOntology(String filename) {
		initParser();

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

		TopEntity[] parsed = null;
		try {
			parsed = parser.parse(new StringReader(doc.toString()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int i = 0; i < parsed.length; i++) {
			if (parsed[i] instanceof Ontology) {
				return (Ontology) parsed[i];
			}
		}
		return null;
	}

	public static TopEntity[] getEntities(String jsonGoal) {
		initParser();

		TopEntity[] parsed = null;
		try {
			parsed = parser.parse(new StringReader(jsonGoal));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return parsed;
	}

	public static Ontology ontologyTopEntity(TopEntity[] entities) {

		for (int i = 0; i < entities.length; i++) {
			if (entities[i] instanceof Ontology) {
				return ((Ontology) entities[i]);
			}
		}
		return null;
	}

	public static Goal goalTopEntity(TopEntity[] entities) {

		for (int i = 0; i < entities.length; i++) {
			if (entities[i] instanceof Goal) {
				return ((Goal) entities[i]);
			}
		}
		return null;
	}

	/*public static String goalToString(Goal goal) {
		String s = null;
		Serializer serializer = Factory.createSerializer(null);
		StringWriter writer = new StringWriter();
		try {
			serializer.serialize(new TopEntity[] { goal }, writer);
			s = writer.getBuffer().toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return s;
	}*/

	/*public static String ontologyToString(Ontology o) {
		String s = null;
		Serializer serializer = Factory.createSerializer(null);
		StringWriter writer = new StringWriter();
		try {
			serializer.serialize(new TopEntity[] { o }, writer);
			s = writer.getBuffer().toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return s;
	}*/

	/*public static String entitiesToString(TopEntity[] entities) {
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
	}*/

	public static String defaultIRI(String iri) {
		String resp = "_\"";
		resp += iri;
		resp += "\"";
		return resp;
	}

}
