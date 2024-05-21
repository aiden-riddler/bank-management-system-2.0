package com.example.bms;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class CustomerPanel extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    private BottomNavigationView bottomNavigationView;
    private AccountViewModel accountViewModel;
    private LoanViewModel loanViewModel;
    private FirebaseFirestore db;
    private User user;
    private Customer customer;
    private CustomerViewModel customerViewModel;
    HomeFrag firstFragment = new HomeFrag();
    AccountsFrag secondFragment = new AccountsFrag();
    AccountFrag thirdFragment = new AccountFrag();
    NotificationsFrag fourthFragment = new NotificationsFrag();
    private DataViewModel dataViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_panel);

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        user = (User) getIntent().getSerializableExtra("User");
        customer = (Customer) getIntent().getSerializableExtra("Customer");
        customerViewModel = new ViewModelProvider(this).get(CustomerViewModel.class);
        dataViewModel = new ViewModelProvider(this).get(DataViewModel.class);

        StorageReference profileRef = FirebaseStorage.getInstance().getReference("ProfileImages/" + user.getId() + ".jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                dataViewModel.setProfile(uri);
            }
        });

        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        db = FirebaseFirestore.getInstance();
        db.collection("Accounts")
                .whereEqualTo("customer", user.getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Account> accounts = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Account account = document.toObject(Account.class);
                                account.setAccountNumber(document.getId());
                                accounts.add(account);
                            }
                            accountViewModel.setAccounts(accounts);
                        } else
                            Log.d("BMS", "Error getting documents: ", task.getException());

                    }
                });

        loanViewModel = new ViewModelProvider(this).get(LoanViewModel.class);
        db.collection("LoanApplications")
                .whereEqualTo("customer", user.getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<LoanApplication> loanApplications = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                LoanApplication loanApplication = document.toObject(LoanApplication.class);
                                loanApplication.setLoanNumber(document.getId());
                                loanApplications.add(loanApplication);
                            }
                            loanViewModel.setLoanApplications(loanApplications);
                        } else {
                            Log.d("BMS", "Error getting documents: ", task.getException());
                        }
                    }
                });

        db.collection("Loans")
                .whereEqualTo("customer", user.getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Loan> loans = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Loan loan = document.toObject(Loan.class);
                                loan.setId(document.getId());
                                loans.add(loan);
                            }
                            loanViewModel.setLoans(loans);
                        } else {
                            Log.d("BMS", "Error getting documents: ", task.getException());
                        }
                    }
                });

        db.collection("Customers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            UserEmailPhoneIds userEmailPhoneIds = new UserEmailPhoneIds();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Customer customer = document.toObject(Customer.class);
                                customer.setCustomerId(document.getId());

                                userEmailPhoneIds.addPhone(customer.getPhone());
                                userEmailPhoneIds.addEmail(customer.getEmail());
                                userEmailPhoneIds.addUserId(customer.getUserid());
                            }
                            dataViewModel.setData(userEmailPhoneIds);
                        } else {
                            Log.d("BMS", "Error getting documents: ", task.getException());
                        }
                    }
                });

        db.collection("Employees")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            UserEmailPhoneIds userEmailPhoneIds = new UserEmailPhoneIds();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Employee employee = document.toObject(Employee.class);
                                employee.setEmployeeID(document.getId());

                                userEmailPhoneIds.addPhone(employee.getPhone());
                                userEmailPhoneIds.addEmail(employee.getEmail());
                                userEmailPhoneIds.addUserId(employee.getUserid());
                            }
                            dataViewModel.setData(userEmailPhoneIds);
                        } else {
                            Log.d("BMS", "Error getting documents: ", task.getException());
                        }
                    }
                });

        db.collection("Transaction")
                .whereEqualTo("account1", user.getId())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("BMS", "Listen failed.", e);
                            return;
                        }

                        ArrayList<BMSTransaction> bmsTransactions = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            BMSTransaction bmsTransaction = doc.toObject(BMSTransaction.class);
                            bmsTransaction.setId(doc.getId());
                            bmsTransactions.add(bmsTransaction);
                        }
                        customerViewModel.setTransactions(bmsTransactions);
                    }
                });



        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        String openFragment = getIntent().getStringExtra("OpenFragment");
        if (openFragment != null){
            switch (openFragment){
                case "Accounts":
                    bottomNavigationView.setSelectedItemId(R.id.accounts);
                    break;
                case "Account":
                    bottomNavigationView.setSelectedItemId(R.id.account);
                    break;
                default:
                    bottomNavigationView.setSelectedItemId(R.id.home);
                    break;
            }
        } else {
            bottomNavigationView.setSelectedItemId(R.id.home);
        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, firstFragment).commit();
                return true;

            case R.id.accounts:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, secondFragment).commit();
                return true;

            case R.id.account:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, thirdFragment).commit();
                return true;

            case R.id.notification:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, fourthFragment).commit();
                return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private int count = 0;
    @Override
    public void onBackPressed() {
        count++;
        if (count > 1) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    count = 0;
                }
            }, 2000);
        }
//        super.onBackPressed();
    }
}
