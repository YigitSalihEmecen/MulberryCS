package io.ysalih.mulberryCS.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Realestate {

    @Id @GeneratedValue
    private Long id;

    private String name;
    private String address;
    private String city;
    private Integer sqm;
    private Integer price;
    private String type;
    private Integer yearBuilt;
    private String status;

    public Realestate() {
    }

    public Realestate(String name, Integer price, String type, Integer sqm, String city, String status, Integer yearBuilt) {
        this.name = name;
        this.price = price;
        this.type = type;
        this.sqm = sqm;
        this.city = city;
        this.yearBuilt = yearBuilt;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getSqm() {
        return sqm;
    }

    public void setSqm(Integer sqm) {
        this.sqm = sqm;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getYearBuilt() {
        return yearBuilt;
    }

    public void setYearBuilt(Integer yearBuilt) {
        this.yearBuilt = yearBuilt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "[" +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", sqm=" + sqm +
                ", price=" + price +
                ", type='" + type + '\'' +
                ", yearBuilt=" + yearBuilt +
                "]\n";
    }
}
