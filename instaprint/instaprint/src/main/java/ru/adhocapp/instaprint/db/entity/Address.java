package ru.adhocapp.instaprint.db.entity;

/**
 * Created by Lenovo on 12.04.2014.
 */
public class Address extends Entity {
    private String fullAddress;
    private String index;
    private String fullName;

    public Address(Long id, String fullAddress, String index, String fullName) {
        this.id = id;
        this.fullAddress = fullAddress;
        this.index = index;
        this.fullName = fullName;
    }

    public Address(String fullAddress, String index, String fullName) {
        this.fullAddress = fullAddress;
        this.index = index;
        this.fullName = fullName;
    }

    public Address(Long id) {
        this.id = id;
    }


    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", fullAddress='" + fullAddress + '\'' +
                ", index='" + index + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }
}
