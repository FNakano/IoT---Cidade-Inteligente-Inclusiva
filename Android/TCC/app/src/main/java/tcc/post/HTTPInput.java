package tcc.post;

import org.json.JSONObject;

/**
 * Created by minato on 23/11/16.
 */

public class HTTPInput{

    public JSONObject getJson() {
        return json;
    }

    public void setJson(JSONObject json) {
        this.json = json;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private JSONObject json;
    private String url;

    public HTTPInput(String url, JSONObject json){
        this.json = json;
        this.url = url;
    }

}
