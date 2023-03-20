package webhook.embed.elements;

import webhook.json.JsonObject;
import webhook.json.JsonValue;

public class Image implements JsonValue {

    private final String url;

    public Image(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public JsonObject toJsonObject() {
        return new JsonObject().put("url", url);
    }
}