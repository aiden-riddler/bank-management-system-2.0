package com.example.bms;

import java.util.Date;

import kotlinx.coroutines.channels.Receive;

public class BMSTransaction {

    private String id;
    private Date date;
    private Double amount;
    private String account1;
    private String account2;
    private String action;
    private String receiver;

    public BMSTransaction() {
    }

    public BMSTransaction(Date date, Double amount, String account1, String account2, String action, String receiver) {
        this.date = date;
        this.amount = amount;
        this.account1 = account1;
        this.account2 = account2;
        this.action = action;
        this.receiver = receiver;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }


    public String getAccount1() {
        return account1;
    }

    public void setAccount1(String account1) {
        this.account1 = account1;
    }

    public String getAccount2() {
        return account2;
    }

    public void setAccount2(String account2) {
        this.account2 = account2;
    }
}
