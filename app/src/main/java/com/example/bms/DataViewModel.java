package com.example.bms;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DataViewModel extends ViewModel{
    private final MutableLiveData<UserEmailPhoneIds> data = new MutableLiveData<>();
    private UserEmailPhoneIds userEmailPhoneIds = new UserEmailPhoneIds();
    private final MutableLiveData<Uri> profile = new MutableLiveData<>();

    public void setData(UserEmailPhoneIds data1) {
        userEmailPhoneIds.addPhoneNumbers(data1.getPhoneNumbers());
        userEmailPhoneIds.addEmails(data1.getEmails());
        userEmailPhoneIds.addUserIds(data1.getUserIds());
        data.setValue(userEmailPhoneIds);
    }

    public void setProfile(Uri uri) {
        profile.setValue(uri);
    }

    public MutableLiveData<UserEmailPhoneIds> getUserEmailPhoneIds() {
        return data;
    }

    public LiveData<Uri> getProfile() { return profile; }

}
