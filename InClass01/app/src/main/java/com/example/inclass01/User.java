package com.example.inclass01;

import java.io.Serializable;

public class User implements Serializable {

    public String first, last, email, uID, password, gender, city, imageURL;


    public User(String uID, String first, String last, String email, String password, String gender,String city,String image) {
        this.first = first;
        this.last = last;
        this.email = email;
        this.uID = uID;
        this.password = password;
        this.city = city;
        this.gender = gender;
        this.imageURL=image;
    }

    public User()
    {

    }


}