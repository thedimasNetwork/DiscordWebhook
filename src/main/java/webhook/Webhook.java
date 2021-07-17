package webhook;

import webhook.embed.Embed;
import webhook.json.JSONObject;
import webhook.json.JsonValue;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class Webhook implements JsonValue {

    private String url;

    private String username;
    private String avatarUrl;
    private String content;
    private Boolean tts;
    private AllowedMentions allowedMentions;

    private final List<Embed> embeds = new ArrayList<>();

    public Webhook() {
        this.url = null;
    }

    public Webhook(String url) {
        this.url = url;
    }

    public Webhook setUrl(String url) {
        this.url = url;
        return this;
    }

    public Webhook setUsername(String username) {
        this.username = username;
        return this;
    }

    public Webhook setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        return this;
    }

    public Webhook setContent(String content) {
        this.content = content;
        return this;
    }

    public Webhook setTts(boolean tts) {
        this.tts = tts;
        return this;
    }

    public Webhook setAllowedMentions(AllowedMentions allowedMentions) {
        this.allowedMentions = allowedMentions;
        return this;
    }

    public Webhook addEmbed(Embed embed) {
        this.embeds.add(embed);
        return this;
    }

    public HttpResponse<String> execute() throws IOException, InterruptedException {
        Objects.requireNonNull(url, "Set Webhook URL");
        if (content == null && embeds.isEmpty()) {
            throw new IllegalArgumentException("Set content or add at least one Embed");
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(toString()))
                .build();

        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();

        json.put("username", this.username);
        json.put("avatar_url", this.avatarUrl);
        json.put("content", this.content);
        json.put("tts", this.tts);

        if (allowedMentions != null) {
            json.put("allowed_mentions", this.allowedMentions.toJSONObject());
        }

        if (!embeds.isEmpty()) {
            List<JSONObject> embedsArray = new ArrayList<>();
            for (Embed embed : embeds) {
                embedsArray.add(embed.toJSONObject());
            }
            json.put("embeds", embedsArray.toArray());
        }
        return json;
    }

    @Override
    public String toString() {
        return toJSONObject().toString();
    }
}
