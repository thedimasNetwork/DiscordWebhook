package ua.pp.stellarnet.discord.webhook.model.embed.elements;

import ua.pp.stellarnet.discord.webhook.json.JsonObject;
import ua.pp.stellarnet.discord.webhook.json.JsonValue;

public class Footer implements JsonValue {

    private final String text;
    private final String iconUrl;

    public Footer(String text, String iconUrl) {
        this.text = text;
        this.iconUrl = iconUrl;
    }

    public String getText() {
        return text;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    @Override
    public JsonObject toJsonObject() {
        return new JsonObject().put("text", text).put("icon_url", iconUrl);
    }
}
