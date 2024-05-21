package com.example.bms;

public class LoanApplication {

    private String customer;
    private String account;
    private String loanNumber;
    private Double loanAmount;
    private Double annualIncome;
    private String employer;
    private String occupation;
    private Double income;
    private Double mortgage;
    private String loanPurpose;
    private String status = "Waiting Approval";
    private Double loanLimit = 0.0;

    public LoanApplication() {
    }
    public LoanApplication(Double loanAmount, Double annualIncome, String employer, String occupation, Double income, Double mortgage, String loanPurpose) {
        this.loanAmount = loanAmount;
        this.annualIncome = annualIncome;
        this.employer = employer;
        this.occupation = occupation;
        this.income = income;
        this.mortgage = mortgage;
        this.loanPurpose = loanPurpose;
    }

    public Double getLoanLimit() {
        return loanLimit;
    }

    public void setLoanLimit(Double loanLimit) {
        this.loanLimit = loanLimit;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getLoanNumber() {
        return loanNumber;
    }

    public void setLoanNumber(String loanNumber) {
        this.loanNumber = loanNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(Double loanAmount) {
        this.loanAmount = loanAmount;
    }

    public Double getAnnualIncome() {
        return annualIncome;
    }

    public void setAnnualIncome(Double annualIncome) {
        this.annualIncome = annualIncome;
    }

    public String getEmployer() {
        return employer;
    }

    public void setEmployer(String employer) {
        this.employer = employer;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public Double getIncome() {
        return income;
    }

    public void setIncome(Double income) {
        this.income = income;
    }

    public Double getMortgage() {
        return mortgage;
    }

    public void setMortgage(Double mortgage) {
        this.mortgage = mortgage;
    }

    public String getLoanPurpose() {
        return loanPurpose;
    }

    public void setLoanPurpose(String loanPurpose) {
        this.loanPurpose = loanPurpose;
    }
}
