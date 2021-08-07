package webhook.http;

import java.io.*;
import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.Flow;

public class MultipartBodyPublisher implements HttpRequest.BodyPublisher {

    private final String boundary = UUID.randomUUID().toString();
    private final Map<CharSequence, File> files;
    private final Map<CharSequence, String> forms;
    private HttpRequest.BodyPublisher publisher;

    public MultipartBodyPublisher(Map<CharSequence, File> files, Map<CharSequence, String> forms) {
        this.files = Objects.requireNonNull(files, "files");
        this.forms = Objects.requireNonNull(forms, "forms");
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getBoundary() {
        return boundary;
    }

    private HttpRequest.BodyPublisher getPublisher() {
        if (publisher == null) {
            List<byte[]> arr = new ArrayList<>();
            byte[] separator = ("--" + boundary + "\r\nContent-Disposition: form-data; name=").getBytes(StandardCharsets.UTF_8);

            for (Map.Entry<CharSequence, String> entry : forms.entrySet()) {
                arr.add(separator);

                arr.add(("\"" + entry.getKey() + "\"\r\n\r\n").getBytes(StandardCharsets.UTF_8));
                arr.add(entry.getValue().getBytes(StandardCharsets.UTF_8));
                arr.add("\r\n".getBytes(StandardCharsets.UTF_8));
            }

            arr.add("\r\n".getBytes(StandardCharsets.UTF_8));

            for (Map.Entry<CharSequence, File> entry : files.entrySet()) {
                arr.add(separator);

                try {
                    Path path = entry.getValue().toPath();
                    String mimeType = Files.probeContentType(path);
                    arr.add(("\"" + entry.getKey() + "\"; filename=\"" + path.getFileName() +
                            "\"\r\nContent-Type: " + mimeType + "\r\n\r\n").getBytes(StandardCharsets.UTF_8));

                    arr.add(Files.readAllBytes(path));
                    arr.add("\r\n".getBytes(StandardCharsets.UTF_8));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            arr.add(("--" + boundary + "--").getBytes(StandardCharsets.UTF_8));
            publisher = HttpRequest.BodyPublishers.ofByteArrays(arr);
        }

        return publisher;
    }

    @Override
    public long contentLength() {
        return getPublisher().contentLength();
    }

    @Override
    public void subscribe(Flow.Subscriber<? super ByteBuffer> subscriber) {
        getPublisher().subscribe(subscriber);
    }

    public static class Builder {

        private Map<CharSequence, File> files;
        private Map<CharSequence, String> forms;

        private Map<CharSequence, File> getOrCreateFiles() {
            if (files == null) {
                files = new LinkedHashMap<>();
            }
            return files;
        }

        private Map<CharSequence, String> getOrCreateForms() {
            if (forms == null) {
                forms = new LinkedHashMap<>();
            }
            return forms;
        }

        public Builder addFile(CharSequence name, File file) {
            getOrCreateFiles().put(name, file);
            return this;
        }

        public Builder addAllFiles(Map<? extends CharSequence, ? extends File> files) {
            getOrCreateFiles().putAll(files);
            return this;
        }

        public Builder addForm(CharSequence name, String content) {
            getOrCreateForms().put(name, content);
            return this;
        }

        public Builder addAllForms(Map<? extends CharSequence, String> forms) {
            getOrCreateForms().putAll(forms);
            return this;
        }

        public MultipartBodyPublisher build() {
            return new MultipartBodyPublisher(files == null ? Map.of() : files, forms == null ? Map.of() : forms);
        }
    }
}