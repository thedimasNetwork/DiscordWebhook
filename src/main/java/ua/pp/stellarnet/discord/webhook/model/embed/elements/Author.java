package ua.pp.stellarnet.discord.webhook.model.embed.elements;

import ua.pp.stellarnet.discord.webhook.json.JsonObject;
import ua.pp.stellarnet.discord.webhook.json.JsonValue;

public class Author implements JsonValue {

    private final String name;
    private final String url;
    private final String iconUrl;

    public Author(String name, String url, String iconUrl) {
        this.name = name;
        this.url = url;
        this.iconUrl = iconUrl;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    @Override
    public JsonObject toJsonObject() {
        return new JsonObject().put("name", name).put("url", url).put("icon_url", iconUrl);
    }
}
