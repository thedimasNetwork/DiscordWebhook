package webhook;

import webhook.json.JSONObject;
import webhook.json.JsonValue;

import java.util.*;

public class AllowedMentions implements JsonValue {

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
        if (!parseRoles && roleIds.size() < 100) {
            roleIds.add(roleID);
        }
        return this;
    }

    public AllowedMentions addUser(String userId) {
        if (!parseUsers && userIds.size() < 100) {
            userIds.add(userId);
        }
        return this;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();

        if (parseEveryone || parseRoles || parseUsers) {
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
        }

        if (roleIds.size() > 0) {
            List<String> rolesArray = new ArrayList<>(roleIds);
            json.put("roles", rolesArray.toArray());
        }

        if (userIds.size() > 0) {
            List<String> usersArray = new ArrayList<>(userIds);
            json.put("users", usersArray.toArray());
        }

        return json;
    }
}
