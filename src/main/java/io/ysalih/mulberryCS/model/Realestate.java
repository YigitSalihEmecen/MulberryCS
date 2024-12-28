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
    private Double sqm;
    private Double price;
    private String type;
    private Integer yearBuilt;

    public Realestate() {
    }

    public Realestate(String name, Double price, String type, Double sqm, String city, String status, Integer yearBuilt) {
        this.name = name;
        this.price = price;
        this.type = type;
        this.sqm = sqm;
        this.city = city;
        this.yearBuilt = yearBuilt;
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

    public Double getSqm() {
        return sqm;
    }

    public void setSqm(Double sqm) {
        this.sqm = sqm;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
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
