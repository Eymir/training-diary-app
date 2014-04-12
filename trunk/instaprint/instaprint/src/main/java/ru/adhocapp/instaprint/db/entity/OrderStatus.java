package ru.adhocapp.instaprint.db.entity;

/**
 * Created by Lenovo on 12.04.2014.
 */
public enum OrderStatus {
    /**
     * Заполнение обязательных полей
     */
    CREATING,
    /**
     * Оплата
     */
    PAYING,
    /**
     * Посылка
     */
    SENDING,
    /**
     * Исполнен
     */
    EXECUTED
}
