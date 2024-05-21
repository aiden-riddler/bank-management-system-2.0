package com.example.bms;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class AccountViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<Account>> accounts = new MutableLiveData<>();
    public void setAccounts(ArrayList<Account> accountsArraylist) {
        accounts.setValue(accountsArraylist);
    }
    public LiveData<ArrayList<Account>> getAccounts() {
        return accounts;
    }

}
