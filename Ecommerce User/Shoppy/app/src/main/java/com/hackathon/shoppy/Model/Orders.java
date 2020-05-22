package com.hackathon.shoppy.Model;

public class Orders {
    private String pName ,pid,pPrice,pQuantity,orderDate,orderTime,status,customerName,email,address , orderId;

    public Orders() {
    }

    public Orders(String pName, String pid, String pPrice, String pQuantity, String orderDate, String orderTime, String status, String customerName, String email, String address, String orderId) {
        this.pName = pName;
        this.pid = pid;
        this.pPrice = pPrice;
        this.pQuantity = pQuantity;
        this.orderDate = orderDate;
        this.orderTime = orderTime;
        this.status = status;
        this.customerName = customerName;
        this.email = email;
        this.address = address;
        this.orderId = orderId;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getpPrice() {
        return pPrice;
    }

    public void setpPrice(String pPrice) {
        this.pPrice = pPrice;
    }

    public String getpQuantity() {
        return pQuantity;
    }

    public void setpQuantity(String pQuantity) {
        this.pQuantity = pQuantity;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
