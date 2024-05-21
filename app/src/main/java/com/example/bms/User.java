package com.example.bms;

import java.io.Serializable;

public class User implements Serializable {
    private String phone;
    private String role;
    private int pin;
    private String id;
    private String email;

    public User() {
    }

    public User(String phone, String role, int pin, String id, String email) {
        this.phone = phone;
        this.role = role;
        this.pin = pin;
        this.id = id;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }
}
