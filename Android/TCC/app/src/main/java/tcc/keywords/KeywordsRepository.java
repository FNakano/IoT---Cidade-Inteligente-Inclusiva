package tcc.keywords;

import java.util.HashSet;
import java.util.Set;

public class KeywordsRepository {

	// Sets

	// Namespaces
	public static final String GRAPH_NAMESPACE = "http://www.example.com/ontology/GraphOntology#";
	public static final String DISCOVERY_NAMESPACE = "http://wiki.wsmx.org/index.php?title=DiscoveryOntology#";
	public static final String SHOPPING_NAMESPACE = "http://www.example.com/ontology/ShoppingOntology#";
	public static final String SHOPPING_PROCESS_NAMESPACE = "http://www.example.com/ontology/ShoppingOntologyProcess#";

	// Ontology names
	public static final String GRAPH_ONTOLOGY_ID = "GraphOntology";
	public static final String SHOPPING_ONTOLOGY_ID = "ShoppingOntology";
	public static final String SHOPPING_ONTOLOGY_PROCESS_ID = "ShoppingOntologyProcess";
	
	//Search attributes
	
	public static final String HAS_FOOD_ATT = "hasFood";
	public static final String HAS_PRODUCT_ATT = "hasProduct";
	public static final String HAS_NODE_ATT = "hasNode";
	public static final String IS_FAVORITE_ATT = "isFavorite";
	/*
	 * public static final String public static final String public static final
	 * String public static final String
	 */

	//help strings
	public static final String PARAMS_NOT_FOUND = "Parameters not found!";
	
	// Locations
	private static Set<String> locationConceptsList;

	public static void initLocationConceptsList() {
		locationConceptsList = new HashSet<String>();

		locationConceptsList.add("Location");

		// present in ontology
		locationConceptsList.add("Toilet");
		locationConceptsList.add("Store");
		locationConceptsList.add("Restaurant");
		locationConceptsList.add("MovieTheater");

		// not present in ontology
		locationConceptsList.add("Stairs");
		locationConceptsList.add("Bank");
		locationConceptsList.add("Escalator");
		locationConceptsList.add("Elevator");

	}

	public static Set<String> getLocationConceptsList() {
		if(locationConceptsList == null){
			initLocationConceptsList();
		}
		return locationConceptsList;
	}

	// products
	private static Set<String> productInstancesList;

	public static void initProductInstancesList() {

		productInstancesList = new HashSet<String>();
		
		// present in stores
		productInstancesList.add("MaleClothing");
		productInstancesList.add("FemaleClothing");
		productInstancesList.add("Shoes");
		productInstancesList.add("Books");

		// not present in stores
		productInstancesList.add("Toys");
		productInstancesList.add("Electronics");
	}

	public static Set<String> getProductInstancesList() {
		if(productInstancesList == null){
			initProductInstancesList();
		}
		return productInstancesList;
	}

	// food
	private static Set<String> foodInstancesList;

	private static void initFoodInstancesList() {

		foodInstancesList = new HashSet<String>();
		
		// present in restaurants
		foodInstancesList.add("Vegan");
		foodInstancesList.add("Pizza");
		foodInstancesList.add("Pasta");
		foodInstancesList.add("Burger");

		// not present in restaurants
		foodInstancesList.add("AsianFood");
	}

	public static Set<String> getFoodInstancesList() {
		if(foodInstancesList == null){
			initFoodInstancesList();
		}
		return foodInstancesList;
	}

	public static String defaultIRI(String iri) {
		String resp = "_\"";
		resp += iri;
		resp += "\"";
		return resp;
	}

}
