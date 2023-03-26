package webhook;

import webhook.http.MultipartBodyPublisher;
import webhook.http.Part;
import webhook.model.Payload;

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

    private String webhookUrl;

    public Webhook() {
    }

    public Webhook(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public void setUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public CompletableFuture<HttpResponse<String>> sendMessage(Payload payload) {
        return sendMessage(webhookUrl, payload);
    }

    public CompletableFuture<HttpResponse<String>> editMessage(long messageId, Payload payload) {
        return editMessage(webhookUrl, messageId, payload);
    }

    public static CompletableFuture<HttpResponse<String>> sendMessage(String webhookUrl, Payload payload) {
        if (payload.getContent() == null && payload.getEmbeds().isEmpty()) {
            throw new IllegalArgumentException("Payload must contain content or at least one Embed");
        }
        return sendMessage(webhookUrl, payload.toJsonString());
    }

    public static CompletableFuture<HttpResponse<String>> sendMessage(String webhookUrl, String json) {
        return sendRequest(webhookUrl, "POST", json);
    }

    public static CompletableFuture<HttpResponse<String>> editMessage(String webhookUrl, long messageId, Payload payload) {
        if (payload.getContent() == null && payload.getEmbeds().isEmpty()) {
            throw new IllegalArgumentException("Payload must contain content or at least one Embed");
        }
        return editMessage(webhookUrl, messageId, payload.toJsonString());
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

    public static CompletableFuture<HttpResponse<String>> sendFiles(String webhookUrl, File... files) {
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
}
