package model.entity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xsu on 16/7/19.
 * it's the entity with username
 */
public abstract class EntityWithUsername extends Entity {

    EntityWithUsername() {
    }

    EntityWithUsername(String username) {
        super();
        this.setUsername(username);
    }

    public JSONObject toJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", this.objectMap.get("username"));
        jsonObject.put("clazz", this.getClass().getName());
        return jsonObject;
    }

    public void updateValueFromJson(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        if (!jsonObject.getString("clazz").equals(this.getClass().getName())) {
            return;
        }

        this.objectMap.put("username", jsonObject.getString("username"));
    }

    public void setUsername(String username) {
        this.objectMap.put("username", username);
    }

    public String getUsername() {
        return (String) this.objectMap.get("username");
    }
}
