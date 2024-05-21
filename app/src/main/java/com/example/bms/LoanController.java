package com.example.bms;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LoanController {
    public static void loanApplication(LoanApplication loanApplication, Context context, ConstraintLayout progressCard, Button submit, User user, Customer customer) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("LoanApplications")
                .add(loanApplication)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(context, "Loan application successful!", Toast.LENGTH_SHORT).show();
                        // calculate loan limit

                        progressCard.setVisibility(View.INVISIBLE);
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
                        Toast.makeText(context, "Loan application failed! " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        submit.setEnabled(true);
                    }
                });
    }
    public static void createLoan(LoanApplication application, ConstraintLayout progressCard, Context context, User user, Employee employee) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference accRef = db.collection("Accounts").document(application.getAccount());
        DocumentReference applicationRef = db.collection("LoanApplications").document(application.getLoanNumber());
        DocumentReference loanRef = db.collection("Loans").document();
        db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                Account account = transaction.get(accRef).toObject(Account.class);
                LoanApplication application1 = transaction.get(applicationRef).toObject(LoanApplication.class);

                double amountToPay = application1.getLoanAmount() * 1.17;
                int monthsToPay = (int) Math.ceil(amountToPay/ (0.3 * application1.getIncome()));
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MONTH, monthsToPay);

                Loan loan = new Loan(application1.getCustomer(), account.getBranch(), new Date(), calendar.getTime(), application1.getLoanAmount(), application.getLoanAmount(), monthsToPay);

                double balance = account.getCurrentBalance() + application1.getLoanAmount();
                transaction.update(accRef, "currentBalance", balance);
                transaction.update(applicationRef, "status", "Approved");
                transaction.set(loanRef, loan);
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressCard.setVisibility(View.INVISIBLE);
                        Toast.makeText(context, "Loan Approved", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, AdminPanel.class);
                        intent.putExtra("User", user);
                        intent.putExtra("Employee", employee);
                        intent.putExtra("OpenFragment", "LoanApplications");
                        context.startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressCard.setVisibility(View.INVISIBLE);
                        Toast.makeText(context, "Loan Approval failed " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("BMS", "ERROR: "  + e);

                        e.printStackTrace();
                    }
                });
    }

    public static void rejectApplication(LoanApplication loanApplication, ConstraintLayout progressCard, Context context, User user, Employee employee){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference applicationRef = db.collection("LoanApplications").document(loanApplication.getLoanNumber());
        db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                transaction.update(applicationRef, "status", "Rejected");
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressCard.setVisibility(View.INVISIBLE);
                        Toast.makeText(context, "Loan Application Rejected", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, AdminPanel.class);
                        intent.putExtra("User", user);
                        intent.putExtra("Employee", employee);
                        intent.putExtra("OpenFragment", "LoanApplications");
                        context.startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressCard.setVisibility(View.INVISIBLE);
                        Toast.makeText(context, "Loan Approval failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public static void repayLoan(Loan loan, String accountNumber, Double amountToPay, Context context, ConstraintLayout progressCard, Button payAmount, User user, Customer customer) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference accRef = db.collection("Accounts").document(accountNumber);
        DocumentReference loanRef = db.collection("Loans").document(loan.getId());
        db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                Account account = transaction.get(accRef).toObject(Account.class);
                Loan loan = transaction.get(loanRef).toObject(Loan.class);

                double balance = account.getCurrentBalance() - amountToPay;
                double amountDue = loan.getAmountDue() - amountToPay;
                transaction.update(accRef, "currentBalance", balance);
                transaction.update(loanRef, "amountDue", amountDue);
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressCard.setVisibility(View.INVISIBLE);
                        Toast.makeText(context, "Payment made succesfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, CustomerPanel.class);
                        intent.putExtra("User", user);
                        intent.putExtra("OpenFragment", "Accounts");
                        intent.putExtra("Customer", customer);
                        context.startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressCard.setVisibility(View.INVISIBLE);
                        Toast.makeText(context, "Payment failed", Toast.LENGTH_SHORT).show();
                        payAmount.setEnabled(true);
                    }
                });
    }

    public List<Loan> getLoansOfCustomer() {
        return null;
    }
    public String getLoanDetails() {
        return null;
    }

}
