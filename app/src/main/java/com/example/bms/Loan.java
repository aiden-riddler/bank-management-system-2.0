package com.example.bms;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Loan implements Serializable {
    private String id;
    private String customer;
    private String branch;
    private Date startingDate;
    private Date dueDate;
    private Double amount;
    private Double amountDue;
    private int monthsToPay;

    public Loan() {
    }

    public Loan(String customer, String branch, Date startingDate, Date dueDate, Double amount, Double amountDue, int monthsToPay) {
        this.customer = customer;
        this.branch = branch;
        this.startingDate = startingDate;
        this.dueDate = dueDate;
        this.amount = amount;
        this.amountDue = amountDue;
        this.monthsToPay = monthsToPay;
    }

    public Double getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(Double amountDue) {
        this.amountDue = amountDue;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public Date getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(Date startingDate) {
        this.startingDate = startingDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public int getMonthsToPay() {
        return monthsToPay;
    }

    public void setMonthsToPay(int monthsToPay) {
        this.monthsToPay = monthsToPay;
    }
}
