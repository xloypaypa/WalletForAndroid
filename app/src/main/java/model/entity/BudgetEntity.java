package model.entity;

/**
 * Created by xsu on 16/7/31.
 * it's the budget entity
 */
public class BudgetEntity extends NameValueEntity {

    public BudgetEntity() {
    }

    public BudgetEntity(String username, String typename, double value) {
        super(username);
        this.setTypename(typename);
        this.setValue(value);
    }
}
