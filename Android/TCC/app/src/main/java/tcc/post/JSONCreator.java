package tcc.post;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by minato on 23/11/16.
 */

public class JSONCreator {

    public static JSONObject createSearchJson(String goal){
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("wsmlEntity", "goal");
            jsonObj.put("wsmlCode", goal);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObj;
    }

    public static JSONObject createRouteJson(String goal, int startNode, int endNode){
        JSONObject jsonObj = new JSONObject();

        try {
            jsonObj.put("wsmlEntity", "goal");
            jsonObj.put("wsmlCode", goal);
            jsonObj.put("startNode", startNode);
            jsonObj.put("endNode", endNode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObj;
    }

    public static JSONObject createFavoriteJson(String goal, String location, String isFavorite){
        JSONObject jsonObj = new JSONObject();

        try {
            jsonObj.put("wsmlEntity", "goal");
            jsonObj.put("wsmlCode", goal);
            jsonObj.put("location", location);
            jsonObj.put("isFavorite", isFavorite);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObj;
    }

    public static JSONObject createLoginJson(String ip, int port){
        JSONObject jsonObj = new JSONObject();

        try {
            jsonObj.put("ip", ip);
            jsonObj.put("port", port);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObj;
    }

    public static String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();
                    if (inetAddress.isSiteLocalAddress()) {
                        ip = inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }
        return ip;
    }

    public static String getJSONInfo(JSONObject jsonObj, String key){
        String resp = "";
        try {
            resp = jsonObj.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(!resp.isEmpty()) {
            resp = resp.substring(resp.lastIndexOf('#') + 1, resp.length());
        }
        return resp;
    }

    public static int getNodeId(String node){
        int id = 0;
        if(!node.isEmpty()) {
            node = node.substring(node.lastIndexOf('N') + 1, node.length());
        }
        id = Integer.parseInt(node);
        return id;
    }

    public static boolean isFavorite(JSONObject jsonObj){
        boolean fav = false;
        if(jsonObj != null) {
            try {
                fav = Boolean.parseBoolean(jsonObj.getString("favorite"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return fav;
    }

    public static JSONObject createJSONGoal(String goal){
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("wsmlEntity", "goal");
            jsonObj.put("wsmlCode", goal);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObj;
    }

}
