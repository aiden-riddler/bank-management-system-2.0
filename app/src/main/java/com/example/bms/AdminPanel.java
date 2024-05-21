package com.example.bms;

import static android.Manifest.permission.MANAGE_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AdminPanel extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CustomerViewModel viewModel;
    private AccountViewModel accountViewModel;
    private BranchViewModel branchViewModel;
    private EmployeeViewModel employeeViewModel;
    private ConstraintLayout progressCard;
    private TextView progressText;
    private DataViewModel dataViewModel;
    private LoanViewModel loanViewModel;
    private User user;

    private static final int PERMISSION_REQUEST_CODE = 200;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_panel);

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (checkPermission()) {
//            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission();
        }

        progressCard = findViewById(R.id.progress);
        progressText = findViewById(R.id.progressText);

        // get data
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        dataViewModel = new ViewModelProvider(this).get(DataViewModel.class);
        user = (User) getIntent().getSerializableExtra("User");
        StorageReference profileRef = FirebaseStorage.getInstance().getReference("ProfileImages/" + user.getId() + ".jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                dataViewModel.setProfile(uri);
            }
        });

        viewModel = new ViewModelProvider(this).get(CustomerViewModel.class);
        db.collection("Customers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            UserEmailPhoneIds userEmailPhoneIds = new UserEmailPhoneIds();
                            ArrayList<Customer> customers = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Customer customer = document.toObject(Customer.class);
                                customer.setCustomerId(document.getId());
                                customers.add(customer);

                                userEmailPhoneIds.addPhone(customer.getPhone());
                                userEmailPhoneIds.addEmail(customer.getEmail());
                                userEmailPhoneIds.addUserId(customer.getUserid());
                            }
                            viewModel.setCustomers(customers);
                            dataViewModel.setData(userEmailPhoneIds);
                        } else {
                            Log.d("BMS", "Error getting documents: ", task.getException());
                        }
                    }
                });

        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        db.collection("Accounts")
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
                        } else {
                            Log.d("BMS", "Error getting documents: ", task.getException());
                        }
                    }
                });

        employeeViewModel = new ViewModelProvider(this).get(EmployeeViewModel.class);
        db.collection("Employees")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Employee> employees = new ArrayList<>();
                            UserEmailPhoneIds userEmailPhoneIds = new UserEmailPhoneIds();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Employee employee = document.toObject(Employee.class);
                                employee.setEmployeeID(document.getId());
                                employees.add(employee);

                                userEmailPhoneIds.addPhone(employee.getPhone());
                                userEmailPhoneIds.addEmail(employee.getEmail());
                                userEmailPhoneIds.addUserId(employee.getUserid());
                            }
                            employeeViewModel.setEmployees(employees);
                            dataViewModel.setData(userEmailPhoneIds);
                        } else {
                            Log.d("BMS", "Error getting documents: ", task.getException());
                        }
                    }
                });

        branchViewModel = new ViewModelProvider(this).get(BranchViewModel.class);
        db.collection("Branch")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Branch> branches = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Branch branch = document.toObject(Branch.class);
                                branch.setId(document.getId());
                                branches.add(branch);
                            }
                            branchViewModel.setBranches(branches);
                        } else {
                            Log.d("BMS", "Error getting documents: ", task.getException());
                        }
                    }
                });

        loanViewModel = new ViewModelProvider(this).get(LoanViewModel.class);
        db.collection("LoanApplications")
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

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        String openFragment = getIntent().getStringExtra("OpenFragment");
        if (openFragment != null){
            switch (openFragment){
                case "LoanApplications":
                    bottomNavigationView.setSelectedItemId(R.id.loans);
                    break;
                case "Account":
                    bottomNavigationView.setSelectedItemId(R.id.account);
                    break;
                case "Employee":
                    bottomNavigationView.setSelectedItemId(R.id.tellers);
                    break;
                default:
                    bottomNavigationView.setSelectedItemId(R.id.customers);
                    break;
            }
        } else {
            bottomNavigationView.setSelectedItemId(R.id.customers);
        }

    }
    CustomerFrag firstFragment = new CustomerFrag();
    EmployeeFrag secondFragment = new EmployeeFrag();
    BranchFrag thirdFragment = new BranchFrag();
    AccountFrag fourthFragment = new AccountFrag();
    LoanApplicationsFrag fifthFragment = new LoanApplicationsFrag();
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.customers:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, firstFragment).commit();
                return true;

            case R.id.tellers:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, secondFragment).commit();
                return true;

            case R.id.branch:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, thirdFragment).commit();
                return true;

            case R.id.account:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, fourthFragment).commit();
                return true;

            case R.id.loans:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, fifthFragment).commit();
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

    private boolean checkPermission() {
        // checking of permissions.
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int permission3 = ContextCompat.checkSelfPermission(getApplicationContext(), MANAGE_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED && permission3 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, MANAGE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {

                // after requesting permissions we are showing
                // users a toast message of permission granted.
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage) {
//                    Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }
}
