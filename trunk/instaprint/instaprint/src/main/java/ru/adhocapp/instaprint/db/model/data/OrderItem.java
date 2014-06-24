package ru.adhocapp.instaprint.db.model.data;

import ru.adhocapp.instaprint.db.entity.Order;

/**
 * Created by Lenovo on 24.06.2014.
 */
public class OrderItem {
    private boolean isCategory;
    private int categoryNameId;
    private Order order;

    public Boolean getIsCategory() {
        return isCategory;
    }

    public void setIsCategory(Boolean isCategory) {
        this.isCategory = isCategory;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public int getCategoryNameId() {
        return categoryNameId;
    }

    public void setCategoryNameId(int categoryNameId) {
        this.categoryNameId = categoryNameId;
    }
}
