package com.techjd.shoppyadminfinal;

public class Product {
    public String date,pid,time,description,categoryName,name , price ,location ,image1,image2,image3,phone ;
    public Product(){

    }

    public Product(String date, String pid, String time, String description, String categoryName, String name, String price, String location, String image1, String image2, String image3, String phone) {
        this.date = date;
        this.pid = pid;
        this.time = time;
        this.description = description;
        this.categoryName = categoryName;
        this.name = name;
        this.price = price;
        this.location = location;
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
        this.phone = phone;

    }
}
