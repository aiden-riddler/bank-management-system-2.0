package com.example.bms;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.Date;

public class FundsTransferController {
    public static void makeTransfer(String accountNumber, Double amountToTransfer, String accountPhone, Context context, ConstraintLayout progressCard, TextView progressText, Button transfer, User user, View accountDialog, Customer customer) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Customers")
                .whereEqualTo("phone", accountPhone)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Customer customer1 = null;
                        for (QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots) {
                            customer1 = documentSnapshot.toObject(Customer.class);
                            customer1.setCustomerId(documentSnapshot.getId());
                        }

                        if (queryDocumentSnapshots.size() > 0){
                            String firstName = InputValidation.toTitleCase(customer1.getFirstName());
                            String lastName = InputValidation.toTitleCase(customer1.getLastName());
                            db.collection("Accounts")
                                    .whereEqualTo("customer", customer1.getCustomerId())
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            ArrayList<Account> accounts = new ArrayList<>();
                                            for (QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                                                Account account = documentSnapshot.toObject(Account.class);
                                                account.setAccountNumber(documentSnapshot.getId());
                                                accounts.add(account);
                                            }

                                            progressCard.setVisibility(View.INVISIBLE);
                                            if (queryDocumentSnapshots.size() > 0){

                                                AutoCompleteTextView accountView = accountDialog.findViewById(R.id.account);
                                                String[] accountList = new String[accounts.size()];
                                                for (int i=0; i<accounts.size(); i++)
                                                    accountList[i] = accounts.get(i).getAccountNumber();

                                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.list_item, accountList);
                                                accountView.setAdapter(arrayAdapter);
                                                accountView.setListSelection(0);

                                                new MaterialAlertDialogBuilder(context)
                                                        .setTitle("Confirm Info")
                                                                .setMessage("Send Ksh." + amountToTransfer + " to " + firstName + " " + lastName + ". Transaction cost Ksh.0.00")
                                                                    .setView(accountDialog)
                                                                        .setPositiveButton("SEND", new DialogInterface.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                                String finalAccount = accountView.getText().toString();
                                                                                if (TextUtils.isEmpty(finalAccount)) {
                                                                                    accountView.setError("Select Account Number");
                                                                                    accountView.requestFocus();
                                                                                } else {
                                                                                    dialogInterface.cancel();
                                                                                    progressCard.setVisibility(View.VISIBLE);
                                                                                    progressText.setText("Making Transfer...");
                                                                                    deposit(finalAccount, accountNumber, amountToTransfer, context, progressCard, transfer, user, customer, firstName + " " + lastName);
                                                                                }
                                                                            }
                                                                        })
                                                        .setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                dialogInterface.cancel();
                                                                transfer.setEnabled(true);
                                                            }
                                                        }).setCancelable(false).show();

                                            } else {
                                                Toast.makeText(context, "An error occurred. Try again later.", Toast.LENGTH_SHORT).show();
                                                transfer.setEnabled(true);
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressCard.setVisibility(View.INVISIBLE);
                                            Toast.makeText(context, "An error occurred. Try again later.", Toast.LENGTH_SHORT).show();
                                            transfer.setEnabled(true);
                                        }
                                    });
                        } else {
                            progressCard.setVisibility(View.INVISIBLE);
                            Toast.makeText(context, "Transfer failed. Account with phone number does not exist.", Toast.LENGTH_SHORT).show();
                            transfer.setEnabled(true);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressCard.setVisibility(View.INVISIBLE);
                        Toast.makeText(context, "Transfer failed. Account with phone number does not exist.", Toast.LENGTH_SHORT).show();
                        transfer.setEnabled(true);
                    }
                });
    }

    public static void depositCustomer(String accountToTransferTo, Double amountToTransfer, Context context, ConstraintLayout progressCard, Button depBtn, User user, Employee employee) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference receiverRef = db.collection("Accounts").document(accountToTransferTo);
        DocumentReference rTransactionRef = db.collection("Transaction").document();
        db.runTransaction(new Transaction.Function<Void>() {
                    @Nullable
                    @Override
                    public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                        Account receiverAcc = transaction.get(receiverRef).toObject(Account.class);

                        double receiverBalance = receiverAcc.getCurrentBalance() + amountToTransfer;

                        transaction.update(receiverRef, "currentBalance", receiverBalance);
                        Date date = new Date();
                        transaction.set(rTransactionRef, new BMSTransaction(date, amountToTransfer, receiverAcc.getCustomer(), employee.getEmployeeID(), "Deposit", "Self"));
                        return null;
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressCard.setVisibility(View.INVISIBLE);
                        Toast.makeText(context, "Transaction successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, EmployeePanel.class);
                        intent.putExtra("User", user);
                        intent.putExtra("Employee", employee);
                        context.startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressCard.setVisibility(View.INVISIBLE);
                        Toast.makeText(context, "Transaction failed", Toast.LENGTH_SHORT).show();
                        depBtn.setEnabled(true);
                    }
                });
    }

    public static void withdrawCustomer(String tellerPhone, String accountToWithdrawFrom, Double amountToWithdraw, Context context, ConstraintLayout progressCard, Button depBtn, User user, Customer customer, ArrayList<Account> accountArrayList) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Employees")
                .whereEqualTo("phone", tellerPhone)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            Employee employee = null;
                            for (DocumentSnapshot documentSnapshot:task.getResult()){
                                employee = documentSnapshot.toObject(Employee.class);
                                employee.setEmployeeID(documentSnapshot.getId());
                            }

                            DocumentReference receiverRef = db.collection("Accounts").document(accountToWithdrawFrom);
                            DocumentReference rTransactionRef = db.collection("Transaction").document();
                            Employee finalEmployee = employee;
                            if (employee != null){
                                db.runTransaction(new Transaction.Function<Void>() {
                                            @Nullable
                                            @Override
                                            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                                Account receiverAcc = transaction.get(receiverRef).toObject(Account.class);

                                                double receiverBalance = receiverAcc.getCurrentBalance() - amountToWithdraw;

                                                transaction.update(receiverRef, "currentBalance", receiverBalance);
                                                Date date = new Date();
                                                transaction.set(rTransactionRef, new BMSTransaction(date, amountToWithdraw, receiverAcc.getCustomer(), finalEmployee.getEmployeeID(), "Withdraw", "Self"));
                                                return null;
                                            }
                                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                progressCard.setVisibility(View.INVISIBLE);
                                                Toast.makeText(context, "Transaction successful", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(context, CustomerPanel.class);
                                                intent.putExtra("User", user);
                                                intent.putExtra("Customer", customer);
                                                intent.putExtra("Accounts", accountArrayList);
                                                context.startActivity(intent);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressCard.setVisibility(View.INVISIBLE);
                                                Toast.makeText(context, "Transaction failed", Toast.LENGTH_SHORT).show();
                                                depBtn.setEnabled(true);
                                            }
                                        });
                            } else {
                                progressCard.setVisibility(View.INVISIBLE);
                                Toast.makeText(context, "Invalid Teller Number", Toast.LENGTH_SHORT).show();
                                depBtn.setEnabled(true);
                            }
                        } else {
                            progressCard.setVisibility(View.INVISIBLE);
                            Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show();
                            depBtn.setEnabled(true);
                        }
                    }
                });
    }

    public static void deposit(String accountToTransferTo, String accountToTransferFrom, Double amountToTransfer, Context context, ConstraintLayout progressCard, Button transfer, User user, Customer customer, String receiverName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference senderRef = db.collection("Accounts").document(accountToTransferFrom);
        DocumentReference receiverRef = db.collection("Accounts").document(accountToTransferTo);
        DocumentReference rTransactionRef = db.collection("Transaction").document();
        DocumentReference sTransactionRef = db.collection("Transaction").document();
        db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                Account senderAcc = transaction.get(senderRef).toObject(Account.class);
                Account receiverAcc = transaction.get(receiverRef).toObject(Account.class);

                double senderBalance = senderAcc.getCurrentBalance() - amountToTransfer;
                double receiverBalance = receiverAcc.getCurrentBalance() + amountToTransfer;

                transaction.update(senderRef, "currentBalance", senderBalance);
                transaction.update(receiverRef, "currentBalance", receiverBalance);
                Date date = new Date();
                transaction.set(sTransactionRef, new BMSTransaction(date, amountToTransfer, senderAcc.getCustomer(), receiverAcc.getCustomer(), "Send", receiverName));
                transaction.set(rTransactionRef, new BMSTransaction(date, amountToTransfer, receiverAcc.getCustomer(), senderAcc.getCustomer(), "Receive", InputValidation.toTitleCase(customer.getFirstName()) + " " + InputValidation.toTitleCase(customer.getLastName())));

                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressCard.setVisibility(View.INVISIBLE);
                        Toast.makeText(context, "Transaction successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, CustomerPanel.class);
                        intent.putExtra("User", user);
                        intent.putExtra("Customer", customer);
                        context.startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressCard.setVisibility(View.INVISIBLE);
                        Toast.makeText(context, "Transaction failed", Toast.LENGTH_SHORT).show();
                        transfer.setEnabled(true);
                    }
                });
    }
}
