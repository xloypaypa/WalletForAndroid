package model.entity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xsu on 16/7/31.
 * it's the entity with name and value
 */
public class NameValueEntity extends EntityWithUsername {

    public NameValueEntity() {
    }

    public NameValueEntity(String username) {
        super(username);
    }

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject jsonObject = super.toJson();
        jsonObject.put("typename", this.objectMap.get("typename"));
        jsonObject.put("value", this.objectMap.get("value"));
        jsonObject.put("clazz", this.getClass().getName());
        return jsonObject;
    }

    @Override
    public void updateValueFromJson(String jsonString) throws JSONException {
        super.updateValueFromJson(jsonString);

        JSONObject jsonObject = new JSONObject(jsonString);
        if (!jsonObject.getString("clazz").equals(this.getClass().getName())) {
            return ;
        }

        this.objectMap.put("typename", jsonObject.getString("typename"));
        this.objectMap.put("value", jsonObject.getDouble("value"));
    }

    public void setTypename(String typename) {
        this.objectMap.put("typename", typename);
    }

    public String getTypename() {
        return (String) this.objectMap.get("typename");
    }

    public void setValue(double value) {
        this.objectMap.put("value", value);
    }

    public double getValue() {
        return (double) this.objectMap.get("value");
    }
}
