package ru.adhocapp.instaprint.db.entity;

import java.util.Date;

/**
 * Created by Lenovo on 12.04.2014.
 */
public class PurchaseDetails extends Entity {
    private Long id;
    private String orderNumber;
    private Date payDate;
    private Double price;

    public PurchaseDetails(String orderNumber, Date payDate, Double price) {
        this.orderNumber = orderNumber;
        this.payDate = payDate;
        this.price = price;
    }

    public PurchaseDetails(Long id, String orderNumber, Date payDate, Double price) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.payDate = payDate;
        this.price = price;
    }

    public PurchaseDetails(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "PurchaseDetails{" +
                "id=" + id +
                ", orderNumber='" + orderNumber + '\'' +
                ", payDate=" + payDate +
                ", price=" + price +
                '}';
    }
}
