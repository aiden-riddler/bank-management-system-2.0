package com.example.bms;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EmployeeAdd extends AppCompatActivity {
    private FirebaseFirestore db;
    private TextInputEditText emailView;
    private TextInputEditText firstNameView;
    private TextInputEditText phoneView;
    private TextInputEditText lastNameView;
    private TextInputEditText idView;
    private TextInputEditText pinView;
    private AutoCompleteTextView positionView;
    private AutoCompleteTextView autoCompleteTextView;
    private ArrayAdapter<String> arrayAdapter;
    private Button submit;
    private List<Branch> branches;
    private boolean isUpdate = false;
    private Employee prevEmp;
    private ConstraintLayout progressCard;
    private TextView progressText;
    private UserEmailPhoneIds userEmailPhoneIds;
    private User user;
    private Employee adminEmployee;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_employee);

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        user = (User) getIntent().getSerializableExtra("User");

        progressCard = findViewById(R.id.progress);
        progressText = findViewById(R.id.progressText);
        autoCompleteTextView = findViewById(R.id.autoComplete);


        // initialise firebase
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
                            arrayAdapter = new ArrayAdapter<>(EmployeeAdd.this, R.layout.list_item, branchList);
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
        positionView = findViewById(R.id.position);
        positionView.setAdapter(new ArrayAdapter<>(EmployeeAdd.this, R.layout.list_item, new String[] {"Teller", "Manager"}));
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // validate input
                validateInput();
            }
        });

        // get intents
        prevEmp = (Employee) getIntent().getSerializableExtra("Employee");
        adminEmployee = (Employee) getIntent().getSerializableExtra("AdminEmployee");
        userEmailPhoneIds = (UserEmailPhoneIds) getIntent().getSerializableExtra("UserEmailIDs");
        boolean edit = getIntent().getBooleanExtra("Edit", false);
        if (edit) {
            isUpdate = true;
            submit.setText("UPDATE");
            emailView.setText(prevEmp.getEmail());
            firstNameView.setText(prevEmp.getFirstName());
            lastNameView.setText(prevEmp.getLastName());
            phoneView.setText(prevEmp.getPhone().substring(4));
            idView.setText(String.valueOf(prevEmp.getUserid()));
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
        positionView.setError(null);
        pinView.setError(null);

        String firstName = firstNameView.getText().toString().trim();
        String lastName = lastNameView.getText().toString().trim();
        String email = emailView.getText().toString().trim();
        String phone = phoneView.getText().toString().trim();
        String id = idView.getText().toString().trim();
        String branchName = autoCompleteTextView.getText().toString();
        String position = positionView.getText().toString();
        String pin = pinView.getText().toString().trim();

        boolean isValid = true;
        View focusView = null;

        if ((TextUtils.isEmpty(pin) || pin.length() != 4) && !isUpdate) {
            isValid = false;
            focusView = pinView;
            pinView.setError("Enter 4 digit pin");
        }

        if (TextUtils.isEmpty(branchName)){
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
                if (!prevEmp.getPhone().equals("+254" + phone)) {
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

        if (TextUtils.isEmpty(position)){
            isValid = false;
            focusView = positionView;
            positionView.setError("This field is required");
        }

        if (!InputValidation.emailValidate(email)){
            isValid = false;
            focusView = emailView;
            emailView.setError("Invalid Email Address");
        } else if (userEmailPhoneIds.getEmails().contains(email)) {
            if (isUpdate) {
                if (!prevEmp.getEmail().equals(email)) {
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
                if (prevEmp.getUserid() != Integer.parseInt(id.trim())) {
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

        if (TextUtils.isEmpty(lastName)){
            isValid = false;
            focusView = lastNameView;
            lastNameView.setError("This field is required");
        }

        if (TextUtils.isEmpty(firstName)){
            isValid = false;
            focusView = firstNameView;
            firstNameView.setError("This field is required");
        }

        if (isValid){
            progressCard.setVisibility(View.VISIBLE);
            submit.setEnabled(false);
            if (!isUpdate){
                progressText.setText("Adding Employee...");
                Employee employee = new Employee();
                employee.setUserid(Integer.parseInt(id));
                employee.setEmail(email);
                employee.setPhone("+254" + phone);
                employee.setFirstName(firstName);
                employee.setLastName(lastName);
                employee.setRegistrationDate(new Date());
                employee.setPin(Integer.parseInt(pin));
                employee.setPosition(position);

                Branch branch = null;
                for (Branch b:branches){
                    if (b.getAddress().equals(branchName))
                        branch = b;
                }
                employee.setBranch(branch.getAddress());

                EmployeeController.createEmployee(employee, this, progressCard, submit, user, adminEmployee);
            } else {
                progressText.setText("Updating Employee...");
                Employee employee = new Employee();
                employee.setUserid(Integer.parseInt(id));
                employee.setEmail(email);
                employee.setPhone("+254" + phone);
                employee.setFirstName(firstName);
                employee.setLastName(lastName);
                employee.setEmployeeID(prevEmp.getEmployeeID());

                Branch branch = null;
                for (Branch b:branches){
                    if (b.getAddress().equals(branchName))
                        branch = b;
                }
                employee.setBranch(branch.getAddress());

                EmployeeController.updateEmployee(employee, EmployeeAdd.this, prevEmp, progressCard, submit, user, adminEmployee, "Employee");
            }

        } else {
            focusView.requestFocus();
        }
    }
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent = new Intent(EmployeeAdd.this, AdminPanel.class);
        intent.putExtra("User", user);
        intent.putExtra("Employee", adminEmployee);
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
