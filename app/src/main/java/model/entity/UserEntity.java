package model.entity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xsu on 16/7/19.
 * it's the user entity
 */
public class UserEntity extends EntityWithUsername {

    public UserEntity() {
        super();
        this.objectMap.put("license", new ArrayList<>());
    }

    public UserEntity(String username) {
        super(username);
        this.objectMap.put("license", new ArrayList<>());
    }

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject jsonObject = super.toJson();
        jsonObject.put("license", this.objectMap.get("license"));
        return jsonObject;
    }

    @Override
    public void updateValueFromJson(String jsonString) throws JSONException {
        super.updateValueFromJson(jsonString);

        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray jsonArray = jsonObject.getJSONArray("license");
        this.clearLicense();
        for (int i = 0; i < jsonArray.length(); i++) {
            Object now = jsonArray.get(i);
            this.giveLicense(now.toString());
        }
    }

    public boolean haveLicense(String license) {
        return ((ArrayList) this.objectMap.get("license")).contains(license);
    }

    public void giveLicense(String license) {
        addObjectToArray("license", license);
    }

    public void removeLicense(String license) {
        removeObjectFromArray("license", license);
    }

    public void clearLicense() {
        clearArray("license");
    }

    private List<String> getArray(String key) {
        ArrayList list = ((ArrayList) this.objectMap.get(key));
        ArrayList<String> result = new ArrayList<>();
        for (Object now : list) {
            result.add(now.toString());
        }
        return result;
    }

    private void addObjectToArray(String key, String license) {
        //noinspection unchecked
        ((ArrayList) this.objectMap.get(key)).add(license);
    }

    private void removeObjectFromArray(String key, String value) {
        ((ArrayList) this.objectMap.get(key)).remove(value);
    }

    private void clearArray(String key) {
        ((ArrayList) this.objectMap.get(key)).clear();
    }
}
