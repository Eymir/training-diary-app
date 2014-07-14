package ru.adhocapp.instaprint.db.entity;

import java.util.Date;

import ru.adhocapp.instaprint.R;
import ru.adhocapp.instaprint.util.ResourceAccess;

/**
 * Created by Lenovo on 12.04.2014.
 */
public class Order extends Entity {
    private Long id;
    private Address addressFrom;
    private Address addressTo;
    private String text;
    private String rawFrontSidePath;
    private String frontSidePhotoPath;
    private String backSidePhotoPath;
    private Date date;
    private PurchaseDetails purchaseDetails;
    private OrderStatus status;
    private String frame;
    private String font;
    private Integer frontSidePhotoRotation = 0;

    public Order(Long id, Address addressFrom, Address addressTo, String text, String frontSidePhotoPath, Date date, PurchaseDetails purchaseDetails, OrderStatus status) {
        this.id = id;
        this.addressFrom = addressFrom;
        this.addressTo = addressTo;
        this.text = text;
        this.frontSidePhotoPath = frontSidePhotoPath;
        this.date = date;
        this.purchaseDetails = purchaseDetails;
        this.status = status;
    }

    public Order(Address addressFrom, Address addressTo, String text, String frontSidePhotoPath, Date date, PurchaseDetails purchaseDetails, OrderStatus status) {
        this.addressFrom = addressFrom;
        this.addressTo = addressTo;
        this.text = text;
        this.frontSidePhotoPath = frontSidePhotoPath;
        this.date = date;
        this.purchaseDetails = purchaseDetails;
        this.status = status;
    }


    public Order(Long id, Address addressFrom, Address addressTo, String text, String rawFrontSidePath, String frontSidePhotoPath, String backSidePhotoPath, Date date, PurchaseDetails purchaseDetails, OrderStatus status) {
        this.id = id;
        this.addressFrom = addressFrom;
        this.addressTo = addressTo;
        this.text = text;
        this.rawFrontSidePath = rawFrontSidePath;
        this.frontSidePhotoPath = frontSidePhotoPath;
        this.backSidePhotoPath = backSidePhotoPath;
        this.date = date;
        this.purchaseDetails = purchaseDetails;
        this.status = status;
    }

    public Order(Long id, Address addressFrom, Address addressTo, String text, String rawFrontSidePath, String frontSidePhotoPath, String backSidePhotoPath, Date date, PurchaseDetails purchaseDetails, OrderStatus status, String frame, String font) {
        this.id = id;
        this.addressFrom = addressFrom;
        this.addressTo = addressTo;
        this.text = text;
        this.rawFrontSidePath = rawFrontSidePath;
        this.frontSidePhotoPath = frontSidePhotoPath;
        this.backSidePhotoPath = backSidePhotoPath;
        this.date = date;
        this.purchaseDetails = purchaseDetails;
        this.status = status;
        this.frame = frame;
        this.font = font;
    }

    public String getRawFrontSidePath() {
        return rawFrontSidePath;
    }

    public void setRawFrontSidePath(String rawFrontSidePath) {
        this.rawFrontSidePath = rawFrontSidePath;
    }

    public String getBackSidePhotoPath() {
        return backSidePhotoPath;
    }

    public void setBackSidePhotoPath(String backSidePhotoPath) {
        this.backSidePhotoPath = backSidePhotoPath;
    }

    public Order(OrderStatus status) {
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

    public String getFrontSidePhotoPath() {
        return frontSidePhotoPath;
    }

    public void setFrontSidePhotoPath(String frontSidePhotoPath) {
        this.frontSidePhotoPath = frontSidePhotoPath;
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


    public String toMailTitle() {
        String title = "SENDER: ";
        if (addressFrom != null && addressFrom.getFullName() != null) {
            title += addressFrom.getFullName();
        } else {
            title += ResourceAccess.getInstance(null).getResources().getString(R.string.title_without_sender);
        }
        if (purchaseDetails != null) {
            title += " PAYMENT: " + purchaseDetails.getOrderNumber();
        } else {
            title += " PAYMENT: " + ResourceAccess.getInstance(null).getResources().getString(R.string.title_wasnt_payed);
        }
        return title;
    }

    public String toMailBody() {
        return toString();
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", addressFrom=" + addressFrom +
                ", addressTo=" + addressTo +
                ", text='" + text + '\'' +
                ", rawFrontSidePath='" + rawFrontSidePath + '\'' +
                ", frontSidePhotoPath='" + frontSidePhotoPath + '\'' +
                ", backSidePhotoPath='" + backSidePhotoPath + '\'' +
                ", date=" + date +
                ", purchaseDetails=" + purchaseDetails +
                ", status=" + status +
                ", frame='" + frame + '\'' +
                ", font='" + font + '\'' +
                '}';
    }

    public String getFrame() {
        return frame;
    }

    public void setFrame(String frame) {
        this.frame = frame;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public Integer getFrontSidePhotoRotation() {
        return frontSidePhotoRotation;
    }

    public void setFrontSidePhotoRotation(Integer frontSidePhotoRotation) {
        this.frontSidePhotoRotation = frontSidePhotoRotation;
    }
}
