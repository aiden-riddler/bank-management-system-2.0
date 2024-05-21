package com.example.bms;

import java.io.Serializable;

public class Employee extends Person implements Serializable {
    private String position;
    private String branch;
    private String employeeID;

    public Employee() {

    }
    public String getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }
}
