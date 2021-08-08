package webhook.http;

import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Flow;

public class MultipartBodyPublisher implements HttpRequest.BodyPublisher {

    private final String boundary = UUID.randomUUID().toString();
    private final List<Part> parts;
    private HttpRequest.BodyPublisher publisher;

    public MultipartBodyPublisher(List<Part> parts) {
        this.parts = Objects.requireNonNull(parts, "parts");
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

            for (Part part : parts) {
                arr.add(separator);

                StringBuilder header = new StringBuilder();
                header.append("\"").append(part.getName()).append("\"");
                if (part instanceof FilePart) {
                   FilePart filePart = (FilePart)part;
                   header.append("; filename=\"").append(filePart.getFilename()).append("\"");
                }

                if (part.getContentType() != null) {
                    header.append("\r\nContent-Type: ").append(part.getContentType());
                }

                header.append("\r\n\r\n");

                arr.add(header.toString().getBytes(StandardCharsets.UTF_8));
                arr.add(part.getContent());
                arr.add("\r\n".getBytes(StandardCharsets.UTF_8));
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

        private List<Part> parts;

        private List<Part> getOrCreateParts() {
            if (parts == null) {
                parts = new LinkedList<>();
            }
            return parts;
        }

        public Builder addPart(Part part) {
            getOrCreateParts().add(part);
            return this;
        }

        public Builder addAllParts(Collection<? extends Part> parts) {
            getOrCreateParts().addAll(parts);
            return this;
        }

        public MultipartBodyPublisher build() {
            return new MultipartBodyPublisher(parts == null ? List.of() : parts);
        }
    }
}
