package tcc.post;

import com.ontotext.wsmo4j.common.IRIImpl;
import com.ontotext.wsmo4j.common.NamespaceImpl;
import com.ontotext.wsmo4j.factory.WsmoFactoryImpl;
import com.ontotext.wsmo4j.ontology.AxiomImpl;
import com.ontotext.wsmo4j.service.CapabilityImpl;
import com.ontotext.wsmo4j.service.GoalImpl;

import org.json.JSONException;
import org.json.JSONObject;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.ontology.Axiom;
import org.wsmo.common.IRI;
import org.wsmo.common.Namespace;
import org.wsmo.common.TopEntity;
import org.wsmo.common.WSML;
import org.wsmo.factory.DataFactory;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.service.Capability;
import org.wsmo.service.Goal;
import org.wsmo.wsml.Serializer;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Set;

import tcc.keywords.KeywordsRepository;

import static org.wsmo.common.WSML.WSML_RULE;

/**
 * Created by minato on 23/11/16.
 */

public class GoalCreator {
    private static WsmoFactory factory;
    private static LogicalExpressionFactory leFactory;
    private static DataFactory dFactory;

    public GoalCreator(){
        factory = Factory.createWsmoFactory(null);
        leFactory = Factory.createLogicalExpressionFactory(null);
    }

    public static String searchLocationByParam(String searchParam){
        Set<String> nameSet;
        //search location concept
        nameSet = KeywordsRepository.getLocationConceptsList();
        for(String s: nameSet){
            if(searchParam.equals(s)){
                String goal = GoalWriter.writeSearchGoal(s, "", "");
                return goal;
            }
        }

        //search by food
        nameSet = KeywordsRepository.getFoodInstancesList();
        for(String s: nameSet){
            if(searchParam.equals(s)){
                String attId = KeywordsRepository.HAS_FOOD_ATT;
                String concept = "Location";
                String goal = GoalWriter.writeSearchGoal(concept, attId, s);
                return goal;
            }
        }

        //search by products
        nameSet = KeywordsRepository.getProductInstancesList();
        for(String s: nameSet){
            if(searchParam.equals(s)){
                String attId = KeywordsRepository.HAS_PRODUCT_ATT;
                String concept = "Location";
                String goal = GoalWriter.writeSearchGoal(concept, attId, s);
                return goal;
            }
        }

        return KeywordsRepository.PARAMS_NOT_FOUND;
    }

    public static String searchLocationById(String id){
        String goal = GoalWriter.writeSearchGoal(id);
        return goal;
    }

    public static String searchLocationByNode(String node){
        String concept = "Location";
        String attId = KeywordsRepository.HAS_NODE_ATT;
        String goal = GoalWriter.writeSearchGoal(concept, attId, node);
        return goal;
    }

    public static String searchFavorites(boolean b){
        String concept = "Location";
        String attId = KeywordsRepository.IS_FAVORITE_ATT;
        String goal = GoalWriter.writeSearchGoal(concept, attId, Boolean.toString(b));
        return goal;
    }

    public static String favoriteLocation(String id, boolean b){
        String goal = GoalWriter.writeFavoriteGoal(id, b);
        return goal;
    }

    public static String findRoute(String startingNode, String endingNode){
        String goal = GoalWriter.writeRouteGoal(startingNode, endingNode);
        return goal;
    }


    public static String searchGoal(String[]preExp,String[]postExp){
        factory = Factory.createWsmoFactory(null);
        leFactory = Factory.createLogicalExpressionFactory(null);
        String s = null;
        try{
            s = goaltoString(createSearchGoal(preExp, postExp));
        }catch(Exception e){
            // TODO Auto-generated catch block
            e.printStackTrace();
            return e.toString();
        }
        return s;
    }

    public static String routeGoal(String[]preExp){
        factory = Factory.createWsmoFactory(null);
        leFactory = Factory.createLogicalExpressionFactory(null);
        String s = null;
        try{
            s = goaltoString(createRouteGoal(preExp));
        }catch(Exception e){
            // TODO Auto-generated catch block
            e.printStackTrace();
            return e.toString();
        }
        return s;
    }

