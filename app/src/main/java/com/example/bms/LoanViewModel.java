package com.example.bms;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class LoanViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<LoanApplication>> loanApplications = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Loan>> loans = new MutableLiveData<>();
    public void setLoanApplications(ArrayList<LoanApplication> loanApplicationArrayList){
        loanApplications.setValue(loanApplicationArrayList);
    }

    public void setLoans(ArrayList<Loan> loanArrayList){
        loans.setValue(loanArrayList);
    }
    public MutableLiveData<ArrayList<LoanApplication>> getLoanApplications() {
        return loanApplications;
    }

    public MutableLiveData<ArrayList<Loan>> getLoans(){
        return loans;
    }
}
