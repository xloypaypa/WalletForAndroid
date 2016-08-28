package model.entity;

/**
 * Created by xsu on 16/7/19.
 * it's the money entity
 */
public class MoneyEntity extends NameValueEntity {

    public MoneyEntity() {
    }

    public MoneyEntity(String username, String typename, double value) {
        super(username);
        this.setTypename(typename);
        this.setValue(value);
    }

}