    public static String favoriteGoal(String[]preExp){
        factory = Factory.createWsmoFactory(null);
        leFactory = Factory.createLogicalExpressionFactory(null);
        String s = null;
        try{
            s = goaltoString(createFavoriteGoal(preExp));
        }catch(Exception e){
            // TODO Auto-generated catch block
            e.printStackTrace();
            return e.toString();
        }
        return s;
    }

    public static String getURI(JSONObject json){
        String s = null;
        try {
            s = json.getString("wsmlCode");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return s;
    }


    /*public static Goal createSearchGoal(String[]preExp, String[]postExp) throws Exception {

        String GOAL_ID = "http://www.example.com/modelagem/goals#SearchGoal";

        factory = new WsmoFactoryImpl(null);

        IRI goalIRI = factory.createIRI(GOAL_ID);
        IRI discoveryIRI = factory.createIRI("http://wiki.wsmx.org/index.php?title=DiscoveryOntology#");
        IRI shoppingIRI = factory.createIRI("http://www.example.com/modelagem/ontology/Shopping#");

        //Goal goal = factory.createGoal(goalIRI);
        Goal goal = new GoalImpl(goalIRI);

        goal.setWsmlVariant(WSML.WSML_RULE);
        Namespace defaultNamespace = factory.createNamespace("",factory.createIRI("http://www.example.com/modelagem/goals#"));
        goal.setDefaultNamespace(defaultNamespace);
        Namespace discoveryNamespace = factory.createNamespace("discovery",discoveryIRI);
        goal.addNamespace(discoveryNamespace);
        Namespace shoppingNamespace = factory.createNamespace("shopping",shoppingIRI);
        goal.addNamespace(shoppingNamespace);

        goal.addOntology(factory.getOntology(factory
                .createIRI("http://www.example.com/modelagem/ontology/Shopping#ShoppingOntology")));
        Capability capability = factory.createCapability(factory.createIRI(defaultNamespace, "searchCapability"));
        Axiom axiom = null;
        LogicalExpression logExp = null;

        capability.addNFPValue(factory.createIRI(discoveryNamespace, "discoveryStrategy"),
                factory.createIRI(discoveryNamespace, "LightweightRuleDiscovery"));
        capability.addNFPValue(factory.createIRI(discoveryNamespace, "discoveryStrategy"),
                factory.createIRI(discoveryNamespace, "NoPreFilter"));


        //add capability pre-condition

        if(preExp.length > 0){
            axiom = factory.createAxiom(factory.createIRI(defaultNamespace, "searchPre"));
            String preString = "";
            preString += preExp[0];
            for(int i=1; i<preExp.length; i++) {
                preString += "\nand " + preExp[i];
            }
            logExp = leFactory.createLogicalExpression(preString+".", goal);
            axiom.addDefinition(logExp);
            capability.addPreCondition(axiom);
        }

        // add capability post-condition
        axiom = factory.createAxiom(factory.createIRI(defaultNamespace, "searchPos"));
        String stringExpression = "?location memberOf shopping#Location "
                + " and ?location[shopping#hasDescription hasValue ?desc] "
                + " and ?location[shopping#hasNode hasValue ?node] "
                + " and ?location[shopping#isFavorite hasValue ?fav] ";
        for(String s: postExp){
            stringExpression += "\nand "+s;
        }
        logExp = leFactory.createLogicalExpression(stringExpression+ ".", goal);
        axiom.addDefinition(logExp);
        capability.addPostCondition(axiom);


        goal.setCapability(capability);

        return goal;
    }*/

    public static Goal createSearchGoal(String[]preExp, String[]postExp) throws Exception {

        String GOAL_ID = "http://www.example.com/modelagem/goals#SearchGoal";

        //factory = new WsmoFactoryImpl(null);

        IRI goalIRI = new IRIImpl(GOAL_ID);
        IRI discoveryIRI = new IRIImpl("http://wiki.wsmx.org/index.php?title=DiscoveryOntology#");
        IRI shoppingIRI = new IRIImpl("http://www.example.com/modelagem/ontology/Shopping#");

        //Goal goal = factory.createGoal(goalIRI);
        Goal goal = new GoalImpl(goalIRI);

        goal.setWsmlVariant(WSML.WSML_RULE);
        Namespace defaultNamespace = new NamespaceImpl("",new IRIImpl("http://www.example.com/modelagem/goals#"));
        goal.setDefaultNamespace(defaultNamespace);
        Namespace discoveryNamespace = new NamespaceImpl("discovery",discoveryIRI);
        goal.addNamespace(discoveryNamespace);
        Namespace shoppingNamespace = new NamespaceImpl("shopping",shoppingIRI);
        goal.addNamespace(shoppingNamespace);

        goal.addOntology(factory.getOntology(new IRIImpl("http://www.example.com/modelagem/ontology/Shopping#ShoppingOntology")));
        Capability capability = new CapabilityImpl(new IRIImpl(defaultNamespace.getIRI().toString() + "searchCapability"));
        Axiom axiom = null;
        LogicalExpression logExp = null;
/*
        capability.addNFPValue(new IRIImpl(discoveryNamespace.getIRI().toString() + "discoveryStrategy"),
                new IRIImpl(discoveryNamespace + "LightweightRuleDiscovery"));
        capability.addNFPValue(new IRIImpl(discoveryNamespace.getIRI().toString() + "discoveryStrategy"),
                new IRIImpl(discoveryNamespace + "NoPreFilter"));
*/

        //add capability pre-condition

        if(preExp.length > 0){
            axiom = new AxiomImpl(new IRIImpl(defaultNamespace.getIRI().toString() + "searchPre"));
            String preString = "";
            preString += preExp[0];
            for(int i=1; i<preExp.length; i++) {
                preString += "\nand " + preExp[i];
            }
            logExp = leFactory.createLogicalExpression(preString+".", goal);
            axiom.addDefinition(logExp);
            capability.addPreCondition(axiom);
        }

        // add capability post-condition
        axiom = new AxiomImpl(new IRIImpl(defaultNamespace.getIRI().toString() + "searchPos"));
        String stringExpression = "?location memberOf shopping#Location "
                + " and ?location[shopping#hasDescription hasValue ?desc] "
                + " and ?location[shopping#hasNode hasValue ?node] "
                + " and ?location[shopping#isFavorite hasValue ?fav] ";
        for(String s: postExp){
            stringExpression += "\nand "+s;
        }
        logExp = leFactory.createLogicalExpression(stringExpression+ ".", goal);
        axiom.addDefinition(logExp);
        capability.addPostCondition(axiom);


        goal.setCapability(capability);

        return goal;
    }

    public static Goal createRouteGoal(String[]preExp) throws Exception {

        String GOAL_ID = "http://www.example.com/modelagem/goals#RouteGoal";


        IRI goalIRI = factory.createIRI(GOAL_ID);
        IRI discoveryIRI = factory.createIRI("http://wiki.wsmx.org/index.php?title=DiscoveryOntology#");
        IRI graphIRI = factory.createIRI("http://www.example.com/modelagem/ontology/Graph#");

        Goal goal = factory.createGoal(goalIRI);
        goal.setWsmlVariant(WSML.WSML_RULE);
        Namespace defaultNamespace = factory.createNamespace("",factory.createIRI("http://www.example.com/modelagem/goals#"));
        goal.setDefaultNamespace(defaultNamespace);
        Namespace discoveryNamespace = factory.createNamespace("discovery",discoveryIRI);
        goal.addNamespace(discoveryNamespace);
        Namespace graphNamespace = factory.createNamespace("graph",graphIRI);
        goal.addNamespace(graphNamespace);

        goal.addOntology(factory.getOntology(factory
                .createIRI("http://www.example.com/modelagem/ontology/Graph#GraphOntology")));
        Capability capability = factory.createCapability(factory.createIRI(defaultNamespace, "routeCapability"));
        Axiom axiom = null;
        LogicalExpression logExp = null;
        capability.addNFPValue(factory.createIRI(discoveryNamespace, "discoveryStrategy"),
                factory.createIRI(discoveryNamespace, "LightweightRuleDiscovery"));
        capability.addNFPValue(factory.createIRI(discoveryNamespace, "discoveryStrategy"),
                factory.createIRI(discoveryNamespace, "NoPreFilter"));


        if(preExp.length > 0){
            axiom = factory.createAxiom(factory.createIRI(defaultNamespace, "routePre"));
            String preString = "";
            preString += preExp[0];
            for(int i=1; i<preExp.length; i++) {
                preString += "\nand " + preExp[i];
            }
            logExp = leFactory.createLogicalExpression(preString+".", goal);
            axiom.addDefinition(logExp);
            capability.addPreCondition(axiom);
        }

        axiom = factory.createAxiom(factory.createIRI(defaultNamespace, "routePos"));
        String stringExpression = "?edge memberOf graph#Edge";
        logExp = leFactory.createLogicalExpression(stringExpression+ ".", goal);
        axiom.addDefinition(logExp);
        capability.addPostCondition(axiom);


        goal.setCapability(capability);

        return goal;
    }

    public static Goal createFavoriteGoal(String[]preExp) throws Exception {

        String GOAL_ID = "http://www.example.com/modelagem/goals#RouteGoal";


        IRI goalIRI = factory.createIRI(GOAL_ID);
        IRI discoveryIRI = factory.createIRI("http://wiki.wsmx.org/index.php?title=DiscoveryOntology#");
        IRI shoppingIRI = factory.createIRI("http://www.example.com/modelagem/ontology/Shopping#");

        Goal goal = factory.createGoal(goalIRI);
        goal.setWsmlVariant(WSML.WSML_RULE);
        Namespace defaultNamespace = factory.createNamespace("",factory.createIRI("http://www.example.com/modelagem/goals#"));
        goal.setDefaultNamespace(defaultNamespace);
        Namespace discoveryNamespace = factory.createNamespace("discovery",discoveryIRI);
        goal.addNamespace(discoveryNamespace);
        Namespace shoppingNamespace = factory.createNamespace("shopping",shoppingIRI);
        goal.addNamespace(shoppingNamespace);

        goal.addOntology(factory.getOntology(factory
                .createIRI("http://www.example.com/modelagem/ontology/Shopping#ShoppingOntology")));
        Capability capability = factory.createCapability(factory.createIRI(defaultNamespace, "FavoriteCapability"));
        Axiom axiom = null;
        LogicalExpression logExp = null;
		/*capability.addNFPValue(factory.createIRI(discoveryNamespace, "discoveryStrategy"),
				factory.createIRI(discoveryNamespace, "LightweightRuleDiscovery"));
		capability.addNFPValue(factory.createIRI(discoveryNamespace, "discoveryStrategy"),
				factory.createIRI(discoveryNamespace, "NoPreFilter"));
		 */

        if(preExp.length > 0){
            axiom = factory.createAxiom(factory.createIRI(defaultNamespace, "favoritePre"));
            String preString = "";
            preString += preExp[0];
            for(int i=1; i<preExp.length; i++) {
                preString += "\nand " + preExp[i];
            }
            logExp = leFactory.createLogicalExpression(preString+".", goal);
            axiom.addDefinition(logExp);
            capability.addPreCondition(axiom);
        }

        axiom = factory.createAxiom(factory.createIRI(defaultNamespace, "favoritePos"));
        String stringExpression = "?location memberOf shopping#Location";
        logExp = leFactory.createLogicalExpression(stringExpression+ ".", goal);
        axiom.addDefinition(logExp);
        capability.addPostCondition(axiom);


        goal.setCapability(capability);

        return goal;
    }

    public static String goaltoString(Goal goal){
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
}
