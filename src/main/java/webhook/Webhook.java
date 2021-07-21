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

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class Webhook implements JsonValue {

    private String webhookUrl;
    private String method = "POST";

    private String username;
    private String avatarUrl;
    private String content;
    private Boolean tts;
    private AllowedMentions allowedMentions;
    private final List<Embed> embeds = new ArrayList<>();

    public Webhook() {
        this.webhookUrl = null;
    }

    public Webhook(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public Webhook setUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
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

    public Webhook editMessage(long id) {
        webhookUrl += "/messages/" + id;
        method = "PATCH";
        return this;
    }

    public HttpResponse<String> execute() throws IOException, InterruptedException {
        if (content == null && embeds.isEmpty()) {
            throw new IllegalArgumentException("Set content or add at least one Embed");
        }
        return execute(toString());
    }

    public HttpResponse<String> execute(String json) throws IOException, InterruptedException {
        Objects.requireNonNull(webhookUrl, "Set Webhook URL");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(webhookUrl))
                .header("Content-Type", "application/json")
                .method(method, HttpRequest.BodyPublishers.ofString(json))
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
