package webhook.embed.elements;

import webhook.json.JSONObject;
import webhook.json.JsonValue;

public class Field implements JsonValue {

    private final String name;
    private final String value;
    private final Boolean inline;

    public Field(String name, String value, Boolean inline) {
        this.name = name;
        this.value = value;
        this.inline = inline;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public Boolean isInline() {
        return inline;
    }

    @Override
    public JSONObject toJSONObject() {
        return new JSONObject().put("name", name).put("value", value).put("inline", inline);
    }
}
