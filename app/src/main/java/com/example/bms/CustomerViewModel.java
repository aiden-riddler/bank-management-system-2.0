package com.example.bms;

import android.net.Uri;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;

public class CustomerViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<Customer>> customers = new MutableLiveData<>();
    private final MutableLiveData<Customer> customerMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<BMSTransaction>> transactions = new MutableLiveData<>();
    public void setCustomers(ArrayList<Customer> customerArrayList) { customers.setValue(customerArrayList); }
    public void setCustomer(Customer customer){
        customerMutableLiveData.setValue(customer);
    }
    public void setTransactions(ArrayList<BMSTransaction> bmsTransactions){ transactions.setValue(bmsTransactions); }
    public LiveData<ArrayList<BMSTransaction>> getBMSTransactions(){
        return transactions;
    }
    public LiveData<ArrayList<Customer>> getCustomers() {
        return customers;
    }
    public LiveData<Customer> getCustomer() { return customerMutableLiveData; }
}
