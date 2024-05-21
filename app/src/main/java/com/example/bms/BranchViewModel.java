package com.example.bms;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class BranchViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<Branch>> branches = new MutableLiveData<>();
    public void setBranches(ArrayList<Branch> branchArrayList) {
        branches.setValue(branchArrayList);
    }
    public LiveData<ArrayList<Branch>> getBranches() {
        return branches;
    }
}
