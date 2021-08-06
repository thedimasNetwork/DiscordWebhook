package webhook;

import webhook.embed.Embed;
import webhook.json.JSONObject;
import webhook.json.JsonValue;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;

@SuppressWarnings({"unused", "UnusedReturnValue", "SameParameterValue"})
public class Webhook implements JsonValue {

    private String webhookUrl;

    private String username;
    private String avatarUrl;
    private String content;
    private Boolean tts;
    private AllowedMentions allowedMentions;
    private List<Embed> embeds;

    public Webhook() {
        this(null);
    }

    public Webhook(String webhookUrl) {
        this.webhookUrl = webhookUrl;
        this.embeds = new LinkedList<>();
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


    public static String sendFile(String webhookUrl, File file) throws IOException {
        String boundary = Long.toHexString(System.currentTimeMillis());
        URLConnection connection = new URL(webhookUrl).openConnection();
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8))) {
            writer.println("--" + boundary);
            writer.println("Content-Disposition: form-data; name=\"" + file.getName() + "\"; filename=\"" + file.getName() + "\"");
            writer.println("Content-Type: text/plain; charset=UTF-8");
            writer.println();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.println(line);
                }
            }
            writer.println("--" + boundary + "--");
        }
        // Connection is lazily executed whenever you request any status.
        return ((HttpURLConnection) connection).getResponseMessage();
    }

    private static HttpResponse<String> sendRequest(String url, String method) throws IOException, InterruptedException {
        return sendRequest(url, method, "");
    }

    private static HttpResponse<String> sendRequest(String url, String method, String body) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .method(method, HttpRequest.BodyPublishers.ofString(body))
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
