package webhook.model;

import webhook.json.JsonObject;
import webhook.json.JsonValue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AllowedMentions implements JsonValue {

    public static final int MAX_IDS_SIZE = 100;

    private boolean parseEveryone = false;
    private boolean parseRoles = false;
    private boolean parseUsers = false;

    private final Set<String> userIds = new HashSet<>();
    private final Set<String> roleIds = new HashSet<>();

    public AllowedMentions parseEveryone() {
        parseEveryone = true;
        return this;
    }

    public AllowedMentions parseRoles() {
        userIds.clear();
        parseRoles = true;
        return this;
    }

    public AllowedMentions parseUsers() {
        roleIds.clear();
        parseUsers = true;
        return this;
    }

    public AllowedMentions addRole(String roleID) {
        if (!parseRoles && roleIds.size() < MAX_IDS_SIZE) {
            roleIds.add(roleID);
        }
        return this;
    }

    public AllowedMentions addUser(String userId) {
        if (!parseUsers && userIds.size() < MAX_IDS_SIZE) {
            userIds.add(userId);
        }
        return this;
    }

    @Override
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();

        List<String> parseArray = new ArrayList<>();
        if (parseEveryone) {
            parseArray.add("everyone");
        }
        if (parseRoles) {
            parseArray.add("roles");
        }
        if (parseUsers) {
            parseArray.add("users");
        }
        json.put("parse", parseArray.toArray());

        if (roleIds.size() > 0) {
            json.put("roles", roleIds.toArray());
        }

        if (userIds.size() > 0) {
            json.put("users", userIds.toArray());
        }

        return json;
    }
}
