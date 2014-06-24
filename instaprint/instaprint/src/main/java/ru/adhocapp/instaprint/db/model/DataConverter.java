package ru.adhocapp.instaprint.db.model;

import java.util.ArrayList;
import java.util.List;

import ru.adhocapp.instaprint.R;
import ru.adhocapp.instaprint.db.entity.Order;
import ru.adhocapp.instaprint.db.entity.OrderStatus;
import ru.adhocapp.instaprint.db.model.data.OrderItem;

/**
 * Created by Lenovo on 24.06.2014.
 */
public class DataConverter {
    public static List<OrderItem> toDataItems(List<Order> ordersWithStatus, boolean withCategories) {
        List<OrderItem> items = new ArrayList<OrderItem>();
        OrderStatus _s = null;
        for (Order o : ordersWithStatus) {
            if (_s != o.getStatus()) {
                OrderItem item = new OrderItem();
                item.setIsCategory(true);
                item.setCategoryNameId(getCategoryNameId(o.getStatus()));
                items.add(item);
                _s = o.getStatus();
            }
            OrderItem item = new OrderItem();
            item.setOrder(o);
            items.add(item);
        }
        return items;
    }

    private static int getCategoryNameId(OrderStatus status) {
        switch (status) {
            case CREATING:
                return R.string.creating_category_name;
            case EMAIL_SENDING:
                return R.string.email_sending_category_name;
            case PAYING:
                return R.string.paying_category_name;
            case PRINTING_AND_SNAILMAILING:
                return R.string.printing_and_snailmailing_category_name;
            case EXECUTED:
                return R.string.executed_category_name;
        }
        return 0;
    }
}
