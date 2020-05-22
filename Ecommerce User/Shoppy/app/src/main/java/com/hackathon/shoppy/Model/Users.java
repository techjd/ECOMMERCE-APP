package com.hackathon.shoppy.Model;

public class Users {
    String name, phone,address,image,uid;

    public Users() {
    }

    public Users(String name, String phoneno, String address, String image, String uid) {
        this.name = name;
        this.phone = phoneno;
        this.address = address;
        this.image = image;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
