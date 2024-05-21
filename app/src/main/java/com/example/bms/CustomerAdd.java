package com.example.bms;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CustomerAdd extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextInputEditText emailView;
    private TextInputEditText firstNameView;
    private TextInputEditText phoneView;
    private TextInputEditText lastNameView;
    private TextInputEditText idView;
    private TextInputEditText pinView;
    private AutoCompleteTextView autoCompleteTextView;
    private ArrayAdapter<String> arrayAdapter;
    private Button submit;
    private List<Branch> branches;
    private boolean isUpdate = false;
    private Customer prevCust;
    private ConstraintLayout progressCard;
    private TextView progressText;
    private UserEmailPhoneIds userEmailPhoneIds;
    private User user;
    private Employee employee;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_customer);

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        user = (User) getIntent().getSerializableExtra("User");
        employee = (Employee) getIntent().getSerializableExtra("Employee");

        progressCard = findViewById(R.id.progress);
        progressText = findViewById(R.id.progressText);
        autoCompleteTextView = findViewById(R.id.autoComplete);

        // initialise firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        branches = new ArrayList<>();

        db.collection("Branch")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Branch branch = document.toObject(Branch.class);
                                branch.setId(document.getId());
                                branches.add(branch);
                            }

                            //add branches to string
                            String[] branchList = new String[branches.size()];
                            for (int i=0; i<branches.size(); i++)
                                branchList[i] = branches.get(i).getAddress();

                            // add items to dropdown
                            arrayAdapter = new ArrayAdapter<>(CustomerAdd.this, R.layout.list_item, branchList);
                            autoCompleteTextView.setAdapter(arrayAdapter);
                        } else {
                            Log.d("BMS", "Error getting documents: ", task.getException());
                        }
                    }
                });

        emailView = findViewById(R.id.email);
        firstNameView = findViewById(R.id.firstname);
        lastNameView = findViewById(R.id.lastname);
        phoneView = findViewById(R.id.phone);
        idView = findViewById(R.id.id_num);
        pinView = findViewById(R.id.pin);
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // validate input
                validateInput();
            }
        });

        // get intents
        prevCust = (Customer) getIntent().getSerializableExtra("Customer");
        userEmailPhoneIds = (UserEmailPhoneIds) getIntent().getSerializableExtra("UserEmailIDs");
        if (prevCust != null) {
            isUpdate = true;
            submit.setText("UPDATE");
            emailView.setText(prevCust.getEmail());
            firstNameView.setText(prevCust.getFirstName());
            lastNameView.setText(prevCust.getLastName());
            phoneView.setText(prevCust.getPhone().substring(4));
            idView.setText(String.valueOf(prevCust.getUserid()));
            autoCompleteTextView.setEnabled(false);
            pinView.setEnabled(false);
        }

    }

    private void validateInput() {
        firstNameView.setError(null);
        lastNameView.setError(null);
        emailView.setError(null);
        phoneView.setError(null);
        idView.setError(null);
        autoCompleteTextView.setError(null);
        pinView.setError(null);

        String firstName = firstNameView.getText().toString().trim();
        String lastName = lastNameView.getText().toString().trim();
        String email = emailView.getText().toString().trim();
        String phone = phoneView.getText().toString().trim();
        String id = idView.getText().toString().trim();
        String branchName = autoCompleteTextView.getText().toString();
        String pin = pinView.getText().toString().trim();

        boolean isValid = true;
        View focusView = null;

        if ((TextUtils.isEmpty(pin) || pin.length() != 4) && !isUpdate) {
            isValid = false;
            focusView = pinView;
            pinView.setError("Enter 4 digit pin");
        }

        if (TextUtils.isEmpty(branchName) && !isUpdate){
            isValid = false;
            focusView = autoCompleteTextView;
            autoCompleteTextView.setError("Please select branch");
        }

        if (!InputValidation.phoneValidate(phone)){
            isValid = false;
            focusView = phoneView;
            phoneView.setError("Phone number should be 9 characters long");
        } else if (userEmailPhoneIds.getPhoneNumbers().contains("+254" + phone.trim())) {
            if (isUpdate) {
                if (!prevCust.getPhone().equals("+254" + phone.trim())) {
                    isValid = false;
                    focusView = phoneView;
                    phoneView.setError("User with phone-number already exists!");
                }
            } else {
                isValid = false;
                focusView = phoneView;
                phoneView.setError("User with phone-number already exists!");
            }
        }

        if (!InputValidation.emailValidate(email)) {
            isValid = false;
            focusView = emailView;
            emailView.setError("Invalid Email Address");
        } else if (userEmailPhoneIds.getEmails().contains(email)) {
            if (isUpdate) {
                if (!prevCust.getEmail().equals(email)) {
                    isValid = false;
                    focusView = emailView;
                    emailView.setError("User with Email Address already exists");
                }
            } else {
                isValid = false;
                focusView = emailView;
                emailView.setError("User with Email Address already exists");
            }
        }

        if (TextUtils.isEmpty(id)){
            isValid = false;
            focusView = idView;
            idView.setError("This field is required");
        } else if (userEmailPhoneIds.getUserIds().contains(id.trim())) {
            if (isUpdate) {
                if (prevCust.getUserid() != Integer.parseInt(id.trim())) {
                    isValid = false;
                    focusView = idView;
                    idView.setError("User with ID already exists");
                }
            } else {
                isValid = false;
                focusView = idView;
                idView.setError("User with ID already exists");
            }
        }

        if (TextUtils.isEmpty(lastName)) {
            isValid = false;
            focusView = lastNameView;
            lastNameView.setError("This field is required");
        }

        if (TextUtils.isEmpty(firstName)) {
            isValid = false;
            focusView = firstNameView;
            firstNameView.setError("This field is required");
        }

        if (isValid){
            progressCard.setVisibility(View.VISIBLE);
            submit.setEnabled(false);

            if (!isUpdate) {
                progressText.setText("Adding Customer...");
                Customer customer = new Customer();
                customer.setUserid(Integer.parseInt(id));
                customer.setEmail(email);
                customer.setPhone("+254" + phone);
                customer.setFirstName(firstName);
                customer.setLastName(lastName);
                customer.setRegistrationDate(new Date());
                customer.setPin(Integer.parseInt(pin));

                Branch branch = null;
                for (Branch b:branches){
                    if (b.getAddress().equals(branchName))
                        branch = b;
                }

                CustomerController.createCustomer(customer, this, progressCard, branch, submit, user, employee);
            } else {
                progressText.setText("Updating Customer...");
                Customer customer = new Customer();
                customer.setUserid(Integer.parseInt(id));
                customer.setEmail(email);
                customer.setPhone("+254" + phone);
                customer.setFirstName(firstName);
                customer.setLastName(lastName);
                customer.setCustomerId(prevCust.getCustomerId());

                CustomerController.updateCustomer(customer, CustomerAdd.this, prevCust, progressCard, submit, user, employee);
            }

        } else {
            focusView.requestFocus();
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent = null;
        if (user.getRole().equals("Teller"))
            intent = new Intent(CustomerAdd.this, EmployeePanel.class);
        else
            intent = new Intent(CustomerAdd.this, AdminPanel.class);
        intent.putExtra("User", user);
        intent.putExtra("Employee", employee);
        startActivity(intent);
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

}
