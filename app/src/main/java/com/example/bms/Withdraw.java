package com.example.bms;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class Withdraw extends AppCompatActivity {
    private AutoCompleteTextView autoCompleteTextView;
    private TextInputEditText phoneView;
    private TextInputEditText amountView;
    private TextInputEditText pinView;
    private Button withdraw;
    private User user;
    private ArrayList<Account> accounts;
    private ArrayAdapter<String> arrayAdapter;
    private ConstraintLayout progressCard;
    private TextView progressText;
    private View accountDialog;
    private Customer customer;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.withdraw);

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        user = (User) getIntent().getSerializableExtra("User");
        customer = (Customer) getIntent().getSerializableExtra("Customer");
        accounts = (ArrayList<Account>) getIntent().getSerializableExtra("Accounts");
        //add accounts to string
        String[] accountsList = new String[accounts.size()];
        for (int i=0; i<accounts.size(); i++)
            accountsList[i] = accounts.get(i).getAccountNumber();

        autoCompleteTextView = findViewById(R.id.autoComplete);
        arrayAdapter = new ArrayAdapter<>(Withdraw.this, R.layout.list_item, accountsList);
        autoCompleteTextView.setAdapter(arrayAdapter);

        progressCard = findViewById(R.id.progress);
        progressText = findViewById(R.id.progressText);

        phoneView = findViewById(R.id.phone);
        amountView = findViewById(R.id.amount);
        pinView = findViewById(R.id.pin);
        withdraw = findViewById(R.id.withdrawBtn);
        withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateInput();
            }
        });

        accountDialog = getLayoutInflater().inflate(R.layout.account_dialog, null);
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
            autoCompleteTextView.setError("Please select account");
        } else if (TextUtils.isEmpty(amount)) {
            cancel = true;
            focusView = amountView;
            amountView.setError("Please enter amount to withdraw");
        } else {
            for (Account account: accounts){
                if (account.getAccountNumber().equals(accountNumber) && account.getCurrentBalance() < Double.parseDouble(amount)){
                    cancel = true;
                    focusView = amountView;
                    amountView.setError("Amount in selected account Ksh." + account.getCurrentBalance() + " is less than the specified amount.");
                    break;
                }
            }
        }

        if (!InputValidation.phoneValidate(phone)){
            cancel = true;
            focusView = phoneView;
            phoneView.setError("Phone number should be 9 characters long");
        }

        if (cancel)
            focusView.requestFocus();
        else {
            withdraw.setEnabled(false);
            progressText.setText("Validating info...");
            progressCard.setVisibility(View.VISIBLE);
            double amountToWithDraw = Double.parseDouble(amount);
            FundsTransferController.withdrawCustomer("+254" + phone,accountNumber, amountToWithDraw, Withdraw.this, progressCard, withdraw, user, customer, accounts);
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent = new Intent(Withdraw.this, CustomerPanel.class);
        intent.putExtra("User", user);
        intent.putExtra("Customer", customer);
        startActivity(intent);
    }
}
