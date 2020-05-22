package com.hackathon.shoppy.Model;

public class Product {
    private String name;
    private String price;
    private String description;
    private String categoryName;
    private String location;
    private String pid;
    private String date;
    private String time;
    private String image1;
    private String image2;
    private String image3;
    private String uid;

    public Product() {
    }

    public Product(String name, String price, String description, String categoryName, String location, String pid, String date, String time, String image1, String image2, String image3, String uid) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.categoryName = categoryName;
        this.location = location;
        this.pid = pid;
        this.date = date;
        this.time = time;
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getImage3() {
        return image3;
    }

    public void setImage3(String image3) {
        this.image3 = image3;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
