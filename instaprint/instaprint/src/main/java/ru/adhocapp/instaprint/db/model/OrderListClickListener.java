package ru.adhocapp.instaprint.db.model;

import ru.adhocapp.instaprint.db.entity.Order;

/**
 * Created by Lenovo on 26.06.2014.
 */
public interface OrderListClickListener {
    void onContextButtonClick(Order order);
}
