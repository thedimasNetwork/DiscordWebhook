package webhook.http;

import java.util.Objects;

public class FilePart extends Part {

    private final CharSequence filename;

    public FilePart(CharSequence name, CharSequence contentType, byte[] content, CharSequence filename) {
        super(name, contentType, content);
        this.filename = Objects.requireNonNull(filename, "filename");
    }

    public CharSequence getFilename() {
        return filename;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FilePart)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        FilePart filePart = (FilePart) o;
        return filename.equals(filePart.filename);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), filename);
    }
}
