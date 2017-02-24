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
	/*
	 * public static final String public static final String public static final
	 * String public static final String
	 */

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
		return locationConceptsList;
	}

	// products
	private static Set<String> productInstancesList;

	public static void initProductInstancesList() {

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
		return productInstancesList;
	}

	// food
	private static Set<String> foodInstancesList;

	public static void initFoodInstancesList() {

		// present in restaurants
		foodInstancesList.add("Vegan");
		foodInstancesList.add("Pizza");
		foodInstancesList.add("Pasta");
		foodInstancesList.add("Burger");

		// not present in restaurants
		foodInstancesList.add("AsianFood");
	}

	public static Set<String> getFoodInstancesList() {
		return foodInstancesList;
	}
	
	/*// location instances
		private static Set<String> locationInstancesList;

		public static void initLocationInstancesList() {

			// present in ontology
			foodInstancesList.add("Vegan");
			foodInstancesList.add("Pizza");
			foodInstancesList.add("Pasta");
			foodInstancesList.add("Burger");

			// not present in ontology
		}

		public static Set<String> getLocationInstancesList() {
			return locationInstancesList;
		}*/

}
