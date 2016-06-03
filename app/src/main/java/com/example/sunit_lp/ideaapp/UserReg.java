package com.example.sunit_lp.ideaapp;



import java.io.Serializable;


public class UserReg implements Serializable{

    String name, email, mobile, profile_pic;

    public UserReg() {
        //  empty default constructor, necessary for Firebase to be able to deserialize data
    }


    public UserReg(String name, String email, String mobile,String profile_pic) {
        this.name=name;
        this.email=email;
        this.mobile=mobile;
        this.profile_pic=profile_pic;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    }
