package webhook.model.embed;

import webhook.json.JsonObject;
import webhook.json.JsonValue;
import webhook.model.embed.elements.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Embed implements JsonValue {

    private String title;
    private String description;
    private String url;
    private int color;

    private Author author;
    private Thumbnail thumbnail;
    private Image image;
    private Footer footer;
    private String timestamp;

    private final List<Field> fields = new ArrayList<>();

    public Embed setTitle(String title) {
        this.title = title;
        return this;
    }

    public Embed setDescription(String description) {
        this.description = description;
        return this;
    }

    public Embed setUrl(String url) {
        this.url = url;
        return this;
    }

    public Embed setColor(Color color) {
        String hexColor = String.format("%06X", (0xFFFFFF & color.getRGB()));
        this.color = Integer.parseInt(hexColor, 16);
        return this;
    }

    public Embed setAuthor(String name) {
        this.author = new Author(name, null, null);
        return this;
    }

    public Embed setAuthor(String name, String url) {
        this.author = new Author(name, url, null);
        return this;
    }

    public Embed setAuthor(String name, String url, String icon) {
        this.author = new Author(name, url, icon);
        return this;
    }

    public Embed setThumbnail(String url) {
        this.thumbnail = new Thumbnail(url);
        return this;
    }

    public Embed setImage(String url) {
        this.image = new Image(url);
        return this;
    }

    public Embed setFooter(String text, String icon) {
        this.footer = new Footer(text, icon);
        return this;
    }

    public Embed setTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public Embed addField(String name, String value) {
        this.fields.add(new Field(name, value, null));
        return this;
    }

    public Embed addField(String name, String value, Boolean inline) {
        this.fields.add(new Field(name, value, inline));
        return this;
    }

    @Override
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();

        json.put("title", title);
        json.put("url", url);
        json.put("description", description);
        json.put("color", color);

        if (author != null) {
            json.put("author", author.toJsonObject());
        }

        if (thumbnail != null) {
            json.put("thumbnail", thumbnail.toJsonObject());
        }

        if (image != null) {
            json.put("image", image.toJsonObject());
        }

        if (footer != null) {
            json.put("footer", footer.toJsonObject());
        }

        if (timestamp != null) {
            json.put("timestamp", timestamp);
        }

        json.put("fields", fields.stream()
                .map(Field::toJsonObject)
                .toArray());

        return json;
    }
}
