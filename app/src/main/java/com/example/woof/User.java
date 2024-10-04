package com.example.woof;

import java.util.Date;

public class User {
    private int id;
    private String uName;
    private int uPhone;
    private String uEmail;
    private Date dob;
    private String uRole;
    private String pass;

    public User() {}

    public User(String uName, int uPhone, String uEmail, Date dob, String pass) {
        this.uName = uName;
        this.uPhone = uPhone;
        this.uEmail = uEmail;
        this.dob = dob;
        this.pass = pass;
        this.uRole = "cus";
    }


    public User(int id, String uName, int uPhone, String uEmail, Date dob, String uRole, String pass) {
        this.id = id;
        this.uName = uName;
        this.uPhone = uPhone;
        this.uEmail = uEmail;
        this.dob = dob;
        this.uRole = uRole;
        this.pass = pass;
    }

    public User(String name, int phoneNumber, String regMail, Date dob) {
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public int getuPhone() {
        return uPhone;
    }

    public void setuPhone(int uPhone) {
        this.uPhone = uPhone;
    }

    public String getuEmail() {
        return uEmail;
    }

    public void setuEmail(String uEmail) {
        this.uEmail = uEmail;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getuRole() {
        return uRole;
    }

    public void setuRole(String uRole) {
        this.uRole = uRole;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
