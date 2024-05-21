package com.example.bms;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class EmployeeViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<Employee>> employees = new MutableLiveData<>();
    public void setEmployees(ArrayList<Employee> employeeArrayList) {
        employees.setValue(employeeArrayList);
    }
    public LiveData<ArrayList<Employee>> getEmployees() {
        return employees;
    }

}
