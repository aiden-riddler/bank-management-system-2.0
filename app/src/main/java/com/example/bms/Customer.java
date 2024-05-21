package com.example.bms;

import java.io.Serializable;
import java.util.List;

public class Customer extends Person implements Serializable {
    private List<String> accounts;
    private List<String> loans;

    private String customerId;
    public Customer() {

    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public List<String> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<String> accounts) {
        this.accounts = accounts;
    }

    public List<String> getLoans() {
        return loans;
    }

    public void setLoans(List<String> loans) {
        this.loans = loans;
    }
}
