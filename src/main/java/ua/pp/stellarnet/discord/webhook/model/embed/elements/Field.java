package ua.pp.stellarnet.discord.webhook.model.embed.elements;

import ua.pp.stellarnet.discord.webhook.json.JsonObject;
import ua.pp.stellarnet.discord.webhook.json.JsonValue;

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
    public JsonObject toJsonObject() {
        return new JsonObject().put("name", name).put("value", value).put("inline", inline);
    }
}
