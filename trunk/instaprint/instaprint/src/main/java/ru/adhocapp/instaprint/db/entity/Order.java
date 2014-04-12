package ru.adhocapp.instaprint.db.entity;

import java.util.Date;

/**
 * Created by Lenovo on 12.04.2014.
 */
public class Order extends Entity {
    private Long id;
    private Address addressFrom;
    private Address addressTo;
    private String text;
    private String photoPath;
    private Date date;
    private PurchaseDetails purchaseDetails;
    private OrderStatus status;

    public Order(Long id, Address addressFrom, Address addressTo, String text, String photoPath, Date date, PurchaseDetails purchaseDetails, OrderStatus status) {
        this.id = id;
        this.addressFrom = addressFrom;
        this.addressTo = addressTo;
        this.text = text;
        this.photoPath = photoPath;
        this.date = date;
        this.purchaseDetails = purchaseDetails;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Address getAddressFrom() {
        return addressFrom;
    }

    public void setAddressFrom(Address addressFrom) {
        this.addressFrom = addressFrom;
    }

    public Address getAddressTo() {
        return addressTo;
    }

    public void setAddressTo(Address addressTo) {
        this.addressTo = addressTo;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public PurchaseDetails getPurchaseDetails() {
        return purchaseDetails;
    }

    public void setPurchaseDetails(PurchaseDetails purchaseDetails) {
        this.purchaseDetails = purchaseDetails;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
