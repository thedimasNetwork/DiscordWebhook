package ua.pp.stellarnet.discord.webhook.json;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JsonObject {

    private final Map<String, Object> map = new HashMap<>();

    public JsonObject put(String key, Object value) {
        if (value != null) {
            map.put(key, value);
        }
        return this;
    }

    @Override
    public String toString() {
        int i = 0;
        StringBuilder builder = new StringBuilder();
        Set<Map.Entry<String, Object>> entrySet = map.entrySet();

        builder.append("{");
        for (Map.Entry<String, Object> entry : entrySet) {
            Object val = entry.getValue();
            builder.append(quote(entry.getKey())).append(":");

            if (val instanceof String) {
                builder.append(quote(String.valueOf(val)));
            } else if (val instanceof Integer) {
                builder.append(Integer.valueOf(String.valueOf(val)));
            } else if (val instanceof Boolean) {
                builder.append(Boolean.valueOf(String.valueOf(val)));
            } else if (val instanceof JsonObject) {
                builder.append(val);
            } else if (val.getClass().isArray()) {
                builder.append("[");
                int len = Array.getLength(val);
                for (int j = 0; j < len; j++) {
                    Object element = Array.get(val, j);
                    builder.append(element instanceof String ? quote(element.toString()) : element.toString());
                    if (j < len - 1) {
                        builder.append(",");
                    }
                }
                builder.append("]");
            }
            builder.append(++i != entrySet.size() ? "," : "");
        }
        builder.append("}");

        return builder.toString().replaceAll("\\n", "\\\\n");
    }

    private String quote(String string) {
        return "\"" + string + "\"";
    }
}
