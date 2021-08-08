package webhook;

import webhook.embed.Embed;
import webhook.http.*;
import webhook.json.JSONObject;
import webhook.json.JsonValue;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.*;

@SuppressWarnings({"unused"})
public class Webhook implements JsonValue {

    private static final HttpClient client = HttpClient.newHttpClient();

    private String webhookUrl;

    private String username;
    private String avatarUrl;
    private String content;
    private Boolean tts;
    private AllowedMentions allowedMentions;
    private List<Embed> embeds = new LinkedList<>();

    public Webhook() {
        this(null);
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

    public Webhook setEmbeds(List<Embed> embeds) {
        this.embeds = embeds;
        return this;
    }

    public Webhook addEmbed(Embed embed) {
        this.embeds.add(embed);
        return this;
    }

    public CompletableFuture<HttpResponse<String>> execute() {
        return sendRequest("POST");
    }

    public CompletableFuture<HttpResponse<String>> editMessage(long messageId) {
        webhookUrl += "/messages/" + messageId;
        return sendRequest("PATCH");
    }

    private CompletableFuture<HttpResponse<String>> sendRequest(String method) {
        if (webhookUrl == null) {
            throw new IllegalStateException("Set Webhook URL");
        }
        if (content == null && embeds.isEmpty()) {
            throw new IllegalStateException("Set content or add at least one Embed");
        }
        return sendRequest(webhookUrl, method, toString());
    }

    public static CompletableFuture<HttpResponse<String>> execute(String webhookUrl, String json) {
        return sendRequest(webhookUrl, "POST", json);
    }

    public static CompletableFuture<HttpResponse<String>> editMessage(String webhookUrl, long messageId, String json) {
        return sendRequest(webhookUrl + "/messages/" + messageId, "PATCH", json);
    }

    public static CompletableFuture<HttpResponse<String>> getMessage(String webhookUrl, long messageId) {
        return sendRequest(webhookUrl + "/messages/" + messageId, "GET");
    }

    public static CompletableFuture<HttpResponse<String>> deleteMessage(String webhookUrl, long messageId) {
        return sendRequest(webhookUrl + "/messages/" + messageId, "DELETE");
    }

    public static CompletableFuture<HttpResponse<String>> sendMultipart(String webhookUrl, Part... parts) {
        MultipartBodyPublisher body = MultipartBodyPublisher.newBuilder()
                .addAllParts(parts)
                .build();

        return sendRequest(webhookUrl, "POST", "multipart/form-data; boundary=" + body.getBoundary(), body);
    }

    public static CompletableFuture<HttpResponse<String>> sendFiles(String webhookUrl, File... files) throws IOException, InterruptedException {
        List<Part> fileParts = IntStream.range(0, files.length)
                .mapToObj(i -> Part.ofFile("file" + i, files[i]))
                .collect(Collectors.toCollection(LinkedList::new));

        MultipartBodyPublisher body = MultipartBodyPublisher.newBuilder()
                .addAllParts(fileParts)
                .build();

        return sendRequest(webhookUrl, "POST", "multipart/form-data; boundary=" + body.getBoundary(), body);
    }

    private static CompletableFuture<HttpResponse<String>> sendRequest(String url, String method) {
        return sendRequest(url, method, "application/json", HttpRequest.BodyPublishers.noBody());
    }

    private static CompletableFuture<HttpResponse<String>> sendRequest(String url, String method, String body) {
        return sendRequest(url, method, "application/json", HttpRequest.BodyPublishers.ofString(body));
    }

    private static CompletableFuture<HttpResponse<String>> sendRequest(String url, String method, String contentType, HttpRequest.BodyPublisher body) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", contentType)
                .method(method, body)
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
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
            json.put("embeds", embeds.stream()
                    .map(Embed::toJSONObject)
                    .toArray());
        }
        return json;
    }

    @Override
    public String toString() {
        return toJSONObject().toString();
    }
}
