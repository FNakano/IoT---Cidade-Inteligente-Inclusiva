package tcc.tcc;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tcc.keywords.KeywordsRepository;
import tcc.keywords.SpeechRecogKeywordsRepository;
import tcc.post.GoalCreator;
import tcc.post.HTTPInput;
import tcc.post.JSONCreator;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    ServerSocket serverSocket;
    static String currentNode;
    static JSONObject currentLocation;
    static int searchIndex = -1;
    static JSONArray searchResponse;
    static int routeIndex = -1;
    static JSONArray routeResponse;

    static JSONArray currentNodeResponse;

    static JSONObject selectedLocation;

    static private boolean onRoute;
    static String nextRouteNode;



    private TextView mText;
    private SpeechRecognizer sr;
    private static final String TAG = "MyActivity";
    private static final int REQUEST_AUDIO = 0;
    TextToSpeech speecher;

    //final String searchURL = "http://192.168.1.30:8080/TCC_Teste/search";
    //final String routeURL = "http://192.168.1.30:8080/TCC_Teste/route";
    //final String favoriteURL = "http://192.168.1.30:8080/TCC_Teste/favorite";
    final String loginURL = "http://192.168.1.34:8080/TCC/login";
    final String WSMLServiceURL = "http://192.168.1.34:8080/wsmlservices/lwrulediscovery";

    View mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        onRoute = false;
        publishConnection();
        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
        System.out.println("socket   "+ socketServerThread.isAlive());


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            // Camera permission has not been granted.

            requestVoicePermission();

        } else {
            Log.i(TAG,
                    "AUDIO permission has already been granted.");
        }

        speecher = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    speecher.setLanguage(Locale.US);
                }
            }
        });

        Button speakButton = (Button) findViewById(R.id.speakButton);
        speakButton.setOnClickListener(this);


        sr = SpeechRecognizer.createSpeechRecognizer(this);
        sr.setRecognitionListener(new listener());

    }

    private void requestVoicePermission() {
        Log.i(TAG, "voice permission has NOT been granted. Requesting permission.");

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.RECORD_AUDIO)) {
            Log.i(TAG,
                    "Displaying camera permission rationale to provide additional context.");
            Snackbar.make(mLayout, "Permission to record audio.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    REQUEST_AUDIO);
                        }
                    })
                    .show();
        } else {

            // Camera permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_AUDIO);
        }
        // END_INCLUDE(camera_permission_request)
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void commandParser(ArrayList<String> data) {
        TextView et = (TextView) findViewById(R.id.textView1);
        for (String s: data){
            System.out.println(s);
        }
        for (String s : data) {
            s = s.toLowerCase();
            if (s.toLowerCase().contains(SpeechRecogKeywordsRepository.SEARCH_KEYWORD.toLowerCase())) {

                String text = SpeechRecogKeywordsRepository.SEARCH_KEYWORD;

                et.setText(SpeechRecogKeywordsRepository.SEARCH_KEYWORD);

                Map<String, String> map = SpeechRecogKeywordsRepository.getSearchCommandList();


                String compareString = s.toLowerCase();
                compareString = compareString.replace(" ", "");
                compareString = compareString.replace(SpeechRecogKeywordsRepository.SEARCH_KEYWORD.toLowerCase(), "");

                System.out.println("compare "+compareString);

                if(compareString.equals("here")){
                    if(currentNodeResponse == null){
                        speakInfo("Current location not found.");
                        System.out.println(currentNode);
                        System.out.println(currentLocation);
                        System.out.println(currentNodeResponse);
                        return;
                    }
                    searchResponse(currentNodeResponse.toString());
                }

                if(compareString.equals("all")){
                    String goal = GoalCreator.searchLocationByParam("Location");
                    JSONObject json = JSONCreator.createJSONGoal(goal);
                    //HTTPInput i = new HTTPInput(searchURL, json);
                    System.out.println("all");
                    //new ATSearchCommand().execute(i);
                    String resp = runService(json);
                    searchResponse(resp);
                    return;
                }

                if(compareString.contains("favorite")){
                    System.out.println("favorite");
                    String goal = GoalCreator.searchFavorites(true);
                    JSONObject json = JSONCreator.createJSONGoal(goal);
                    //HTTPInput i = new HTTPInput(searchURL, json);
                    //new ATSearchCommand().execute(i);
                    //postFav(fav);
                    String resp = runService(json);
                    searchResponse(resp);
                    return;
                }

                for(String key: map.keySet()){

                    //if (s.toLowerCase().trim().contains(key.toLowerCase())) {
                    //if (s.toLowerCase().contains(key.toLowerCase())) {
                    if(compareString.equals(key.trim().replace(" ", "").toLowerCase())){
                        /*System.out.println("Foi + "+s+" "+key);
                        text += key +"     "+map.get(key);
                        et.setText(text);
                        speecher.speak(text, TextToSpeech.QUEUE_FLUSH, null);*/
                        System.out.println(map.get(key));
                        String goal = GoalCreator.searchLocationByParam(map.get(key));
                        JSONObject json = JSONCreator.createJSONGoal(goal);
                        //HTTPInput i = new HTTPInput(searchURL, json);
                        //new ATSearchCommand().execute(i);
                        String resp = runService(json);
                        searchResponse(resp);
                        return;
                        //break;
                    }


                }
            }
            if (s.contains(SpeechRecogKeywordsRepository.DISLIKE_KEYWORD.toLowerCase())) {
                et.setText(SpeechRecogKeywordsRepository.DISLIKE_KEYWORD.toLowerCase());

                System.out.println(SpeechRecogKeywordsRepository.DISLIKE_KEYWORD.toLowerCase());
                favoriteCommand(false);
                return;
            }
            if (s.contains(SpeechRecogKeywordsRepository.LIKE_KEYWORD.toLowerCase())) {
                et.setText(SpeechRecogKeywordsRepository.LIKE_KEYWORD.toLowerCase());
                favoriteCommand(true);
                return;
            }
            if (s.contains(SpeechRecogKeywordsRepository.NEXT_KEYWORD)) {
                et.setText(SpeechRecogKeywordsRepository.NEXT_KEYWORD);
                nextCommand();
                return;
            }
            if (s.contains(SpeechRecogKeywordsRepository.PREVIOUS_KEYWORD)) {
                et.setText(SpeechRecogKeywordsRepository.PREVIOUS_KEYWORD);
                //speecher.speak(previous, TextToSpeech.QUEUE_FLUSH, null);
                previousCommand();
                return;
            }
            if (s.contains(SpeechRecogKeywordsRepository.ROUTE_KEYWORD)) {

                if(s.contains(SpeechRecogKeywordsRepository.INSTRUCTION_KEYWORD)){
                    getRouteInstruction();
                    return;
                }

                //speecher.speak(route, TextToSpeech.QUEUE_FLUSH, null);
                et.setText(SpeechRecogKeywordsRepository.ROUTE_KEYWORD);
                routeCommand();
                return;
            }


            if (s.contains(SpeechRecogKeywordsRepository.DESCRIPTION_KEYWORD)) {
                //speecher.speak(route, TextToSpeech.QUEUE_FLUSH, null);
                //et.setText(route);
                getDescription();
                return;
            }

        }
    }

    public void searchCommand(){
        //
    }

    public void searchResponse(String resp){
        if(resp == null){
            speecher.speak("No location found.", TextToSpeech.QUEUE_FLUSH, null);
            searchResponse = null;
            selectedLocation = null;
            updateSelectedLocation();
            searchIndex = -1;
            return;
        }
        try {
            searchResponse = new JSONArray(resp);
            if(searchResponse.length() <= 0){
                speecher.speak("No location found.", TextToSpeech.QUEUE_FLUSH, null);
                searchResponse = null;
                selectedLocation = null;
                updateSelectedLocation();
                searchIndex = -1;
                return;
            }
            selectedLocation = searchResponse.getJSONObject(0);
            searchIndex = 0;
            updateSelectedLocation();
            speakInfo(JSONCreator.getJSONInfo(selectedLocation, "location"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void currentNodeResponse(String resp) {


        if(resp == null){
            currentNodeResponse = null;
            currentLocation = null;
            currentNode = null;
            return;
        }
        try {
            JSONArray jArray = new JSONArray(resp);

            System.out.println("ASDFASDFAS current "+jArray);

            currentNodeResponse = jArray;
            refreshLocation(currentNodeResponse);
            if(onRoute) {
                routeUser();

            }else {
                offerFavorite();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void favoriteCommand(boolean b){
        if(selectedLocation == null){
            speakInfo("No Location Selected");
            return;
        }
        String goal = null;
            goal = GoalCreator.favoriteLocation(JSONCreator.getJSONInfo(selectedLocation, "location"), b);
            JSONObject json = JSONCreator.createJSONGoal(goal);
            String resp = runService(json);
            favoriteResponse(resp);
            //HTTPInput i = new HTTPInput(favoriteURL, json);
            //new ATFavorite().execute(i);
    }

    public void favoriteResponse(String resp){
        if(resp == null){
            speecher.speak("No location found.", TextToSpeech.QUEUE_FLUSH, null);
            searchResponse = null;
            selectedLocation = null;
            updateSelectedLocation();
            searchIndex = -1;
            return;
        }
        try {
            searchResponse = new JSONArray(resp);
            if(searchResponse.length() <= 0){
                speecher.speak("No location found.", TextToSpeech.QUEUE_FLUSH, null);
                searchResponse = null;
                selectedLocation = null;
                updateSelectedLocation();
                searchIndex = -1;
                return;
            }
            selectedLocation = searchResponse.getJSONObject(0);
            searchIndex = 0;
            updateSelectedLocation();

            if(selectedLocation.getString("node").equals(currentNode)){
                currentNodeResponse = searchResponse;
                refreshLocation(currentNodeResponse);
            }
            if(JSONCreator.isFavorite(selectedLocation)){
                speakInfo("Location favorited.");
            }else{
                speakInfo("Location unfavorited.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //speakInfo("Location");
    }

    public void routeCommand(){
        if(selectedLocation == null){
            speakInfo("No Location Selected");
            return;
        }
        if(currentLocation == null){
            speakInfo("Current location not found");
            return;
        }

        try {
            routeResponse = null;
            routeIndex = -1;
            nextRouteNode = null;
            String sNode = currentLocation.getString("node");
            String eNode = selectedLocation.getString("node");
            String goal = null;
            goal = GoalCreator.findRoute(sNode, eNode);
            JSONObject json = JSONCreator.createJSONGoal(goal);
            //HTTPInput i = new HTTPInput(routeURL, json);
            //new ATRoute().execute(i);
            String resp = runService(json);
            routeResponse(resp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void routeResponse(String resp){
        if(resp == null){
            speecher.speak("No route found.", TextToSpeech.QUEUE_FLUSH, null);
            routeResponse = null;
            routeIndex = -1;
            onRoute = false;
            nextRouteNode = null;
            return;
        }
        try {
            routeResponse = new JSONArray(resp);
            System.out.println(routeResponse);
            if(routeResponse.length() <= 0){
                speecher.speak("No route found.", TextToSpeech.QUEUE_FLUSH, null);
                routeResponse = null;
                routeIndex = -1;
                onRoute = false;
                nextRouteNode = null;
                return;
            }
            //selectedLocation = searchResponse.getJSONObject(0);
            routeIndex = 0;
            onRoute = true;
            nextRouteNode = currentNode;
            routeUser();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //speakInfo("Location");
    }

    public void routeUser(){
        if (!onRoute || routeResponse == null || routeResponse.length() == 0 || routeIndex < 0 ||
                currentNode == null || nextRouteNode == null) {

            nextRouteNode = null;
            routeResponse = null;
            routeIndex = -1;
            onRoute = false;
            speakInfo("Route not found");

            return;
        }else{
            try {
                if(!currentNode.equals(nextRouteNode)){
                    nextRouteNode = null;
                    routeResponse = null;
                    routeIndex = -1;
                    onRoute = false;
                    speakInfo("You went out of route.");
                    return;
                }

                if(routeIndex == routeResponse.length()){
                    nextRouteNode = null;
                    routeResponse = null;
                    routeIndex = -1;
                    onRoute = false;
                    speakInfo("You've arrived.");
                    return;
                }


                JSONObject node = routeResponse.getJSONObject(routeIndex);
                speakInfo(node.getString("desc"));
                if(node.getString("endNode").equals("current")){
                    nextRouteNode = null;
                    routeResponse = null;
                    routeIndex = -1;
                    onRoute = false;
                }else{
                    nextRouteNode = node.getString("endNode");
                    routeIndex++;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void getRouteInstruction(){

        System.out.println(onRoute);
        System.out.println(routeResponse);
        System.out.println(currentNodeResponse);
        System.out.println(currentLocation);
        System.out.println(routeIndex);
        System.out.println(currentNode);
        System.out.println(nextRouteNode);
        System.out.println();

        if (!onRoute || routeResponse == null || routeResponse.length() == 0 || routeIndex < 0 ||
                currentNode == null || nextRouteNode == null) {

            nextRouteNode = null;
            routeResponse = null;
            routeIndex = -1;
            onRoute = false;
            speakInfo("Route not found");

            return;
        }

        try {


            JSONObject node = routeResponse.getJSONObject(routeIndex-1);
            speakInfo(node.getString("desc"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void searchCurrent(){
        if(currentLocation != null){
            selectedLocation = currentLocation;
            refreshSelectedInfo();
            speecher.speak("Location "+JSONCreator.getJSONInfo(selectedLocation, "location"), TextToSpeech.QUEUE_FLUSH, null);
        } else {
            speecher.speak("Location not found.", TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public void getDescription(){
        if(selectedLocation != null){
            speecher.speak(JSONCreator.getJSONInfo(selectedLocation, "description"), TextToSpeech.QUEUE_FLUSH, null);
            speecher.speak("Node "+JSONCreator.getNodeId(JSONCreator.getJSONInfo(selectedLocation, "node")), TextToSpeech.QUEUE_ADD, null);
            if(JSONCreator.isFavorite(selectedLocation)){
                speecher.speak("Favorited", TextToSpeech.QUEUE_ADD, null);
            }else{
                speecher.speak("Not favorited", TextToSpeech.QUEUE_ADD, null);
            }
        } else {
            speecher.speak("No location selected.", TextToSpeech.QUEUE_ADD, null);
        }
    }



    public void nextCommand(){
        if(searchResponse == null || searchResponse.length() == 0){
            speecher.speak("No searched information.", TextToSpeech.QUEUE_FLUSH, null);
            return;
        }
        if(searchIndex >= searchResponse.length()-1){
            speakInfo("End of the list.");
        }else{
            searchIndex++;
            try {
                selectedLocation = searchResponse.getJSONObject(searchIndex);
                updateSelectedLocation();
                speakInfo(JSONCreator.getJSONInfo(selectedLocation, "location"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void previousCommand(){
        if(searchResponse == null || searchResponse.length() == 0){
            speecher.speak("No searched information.", TextToSpeech.QUEUE_FLUSH, null);
            return;
        }
        if(searchIndex <= 0){
            speakInfo("End of the list.");
        }else{
            searchIndex--;
            try {
                selectedLocation = searchResponse.getJSONObject(searchIndex);
                updateSelectedLocation();
                speakInfo(JSONCreator.getJSONInfo(selectedLocation, "location"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String runService(JSONObject goal){
        String resp = null;
        HTTPInput i = new HTTPInput(WSMLServiceURL, goal);
        try {
            String uri = new ATWSMLServiceConnection().execute(i).get();
            JSONObject j = new JSONObject(uri);
            uri = j.getString("wsmlCode");
            //i= new HTTPInput(uri, goal);
            i= new HTTPInput(uri, goal);
            resp = new ATSearchCurrent().execute(i).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resp;
    }

    public void speakInfo(String info){
        speecher.speak(info, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void newNode(String node) {
        TextView txt = (TextView) findViewById(R.id.currentNode);
        currentNode = node;
        //txt.setText("Current Node: "+JSONCreator.getNodeId(node));
        txt.setText("Current Node: "+currentNode);
        String goal = GoalCreator.searchLocationByNode(currentNode);


        System.out.println(goal);
        System.out.println(node);

        //String s = new ATSearchTest().execute(i);




        JSONObject json = JSONCreator.createJSONGoal(goal);
        //HTTPInput i = new HTTPInput(searchURL, json);

        String resp = runService(json);
        currentNodeResponse(resp);


        //System.out.println("dafasdhfha"+runService(json));
        //ATServerConnection at = new ATServerConnection();
        //new ATSearchCurrent().execute(i);

        System.out.println(currentNode);
        System.out.println(currentLocation);
        System.out.println(currentNodeResponse);
    }

    public void updateSelectedLocation(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                /*TextView location = (TextView)findViewById(R.id.selectedLocation);
                location.setText(JSONCreator.getJSONInfo(selectedLocation, "location"));
                TextView node = (TextView)findViewById(R.id.selectedNode);
                node.setText(JSONCreator.getJSONInfo(selectedLocation, "node"));
                TextView fav = (TextView)findViewById(R.id.selectedFav);
                fav.setText(JSONCreator.getJSONInfo(selectedLocation, "fav"));
                */

                if(selectedLocation == null){
                    speakInfo("No selected location");
                    TextView txt = (TextView)findViewById(R.id.selectedLocation);
                    txt.setText("Location: " + "None");
                    txt = (TextView)findViewById(R.id.selectedNode);
                    txt.setText("Node: " + "None");
                    txt.setText("Favorited: Yes");
                    return;
                }

                TextView txt = (TextView)findViewById(R.id.selectedLocation);
                txt.setText("Location: " +JSONCreator.getJSONInfo(selectedLocation, "location"));
                txt = (TextView)findViewById(R.id.selectedNode);
                txt.setText("Node: " +JSONCreator.getNodeId(JSONCreator.getJSONInfo(selectedLocation, "node")));
                txt = (TextView)findViewById(R.id.selectedFav);
                if(JSONCreator.isFavorite(selectedLocation)){
                    txt.setText("Favorited: Yes")
                    ;
                }else{
                    txt.setText("Favorited: No");
                }


                //refreshSelectedInfo();
                //speakInfo(JSONCreator.getJSONInfo(selectedLocation, "location"));

            }
        });
    }

    /*public void refreshLocation(String output){

        System.out.println("ENTROU");
        JSONArray jArray = null;
        JSONObject jsonObj = null;
        try {
            jArray = new JSONArray(output);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            jsonObj = jArray.getJSONObject(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        currentLocation =jsonObj;
        TextView txt = (TextView)findViewById(R.id.currentLocation);
        txt.setText("Location: " +JSONCreator.getJSONInfo(currentLocation, "location"));
        System.out.println(JSONCreator.getJSONInfo(currentLocation, "location"));
        System.out.println(txt.getText());
        System.out.println("SAIU");
    }*/

    public void refreshLocation(JSONArray node){

        //runOnUiThread(new Runnable() {
        //    @Override
            //public void run() {
                // TODO Auto-generated method stub
                JSONObject jsonObj = null;
                try {
                    //jsonObj = node.getJSONObject(0);
                    jsonObj = currentNodeResponse.getJSONObject(0);
                    currentLocation =jsonObj;
                    TextView txt = (TextView)findViewById(R.id.currentLocation);
                    txt.setText("Location: " +JSONCreator.getJSONInfo(currentLocation, "location"));


                    System.out.println("SAIUUUUUUUU");

                    System.out.println(currentNode);
                    System.out.println(currentLocation);
                    System.out.println(currentNodeResponse);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

          //  }
        //});



        /*JSONObject jsonObj = null;
        try {
            jsonObj = jArray.getJSONObject(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        currentLocation =jsonObj;
        TextView txt = (TextView)findViewById(R.id.currentLocation);
        txt.setText("Location: " +JSONCreator.getJSONInfo(currentLocation, "location"));
        */
    }

    public void refreshSelectedInfo(){
        if(selectedLocation == null){
            speakInfo("No selected location");
            TextView txt = (TextView)findViewById(R.id.selectedLocation);
            txt.setText("Location: " + "None");
            txt = (TextView)findViewById(R.id.selectedNode);
            txt.setText("Node: " + "None");
                txt.setText("Favorited: Yes");
            return;
        }

        TextView txt = (TextView)findViewById(R.id.selectedLocation);
        txt.setText("Location: " +JSONCreator.getJSONInfo(selectedLocation, "location"));
        txt = (TextView)findViewById(R.id.selectedNode);
        txt.setText("Node: " +JSONCreator.getNodeId(JSONCreator.getJSONInfo(selectedLocation, "node")));
        txt = (TextView)findViewById(R.id.selectedFav);
        if(JSONCreator.isFavorite(selectedLocation)){
            txt.setText("Favorited: Yes")
            ;
        }else{
            txt.setText("Favorited: No");
        }

    }

    public void offerFavorite(){
        if(!onRoute){
            //String b = JSONCreator.getJSONInfo(currentLocation, "fav");
            //boolean bool = Boolean.parseBoolean(b);
            boolean bool = JSONCreator.isFavorite(currentLocation);
            if(bool){
                String loc = JSONCreator.getJSONInfo(currentLocation, "location");
                speecher.speak("Current location "+loc+" is favorite", TextToSpeech.QUEUE_FLUSH, null);
            }
        }
    }

    public void publishConnection() {
        JSONObject json = JSONCreator.createLoginJson(JSONCreator.getIpAddress(), 8080);
        HTTPInput i = new HTTPInput(loginURL, json);
        new ATLogin().execute(i);
    }

    public void onClick2(View v) {
        String goal = GoalCreator.searchLocationByParam("Location");
        JSONObject obj = JSONCreator.createJSONGoal(goal);
        HTTPInput i = new HTTPInput(WSMLServiceURL, obj);

        WSMLServiceSearch(i);

    }

    public void WSMLServiceSearch(HTTPInput i){
        try {


            System.out.println(i.getJson().toString());
            System.out.println("SFDHADS       "+i.getUrl());

            String s = new ATWSMLServiceConnection().execute(i).get();
            System.out.println("SFDHADSFHASDAFSH       "+s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void WSMLServiceResponse(String s){
        if(s == null){
            speakInfo("Server is down.");
        }else{
            try {
                //JSONArray jArray = new JSONArray(s);
                JSONObject ur = new JSONObject(s);
                String uri = ur.getString("wsmlCode");
                if(uri.contains("search")){

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class listener implements RecognitionListener {

        public void onReadyForSpeech(Bundle params) {
            Log.d(TAG, "onReadyForSpeech");
        }

        public void onBeginningOfSpeech() {
            Log.d(TAG, "onBeginningOfSpeech");
        }

        public void onRmsChanged(float rmsdB) {
            Log.d(TAG, "onRmsChanged");
        }

        public void onBufferReceived(byte[] buffer) {
            Log.d(TAG, "onBufferReceived");
        }

        public void onEndOfSpeech() {
            Log.d(TAG, "onEndofSpeech");
        }

        public void onError(int error) {
            Log.d(TAG, "error " + error);
            //mText.setText("error " + error);
            System.out.println("ERROR: "+error);
        }

        public void onResults(Bundle results) {
            String str = new String();
            //Log.d(TAG, "onResults " + results);
            ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String res = "results: ";
            for (int i = 0; i < data.size(); i++) {
                //Log.d(TAG, "result " + data.get(i));
                res += data.get(i);
                str += data.get(i);
            }
            //mText.setText("results: "+String.valueOf(data.size())):
            //mText.setText(res);
            commandParser(data);
        }

        public void onPartialResults(Bundle partialResults) {
            Log.d(TAG, "onPartialResults");
        }

        public void onEvent(int eventType, Bundle params) {
            Log.d(TAG, "onEvent " + eventType);
        }
    }

    public void onClick(View v) {
        if (v.getId() == R.id.speakButton) {

            System.out.println("RUN");

            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "voice.recognition.test");

            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
            sr.startListening(intent);
            //Log.i("111111","11111111");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_AUDIO) {
            Log.i(TAG, "Received response for AUDIO permission request.");

            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "AUDIO permission has now been granted.");
                Snackbar.make(mLayout, "Permission granted",    //isso da exception
                        Snackbar.LENGTH_SHORT).show();
            } else {
                Log.i(TAG, "AUDIO permission was NOT granted.");
                Snackbar.make(mLayout, "Permission not granted",
                        Snackbar.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private class SocketServerThread extends Thread {
        static final int SocketServerPORT = 8080;
        @Override
        public void run() {
            Socket socket = null;
            DataInputStream dataInputStream = null;

            System.out.println("Socket here");

            try {
                serverSocket = new ServerSocket(SocketServerPORT);

                System.out.println("SERVER SOCKET"+serverSocket.toString());

                while (true) {

                    System.out.println("HILE TRUE");

                    socket = serverSocket.accept();

                    System.out.println("ACCEPT");

                    dataInputStream = new DataInputStream(
                            socket.getInputStream());

                    System.out.println("INUT STREAM");

                    String messageFromClient = "";
                    messageFromClient = dataInputStream.readUTF();
                    currentNode = messageFromClient;

                    System.out.println("RUN THREAD BEGGINGN");

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            System.out.println("RUN");
                            newNode(currentNode);
                        }
                    });
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                /*final String errMsg = e.toString();
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        msg.setText(errMsg);
                    }
                });*/
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                if (dataInputStream != null) {
                    try {
                        dataInputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public class ATLogin extends AsyncTask<HTTPInput, Void, String> {

        @Override
        protected String doInBackground(HTTPInput... params) {
            URL url;
            HttpURLConnection urlConnection;
            String page = null;
            HTTPInput httpInput;
            try {
                httpInput = params[0];
                url = new URL(httpInput.getUrl());
                //url = new URL("http://www.google.com");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.connect();
                String input = httpInput.getJson().toString();
                OutputStream os = urlConnection.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                osw.write(input);
                osw.flush();
                osw.close();

                BufferedReader in =
                        new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                page = "";
                String inLine;
                while ((inLine = in.readLine()) != null) {
                    page += inLine;
                }
                in.close();

                urlConnection.disconnect();
                System.out.println("Foi?");
            } catch (Exception e) {
                System.err.println(e);
            }
            return page;
        }

        @Override
        protected void onPostExecute(String result) {
            System.out.println("RESULT: "+result);
        }

        @Override
        protected void onPreExecute() {


        }

    }


    public class ATWSMLServiceConnection extends AsyncTask<HTTPInput, Void, String> {
        @Override
        protected String doInBackground(HTTPInput... params) {
            URL url;
            HttpURLConnection urlConnection;
            String page = null;
            HTTPInput httpInput;
            try {
                httpInput = params[0];
                url = new URL(httpInput.getUrl());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.connect();
                String input = httpInput.getJson().toString();
                OutputStream os = urlConnection.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                osw.write(input);
                osw.flush();
                osw.close();
                BufferedReader in =
                        new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                page = "";
                String inLine;
                while ((inLine = in.readLine()) != null) {
                    page += inLine;
                }
                in.close();
                urlConnection.disconnect();
                System.out.println("Foi?");
                System.out.println("PAGE    "+page);
            } catch (Exception e) {
                System.err.println(e);
            }
            return page;
        }
        @Override
        protected void onPostExecute(String result) {
            System.out.println("RESULT    "+result);
            WSMLServiceResponse(result);
        }
        @Override
        protected void onPreExecute() {
        }
    }


    /*public class ATServerConnection extends AsyncTask<HTTPInput, Void, String> {
        @Override
        protected String doInBackground(HTTPInput... params) {
            URL url;
            HttpURLConnection urlConnection;
            String page = null;
            HTTPInput httpInput;
            try {
                httpInput = params[0];
                url = new URL(httpInput.getUrl());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.connect();
                String input = httpInput.getJson().toString();
                OutputStream os = urlConnection.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                osw.write(input);
                osw.flush();
                osw.close();
                BufferedReader in =
                        new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                page = "";
                String inLine;
                while ((inLine = in.readLine()) != null) {
                    page += inLine;
                }
                in.close();
                urlConnection.disconnect();
                System.out.println("Foi?");
            } catch (Exception e) {
                System.err.println(e);
            }
            return page;
        }
        @Override
        protected void onPostExecute(String result) {
            if(result == null){
                //serverResponse = null;
                System.out.println("RETONOU NULL");
                return;
            }
            //refreshLocation(result);
            if(onRoute){
                routeUser();
            }else {
                offerFavorite();
            }
            //refreshLocation(result);
            //offerFavorite();
            /*try {
                //sserverResponse = new JSONArray(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }*
        }
        @Override
        protected void onPreExecute() {
        }
    }*/

    public class ATSearchCurrent extends AsyncTask<HTTPInput, Void, String> {
        @Override
        protected String doInBackground(HTTPInput... params) {
            URL url;
            HttpURLConnection urlConnection;
            String page = null;
            HTTPInput httpInput;
            try {
                httpInput = params[0];
                url = new URL(httpInput.getUrl());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.connect();
                String input = httpInput.getJson().toString();
                OutputStream os = urlConnection.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                osw.write(input);
                osw.flush();
                osw.close();
                BufferedReader in =
                        new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                page = "";
                String inLine;
                while ((inLine = in.readLine()) != null) {
                    page += inLine;
                }
                in.close();
                urlConnection.disconnect();
                System.out.println("Foi?");
            } catch (Exception e) {
                System.err.println(e);
            }
            return page;
        }
        @Override
        protected void onPostExecute(String result) {
            //currentNodeResponse(result);
            /*System.out.println("--------------SearchCuurent--------------------");
            System.out.println(result);
            System.out.println("--------------SearchCuurent--------------------");
            try {
                serverResponse = new JSONArray(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            refreshLocation(result);
            offerFavorite();
*/
        }
        @Override
        protected void onPreExecute() {
        }
    }
    public class ATSearchCommand extends AsyncTask<HTTPInput, Void, String> {
        @Override
        protected String doInBackground(HTTPInput... params) {
            URL url;
            HttpURLConnection urlConnection;
            String page = null;
            HTTPInput httpInput;
            try {
                httpInput = params[0];
                url = new URL(httpInput.getUrl());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.connect();
                String input = httpInput.getJson().toString();
                OutputStream os = urlConnection.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                osw.write(input);
                osw.flush();
                osw.close();
                BufferedReader in =
                        new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                page = "";
                String inLine;
                while ((inLine = in.readLine()) != null) {
                    page += inLine;
                }
                in.close();
                urlConnection.disconnect();
                System.out.println("Foi?");
            } catch (Exception e) {
                System.err.println(e);
            }
            return page;
        }
        @Override
        protected void onPostExecute(String result) {
            searchResponse(result);

        }
        @Override
        protected void onPreExecute() {
        }
    }

    public class ATFavorite extends AsyncTask<HTTPInput, Void, String> {
        @Override
        protected String doInBackground(HTTPInput... params) {
            URL url;
            HttpURLConnection urlConnection;
            String page = null;
            HTTPInput httpInput;
            try {
                httpInput = params[0];
                url = new URL(httpInput.getUrl());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.connect();
                String input = httpInput.getJson().toString();
                OutputStream os = urlConnection.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                osw.write(input);
                osw.flush();
                osw.close();
                BufferedReader in =
                        new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                page = "";
                String inLine;
                while ((inLine = in.readLine()) != null) {
                    page += inLine;
                }
                in.close();
                urlConnection.disconnect();
                System.out.println("Foi?");
            } catch (Exception e) {
                System.err.println(e);
            }
            return page;
        }
        @Override
        protected void onPostExecute(String result) {
            favoriteResponse(result);
        }
        @Override
        protected void onPreExecute() {
        }
    }

    public class ATRoute extends AsyncTask<HTTPInput, Void, String> {
        @Override
        protected String doInBackground(HTTPInput... params) {
            URL url;
            HttpURLConnection urlConnection;
            String page = null;
            HTTPInput httpInput;
            try {
                httpInput = params[0];
                url = new URL(httpInput.getUrl());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.connect();
                String input = httpInput.getJson().toString();
                OutputStream os = urlConnection.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                osw.write(input);
                osw.flush();
                osw.close();
                BufferedReader in =
                        new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                page = "";
                String inLine;
                while ((inLine = in.readLine()) != null) {
                    page += inLine;
                }
                in.close();
                urlConnection.disconnect();
                System.out.println("Foi?");
            } catch (Exception e) {
                System.err.println(e);
            }
            return page;
        }
        @Override
        protected void onPostExecute(String result) {
            System.out.println(result);
            routeResponse(result);
        }
        @Override
        protected void onPreExecute() {
        }
    }

    public class ATSearchTest extends AsyncTask<HTTPInput, Void, String> {
        // you may separate this or combined to caller class.

        @Override
        protected String doInBackground(HTTPInput... params) {
            URL url;
            HttpURLConnection urlConnection;
            String page = null;
            HTTPInput httpInput;
            try {
                httpInput = params[0];
                url = new URL(httpInput.getUrl());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.connect();
                String input = httpInput.getJson().toString();
                OutputStream os = urlConnection.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                osw.write(input);
                osw.flush();
                osw.close();
                BufferedReader in =
                        new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                page = "";
                String inLine;
                while ((inLine = in.readLine()) != null) {
                    page += inLine;
                }
                in.close();
                urlConnection.disconnect();
                System.out.println("Foi?");
            } catch (Exception e) {
                System.err.println(e);
            }
            return page;
        }
        @Override
        protected void onPostExecute(String result) {
            //currentNodeResponse(result);
            if(result == null){
                currentNodeResponse = null;
                currentLocation = null;
                currentNode = null;
                return;
            }
            try {
                JSONArray jArray = new JSONArray(result);
                currentNodeResponse = jArray;
                refreshLocation(currentNodeResponse);
                JSONObject jsonObj = null;
                try {
                    //jsonObj = node.getJSONObject(0);
                    jsonObj = currentNodeResponse.getJSONObject(0);
                    currentLocation =jsonObj;
                    TextView txt = (TextView)findViewById(R.id.currentLocation);
                    txt.setText("Location: " +JSONCreator.getJSONInfo(currentLocation, "location"));


                    System.out.println("SAIUUUUUUUU");

                    System.out.println(currentNode);
                    System.out.println(currentLocation);
                    System.out.println(currentNodeResponse);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(onRoute) {
                    routeUser();
                }else {
                    offerFavorite();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        protected void onPreExecute() {
        }
    }

}
