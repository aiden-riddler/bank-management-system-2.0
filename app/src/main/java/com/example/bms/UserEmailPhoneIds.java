package com.example.bms;


import java.io.Serializable;
import java.util.ArrayList;

public class UserEmailPhoneIds implements Serializable {
    private ArrayList<String> phoneNumbers = new ArrayList<>();
    private ArrayList<String> emails = new ArrayList<>();
    private ArrayList<Integer> userIds = new ArrayList<>();

    public UserEmailPhoneIds() {
    }

    public UserEmailPhoneIds(ArrayList<String> phoneNumbers, ArrayList<String> emails, ArrayList<Integer> userIds) {
        this.phoneNumbers = phoneNumbers;
        this.emails = emails;
        this.userIds = userIds;
    }

    public void addPhone(String phone){
        phoneNumbers.add(phone);
    }

    public void addEmail(String email){
        emails.add(email);
    }

    public void addUserId(int userid){
        userIds.add(userid);
    }

    public void addPhoneNumbers(ArrayList<String> phoneNumbers) {
        for (String phone:phoneNumbers) {
            if (!this.phoneNumbers.contains(phone))
                addPhone(phone);
        }
    }

    public void addEmails(ArrayList<String> emails) {
        for (String email:emails) {
            if (!this.emails.contains(email))
                addEmail(email);
        }
    }

    public void addUserIds(ArrayList<Integer> userIds) {
        for (Integer userId:userIds) {
            if (!this.userIds.contains(userId))
                addUserId(userId);
        }
    }

    public ArrayList<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public ArrayList<String> getEmails() {
        return emails;
    }

    public ArrayList<Integer> getUserIds() {
        return userIds;
    }
}
