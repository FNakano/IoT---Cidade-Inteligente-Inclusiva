package tcc.keywords;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by minato on 26/01/17.
 */

public class SpeechRecogKeywordsRepository {

   /* public final String SEARCH_KEYWORD = "search";
    public final String LOCATION_KEYWORD = "location";
    public final String RESTAURANT_KEYWORD = "restaurant";
    public final String TOILET_KEYWORD = "toilet";
    public final String STORE_KEYWORD = "store";
    public final String MOVIES_KEYWORD = "movies";
    public final String LIKE_KEYWORD = "like";
    public final String NEXT_KEYWORD = "next";
    public final String PREVIOUS_KEYWORD = "previous";
    public final String ROUTE_KEYWORD = "route";
    public final String DISLIKE_KEYWORD = "dislike";
    public final String CURRENT_KEYWORD = "current";
    public final String DESCRIPTION_KEYWORD = "description";


    public final String LOCATION_ONTOLOGY_KEYWORD = "Location";
    public final String RESTAURANT_ONTOLOGY_KEYWORD = "restaurant";
    public final String TOILET_KEYWORD = "toilet";
    public final String STORE_KEYWORD = "store";
    public final String MOVIES_KEYWORD = "movies";
    public final String NEXT_KEYWORD = "next";
    public final String PREVIOUS_KEYWORD = "previous";
    public final String ROUTE_KEYWORD = "route";
    public final String CURRENT_KEYWORD = "current";
    public final String DESCRIPTION_KEYWORD = "description";*/

    public static final String SEARCH_KEYWORD = "search";
    public static final String LIKE_KEYWORD = "like";
    public static final String DISLIKE_KEYWORD = "dislike";
    public static final String ROUTE_KEYWORD = "route";
    public static final String NEXT_KEYWORD = "next";
    public static final String PREVIOUS_KEYWORD = "previous";
    public static final String DESCRIPTION_KEYWORD = "description";
    public static final String INSTRUCTION_KEYWORD = "instruction";

    private static Map<String, String> searchCommandList;

    public static void initSearchCommandList() {

        searchCommandList = new HashMap<String, String>();

        searchCommandList.put("location", "Location");

        searchCommandList.put("toilet","Toilet");
        searchCommandList.put("store", "Store");
        searchCommandList.put("restaurant", "Restaurant");
        searchCommandList.put("movie theater", "MovieTheater");


        searchCommandList.put("female clothing", "FemaleClothing");
        searchCommandList.put("male clothing", "MaleClothing");
        searchCommandList.put("shoes", "Shoes");
        searchCommandList.put("books", "Books");

        searchCommandList.put("vegan", "Vegan");
        searchCommandList.put("pizza", "Pizza");
        searchCommandList.put("pasta", "Pasta");
        searchCommandList.put("burger", "Burger");


        // not present in ontology
        searchCommandList.put("toys", "Toys");
        searchCommandList.put("electronics", "Electronics");
        searchCommandList.put("stairs", "Stairs");
        searchCommandList.put("bank", "Bank");
        searchCommandList.put("escalator", "Escalator");
        searchCommandList.put("elevator", "Elevator");
        searchCommandList.put("asian food", "AsianFood");

    }

    public static Map<String, String> getSearchCommandList() {
        if(searchCommandList == null){
            initSearchCommandList();
        }
        return searchCommandList;
    }


    /*public static void initCommandsList() {

        commandsList = new HashSet<String>();

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
    }*/
}
