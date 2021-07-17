package webhook.embed.elements;

import webhook.json.JSONObject;
import webhook.json.JsonValue;

public class Thumbnail implements JsonValue {
    private final String url;

    public Thumbnail(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public JSONObject toJSONObject() {
        return new JSONObject().put("url", url);
    }
}