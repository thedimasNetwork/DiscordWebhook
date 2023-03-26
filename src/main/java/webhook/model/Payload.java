package webhook.model;

import webhook.json.JsonObject;
import webhook.json.JsonValue;
import webhook.model.embed.Embed;

import java.util.List;

public class Payload implements JsonValue {

    private String username;
    private String avatarUrl;
    private String content;
    private Boolean tts;
    private AllowedMentions allowedMentions;
    private List<Embed> embeds;

    public String getUsername() {
        return username;
    }

    public Payload setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public Payload setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        return this;
    }

    public String getContent() {
        return content;
    }

    public Payload setContent(String content) {
        this.content = content;
        return this;
    }

    public Boolean getTts() {
        return tts;
    }

    public Payload setTts(boolean tts) {
        this.tts = tts;
        return this;
    }

    public AllowedMentions getAllowedMentions() {
        return allowedMentions;
    }

    public Payload setAllowedMentions(AllowedMentions allowedMentions) {
        this.allowedMentions = allowedMentions;
        return this;
    }

    public List<Embed> getEmbeds() {
        return embeds;
    }

    public Payload setEmbeds(List<Embed> embeds) {
        this.embeds = embeds;
        return this;
    }

    @Override
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();

        json.put("username", this.username);
        json.put("avatar_url", this.avatarUrl);
        json.put("content", this.content);
        json.put("tts", this.tts);

        if (allowedMentions != null) {
            json.put("allowed_mentions", this.allowedMentions.toJsonObject());
        }

        if (embeds != null && !embeds.isEmpty()) {
            json.put("embeds", embeds.stream()
                    .map(Embed::toJsonObject)
                    .toArray());
        }
        return json;
    }
}
