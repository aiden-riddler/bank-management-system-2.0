package com.example.bms;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AccountController {
    public static void createAccount(Account account, Context context, ConstraintLayout progressBar, Button submit) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Accounts")
                .add(account)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // create account
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(context, "Account Created Successfully", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(context, AdminPanel.class);
                        context.startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(context, "Bank Account creation failed! Contact Admin", Toast.LENGTH_LONG).show();
                        submit.setEnabled(true);
                    }
                });
    }

    public static boolean updateAccount() {
        return false;
    }

    public static boolean removeAccount() {
        return false;
    }

    public static List<Account> getAccountsOfCustomer() {
        return null;
    }

    public static String getAccountDetails() {
        return null;
    }
}
