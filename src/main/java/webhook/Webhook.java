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

    public HttpResponse<String> execute() throws IOException, InterruptedException {
        return sendRequest("POST");
    }

    public HttpResponse<String> editMessage(long messageId) throws IOException, InterruptedException {
        webhookUrl += "/messages/" + messageId;
        return sendRequest("PATCH");
    }

    private HttpResponse<String> sendRequest(String method) throws IOException, InterruptedException {
        if (webhookUrl == null) {
            throw new IllegalStateException("Set Webhook URL");
        }
        if (content == null && embeds.isEmpty()) {
            throw new IllegalStateException("Set content or add at least one Embed");
        }
        return sendRequest(webhookUrl, method, toString());
    }

    public static HttpResponse<String> execute(String webhookUrl, String json) throws IOException, InterruptedException {
        return sendRequest(webhookUrl, "POST", json);
    }

    public static HttpResponse<String> editMessage(String webhookUrl, long messageId, String json) throws IOException, InterruptedException {
        return sendRequest(webhookUrl + "/messages/" + messageId, "PATCH", json);
    }

    public static HttpResponse<String> getMessage(String webhookUrl, long messageId) throws IOException, InterruptedException {
        return sendRequest(webhookUrl + "/messages/" + messageId, "GET");
    }

    public static HttpResponse<String> deleteMessage(String webhookUrl, long messageId) throws IOException, InterruptedException {
        return sendRequest(webhookUrl + "/messages/" + messageId, "DELETE");
    }

    public static HttpResponse<String> sendFile(String webhookUrl, File file) throws IOException, InterruptedException {
        MultipartBodyPublisher body = MultipartBodyPublisher.newBuilder()
                .addPart(Part.ofFile("file", file))
                .build();

        return sendRequest(webhookUrl, "POST", "multipart/form-data; boundary=" + body.getBoundary(), body);
    }

    public static HttpResponse<String> sendFiles(String webhookUrl, File... files) throws IOException, InterruptedException {
        List<Part> fileParts = IntStream.range(0, files.length)
                .mapToObj(i -> Part.ofFile("file" + i, files[i]))
                .collect(Collectors.toCollection(LinkedList::new));

        MultipartBodyPublisher body = MultipartBodyPublisher.newBuilder()
                .addAllParts(fileParts)
                .build();

        return sendRequest(webhookUrl, "POST", "multipart/form-data; boundary=" + body.getBoundary(), body);
    }

    private static HttpResponse<String> sendRequest(String url, String method) throws IOException, InterruptedException {
        return sendRequest(url, method, "application/json", HttpRequest.BodyPublishers.noBody());
    }

    private static HttpResponse<String> sendRequest(String url, String method, String body) throws IOException, InterruptedException {
        return sendRequest(url, method, "application/json", HttpRequest.BodyPublishers.ofString(body));
    }

    private static HttpResponse<String> sendRequest(String url, String method, String contentType, HttpRequest.BodyPublisher body)
            throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", contentType)
                .method(method, body)
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
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
