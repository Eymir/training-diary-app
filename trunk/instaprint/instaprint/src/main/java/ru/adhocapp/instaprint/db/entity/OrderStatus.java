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
     * Посылка на сервер печати
     */
    EMAIL_SENDING,
    /**
     * Печать и отправка по настоящей почте
     */
    PRINTING_AND_SNAILMAILING,
    /**
     * Исполнен
     */
    EXECUTED
}
