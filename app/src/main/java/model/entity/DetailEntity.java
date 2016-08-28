package model.entity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xsu on 16/7/19.
 * it's the detail entity
 */
public class DetailEntity extends EntityWithUsername {

    public DetailEntity() {
    }

    public DetailEntity(String username, long date, String event, String eventParam, String rollBackParam) {
        super(username);
        this.setDate(date);
        this.setEvent(event);
        this.setEventParam(eventParam);
        this.setRollBackParam(rollBackParam);
    }

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject jsonObject = super.toJson();
        jsonObject.put("date", this.getDate());
        jsonObject.put("event", this.getEvent());
        jsonObject.put("eventParam", this.getEventParam());
        jsonObject.put("rollBackParam", this.getRollBackParam());
        return jsonObject;
    }

    @Override
    public void updateValueFromJson(String jsonString) throws JSONException {
        super.updateValueFromJson(jsonString);

        JSONObject jsonObject = new JSONObject(jsonString);
        this.setDate(jsonObject.getLong("date"));
        this.setEvent(jsonObject.getString("event"));
        this.setEventParam(jsonObject.getString("eventParam"));
        this.setRollBackParam(jsonObject.getString("rollBackParam"));
    }

    public void setDate(long date) {
        this.objectMap.put("date", date);
    }

    public long getDate() {
        return (long) this.objectMap.get("date");
    }

    public void setEvent(String event) {
        this.objectMap.put("event", event);
    }

    public String getEvent() {
        return (String) this.objectMap.get("event");
    }

    public void setEventParam(String eventParam) {
        this.objectMap.put("eventParam", eventParam);
    }

    public String getEventParam() {
        return (String) this.objectMap.get("eventParam");
    }

    public void setRollBackParam(String rollBackParam) {
        this.objectMap.put("rollBackParam", rollBackParam);
    }

    public String getRollBackParam() {
        return (String) this.objectMap.get("rollBackParam");
    }
}
