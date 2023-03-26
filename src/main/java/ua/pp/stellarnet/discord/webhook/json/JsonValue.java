package ua.pp.stellarnet.discord.webhook.json;

public interface JsonValue {

    JsonObject toJsonObject();

    default String toJsonString() {
        return this.toJsonObject().toString();
    }
}
