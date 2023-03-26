package ua.pp.stellarnet.discord.webhook;

import ua.pp.stellarnet.discord.webhook.http.Method;
import ua.pp.stellarnet.discord.webhook.http.MultipartBodyPublisher;
import ua.pp.stellarnet.discord.webhook.http.Part;
import ua.pp.stellarnet.discord.webhook.model.Payload;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Webhook {

    private static final HttpClient client = HttpClient.newHttpClient();

    private final String webhookUrl;

    public Webhook(String webhookUrl) {
        if (webhookUrl == null) {
            throw new IllegalArgumentException("Webhook URL cannot be null");
        }
        this.webhookUrl = webhookUrl;
    }

    public CompletableFuture<HttpResponse<String>> sendMessage(Payload payload) {
        return sendMessage(webhookUrl, payload);
    }

    public CompletableFuture<HttpResponse<String>> getMessage(long messageId) {
        return getMessage(webhookUrl, messageId);
    }

    public CompletableFuture<HttpResponse<String>> editMessage(long messageId, Payload payload) {
        return editMessage(webhookUrl, messageId, payload);
    }

    public CompletableFuture<HttpResponse<String>> deleteMessage(long messageId) {
        return deleteMessage(webhookUrl, messageId);
    }

    public CompletableFuture<HttpResponse<String>> sendParts(Part... parts) {
        return sendParts(webhookUrl, parts);
    }

    public CompletableFuture<HttpResponse<String>> sendFiles(File... files) {
        return sendFiles(webhookUrl, files);
    }

    public static CompletableFuture<HttpResponse<String>> sendMessage(String webhookUrl, Payload payload) {
        if (payload.getContent() == null && payload.getEmbeds().isEmpty()) {
            throw new IllegalArgumentException("Payload must contain content or at least one Embed");
        }
        return sendMessage(webhookUrl, payload.toJsonString());
    }

    public static CompletableFuture<HttpResponse<String>> sendMessage(String webhookUrl, String json) {
        return sendRequest(webhookUrl, Method.POST, json);
    }

    public static CompletableFuture<HttpResponse<String>> getMessage(String webhookUrl, long messageId) {
        return sendRequest(webhookUrl + "/messages/" + messageId, Method.GET);
    }

    public static CompletableFuture<HttpResponse<String>> editMessage(String webhookUrl, long messageId, Payload payload) {
        if (payload.getContent() == null && payload.getEmbeds().isEmpty()) {
            throw new IllegalArgumentException("Message must contain content or at least one Embed");
        }
        return editMessage(webhookUrl, messageId, payload.toJsonString());
    }

    public static CompletableFuture<HttpResponse<String>> editMessage(String webhookUrl, long messageId, String json) {
        return sendRequest(webhookUrl + "/messages/" + messageId, Method.PATCH, json);
    }

    public static CompletableFuture<HttpResponse<String>> deleteMessage(String webhookUrl, long messageId) {
        return sendRequest(webhookUrl + "/messages/" + messageId, Method.DELETE);
    }

    public static CompletableFuture<HttpResponse<String>> sendParts(String webhookUrl, Part... parts) {
        MultipartBodyPublisher body = MultipartBodyPublisher.newBuilder()
                .addAllParts(parts)
                .build();

        return sendRequest(webhookUrl, Method.POST, "multipart/form-data; boundary=" + body.getBoundary(), body);
    }

    public static CompletableFuture<HttpResponse<String>> sendFiles(String webhookUrl, File... files) {
        List<Part> fileParts = IntStream.range(0, files.length)
                .mapToObj(i -> Part.ofFile("file" + i, files[i]))
                .collect(Collectors.toCollection(LinkedList::new));

        return sendParts(webhookUrl, fileParts.toArray(Part[]::new));
    }

    private static CompletableFuture<HttpResponse<String>> sendRequest(String url, Method method) {
        return sendRequest(url, method, "application/json", HttpRequest.BodyPublishers.noBody());
    }

    private static CompletableFuture<HttpResponse<String>> sendRequest(String url, Method method, String body) {
        return sendRequest(url, method, "application/json", HttpRequest.BodyPublishers.ofString(body));
    }

    private static CompletableFuture<HttpResponse<String>> sendRequest(String url, Method method, String contentType, HttpRequest.BodyPublisher body) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", contentType)
                .method(method.name(), body)
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }
}
