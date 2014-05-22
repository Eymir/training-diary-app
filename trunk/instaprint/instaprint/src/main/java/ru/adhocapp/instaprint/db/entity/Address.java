package ru.adhocapp.instaprint.db.entity;

/**
 * Created by Lenovo on 12.04.2014.
 */
public class Address extends Entity {
    private String streetAddress;
    private String cityName;
    private String countryName;
    private String zipCode;
    private String fullName;

    public Address(String streetAddress, String cityName, String countryName, String zipCode, String fullName) {
        this.streetAddress = streetAddress;
        this.cityName = cityName;
        this.countryName = countryName;
        this.zipCode = zipCode;
        this.fullName = fullName;
    }

    public Address(Long id, String streetAddress, String cityName, String countryName, String zipCode, String fullName) {
        this.id = id;
        this.streetAddress = streetAddress;
        this.cityName = cityName;
        this.countryName = countryName;
        this.zipCode = zipCode;
        this.fullName = fullName;
    }

    public Address(Long id) {
        this.id = id;
    }


    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    @Override
    public String toString() {
        return "Address{" +
                "streetAddress='" + streetAddress + '\'' +
                ", cityName='" + cityName + '\'' +
                ", countryName='" + countryName + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }
}
