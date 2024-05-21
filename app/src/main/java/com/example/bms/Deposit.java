package com.example.bms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class Deposit extends AppCompatActivity {

    private Employee employee;
    private ArrayList<Account> accounts;
    private Customer customer;
    private User user;
    private ArrayAdapter<String> arrayAdapter;
    private AutoCompleteTextView autoCompleteTextView;
    private TextInputEditText phoneView;
    private TextInputEditText amountView;
    private TextInputEditText pinView;
    private Button deposit;
    private ConstraintLayout progressCard;
    private TextView progressText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit);

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        user = (User) getIntent().getSerializableExtra("User");
        employee = (Employee) getIntent().getSerializableExtra("Employee");
        customer = (Customer) getIntent().getSerializableExtra("Customer");
        accounts = (ArrayList<Account>) getIntent().getSerializableExtra("Accounts");
        //add accounts to string
        String[] accountsList = new String[accounts.size()];
        for (int i=0; i<accounts.size(); i++)
            accountsList[i] = accounts.get(i).getAccountNumber();

        autoCompleteTextView = findViewById(R.id.autoComplete);
        arrayAdapter = new ArrayAdapter<>(Deposit.this, R.layout.list_item, accountsList);
        autoCompleteTextView.setAdapter(arrayAdapter);

        progressCard = findViewById(R.id.progress);
        progressText = findViewById(R.id.progressText);

        phoneView = findViewById(R.id.phone);
        phoneView.setText(customer.getPhone().substring(4));
        phoneView.setEnabled(false);
        amountView = findViewById(R.id.amount);
        pinView = findViewById(R.id.pin);
        deposit = findViewById(R.id.dep);
        deposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateInput();
            }
        });

    }

    private void validateInput() {
        amountView.setError(null);
        pinView.setError(null);
        phoneView.setError(null);
        autoCompleteTextView.setError(null);

        String amount = amountView.getText().toString();
        String pin = pinView.getText().toString();
        String phone = phoneView.getText().toString();
        String accountNumber = autoCompleteTextView.getText().toString();

        Boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(pin)) {
            cancel = true;
            focusView = pinView;
            pinView.setError("Please enter pin");
        } else if (user.getPin() != Integer.valueOf(pin)){
            cancel = true;
            focusView = pinView;
            pinView.setError("Incorrect pin");
        }

        if (TextUtils.isEmpty(accountNumber)) {
            cancel = true;
            focusView = autoCompleteTextView;
            autoCompleteTextView.setError("Please select account to deposit");
        } else if (TextUtils.isEmpty(amount)) {
            cancel = true;
            focusView = amountView;
            amountView.setError("Please enter amount to deposit");
        }

        if (cancel)
            focusView.requestFocus();
        else {
            deposit.setEnabled(false);
            progressText.setText("Depositing...");
            progressCard.setVisibility(View.VISIBLE);
            double amountToDeposit = Double.parseDouble(amount);
            FundsTransferController.depositCustomer(accountNumber, amountToDeposit, Deposit.this, progressCard, deposit, user, employee);
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent = null;
        if (user.getRole().equals("Teller"))
            intent = new Intent(Deposit.this, EmployeePanel.class);
        else
            intent = new Intent(Deposit.this, AdminPanel.class);
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