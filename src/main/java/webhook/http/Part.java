package webhook.http;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class Part {
    private final CharSequence name;
    // @Nullable
    private final CharSequence contentType;
    private final byte[] content;

    public Part(CharSequence name, CharSequence contentType, byte[] content) {
        this.name = Objects.requireNonNull(name, "name");
        this.contentType = contentType;
        this.content = Objects.requireNonNull(content, "content");
    }

    public static FilePart ofFile(CharSequence name, File file){
        try {
            return new FilePart(name, Files.probeContentType(file.toPath()),
                    Files.readAllBytes(file.toPath()), file.getName());
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public static Part ofBytes(CharSequence name, CharSequence contentType, byte[] content) {
        return new Part(name, contentType, content);
    }

    public static Part ofString(CharSequence name, CharSequence contentType, String content) {
        return new Part(name, contentType, content.getBytes(StandardCharsets.UTF_8));
    }

    public CharSequence getName() {
        return name;
    }

    public CharSequence getContentType() {
        return contentType;
    }

    public byte[] getContent() {
        return content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (!(o instanceof Part)) {
            return false;
        }
        Part part = (Part)o;
        return name.equals(part.name) &&
                Objects.equals(contentType, part.contentType) &&
                Arrays.equals(content, part.content);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name, contentType);
        result = 31 * result + Arrays.hashCode(content);
        return result;
    }
}
