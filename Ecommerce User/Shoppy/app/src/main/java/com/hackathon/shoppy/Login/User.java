package com.hackathon.shoppy.Login;

class User {
    public String name,email ;
    public  User(){

    }

    public User(String name) {
        this.name = name;
    }

    public User(String uid, String email){
        this.name =uid;
        this.email =email;
    }
}
