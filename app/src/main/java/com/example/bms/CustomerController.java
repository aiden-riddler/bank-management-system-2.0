package com.example.bms;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.Date;
import java.util.List;

public class CustomerController {
    public static void createCustomer(Customer customer, Context context, ConstraintLayout progressCard, Branch branch, Button submit, User user1, Employee employee) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        WriteBatch batch = db.batch();
        DocumentReference customersRef = db.collection("Customers").document();
        batch.set(customersRef, customer);
        DocumentReference userRef = db.collection("Users").document(customersRef.getId());
        User user = new User(customer.getPhone(), "Customer", customer.getPin(), customersRef.getId(), customer.getEmail());
        batch.set(userRef, user);

        Account account = new Account(customersRef.getId(), branch.getId(), new Date(), 50.00, 17.5);
        DocumentReference accountRef = db.collection("Accounts").document();
        batch.set(accountRef, account);

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    progressCard.setVisibility(View.INVISIBLE);
                    Toast.makeText(context, "Customer Added Successfully!", Toast.LENGTH_LONG).show();
                    Intent intent = null;
                    if (user1.getRole().equals("Teller"))
                        intent = new Intent(context, EmployeePanel.class);
                    else
                        intent = new Intent(context, AdminPanel.class);
                    intent.putExtra("Employee", employee);
                    intent.putExtra("User", user1);
                    context.startActivity(intent);
                } else {
                    progressCard.setVisibility(View.INVISIBLE);
                    Toast.makeText(context, "Customer Account Creation Failed!", Toast.LENGTH_LONG).show();
                    submit.setEnabled(true);
                }
            }
        });
    }

    public static void removeCustomer(Customer customer, Context context, User user, Employee employee) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        WriteBatch batch = db.batch();
        DocumentReference customersRef = db.collection("Customers").document(customer.getCustomerId());
        batch.delete(customersRef);
        DocumentReference userRef = db.collection("Users").document(customer.getCustomerId());
        batch.delete(userRef);
        db.collection("Accounts")
                .whereEqualTo("customer", customer.getCustomerId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots) {
                            String id = documentSnapshot.getId();
                            DocumentReference accRef = db.collection("Accounts").document(id);
                            batch.delete(accRef);
                        }

                        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(context, "Customer removed", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Error removing customer!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(context, AdminPanel.class);
                                    intent.putExtra("User", user);
                                    intent.putExtra("Employee", employee);
                                    context.startActivity(intent);
                                }
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Error removing customer!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, AdminPanel.class);
                        context.startActivity(intent);
                    }
                });
    }

    public static void updateCustomer(Customer customer, Context context, Customer prevCust, ConstraintLayout progressCard, Button submit, User user, Employee employee){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        WriteBatch batch = db.batch();
        DocumentReference customerRef = db.collection("Customers").document(prevCust.getCustomerId());
        batch.update(customerRef, "email", customer.getEmail());
        batch.update(customerRef, "phone", customer.getPhone());
        batch.update(customerRef, "firstName", customer.getFirstName());
        batch.update(customerRef, "lastName", customer.getLastName());
        batch.update(customerRef, "userid", customer.getUserid());

        if (!prevCust.getPhone().equals(customer.getPhone())) {
            DocumentReference userRef = db.collection("Users").document(prevCust.getCustomerId());
            batch.update(userRef, "phone", customer.getPhone());
        } else if(!customer.getEmail().equals(prevCust.getEmail())){
            DocumentReference userRef = db.collection("Users").document(prevCust.getCustomerId());
            batch.update(userRef, "email", customer.getEmail());
            user.setEmail(customer.getEmail());
        }
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    progressCard.setVisibility(View.INVISIBLE);
                    Toast.makeText(context, "Update success", Toast.LENGTH_SHORT).show();
                    Intent intent = null;
                    if (user.getRole().equals("Teller")) {
                        intent = new Intent(context, EmployeePanel.class);
                        intent.putExtra("Employee", employee);
                    }   else if (user.getRole().equals("Manager")) {
                        intent = new Intent(context, AdminPanel.class);
                        intent.putExtra("Employee", employee);
                    }
                    else {
                        intent = new Intent(context, CustomerPanel.class);
                        intent.putExtra("Customer", customer);
                    }

                    intent.putExtra("User", user);
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    Log.d("BMS", "Error updating: " + task.getException().getLocalizedMessage());
                    progressCard.setVisibility(View.INVISIBLE);
                    submit.setEnabled(true);
                }
            }
        });
    }

    public static Customer getCustomerOfAccount(Account account) {
        return null;
    }

    public static Customer findCustomerByID(int id) {
        return null;
    }

    public static List<Customer> findCustomersByName(String name) {
        return null;
    }

}
