package com.example.bms;

import java.io.Serializable;

public class Branch implements Serializable {

    private String id;

    private String address;

    private String phone;

    public Branch() {
    }

    public Branch(String address, String phone) {
        this.address = address;
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
