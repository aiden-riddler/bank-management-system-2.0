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

public class RepayLoan extends AppCompatActivity {
    private AutoCompleteTextView autoCompleteTextView;
    private User user;
    private ArrayList<Account> accounts;
    private ArrayAdapter<String> arrayAdapter;
    private Loan loan;
    private TextView loanNumber;
    private TextView loanAmount;
    private TextView amountDue;
    private TextInputEditText amountView;
    private TextInputEditText pinView;
    private Button payAmount;
    private ConstraintLayout progressCard;
    private TextView progressText;
    private Customer customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.repay_loan);

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        user = (User) getIntent().getSerializableExtra("User");
        loan = (Loan) getIntent().getSerializableExtra("Loan");
        customer = (Customer) getIntent().getSerializableExtra("Customer");
        accounts = (ArrayList<Account>) getIntent().getSerializableExtra("Accounts");
        //add accounts to string
        String[] accountsList = new String[accounts.size()];
        for (int i=0; i<accounts.size(); i++)
            accountsList[i] = accounts.get(i).getAccountNumber();

        autoCompleteTextView = findViewById(R.id.autoComplete);
        arrayAdapter = new ArrayAdapter<>(RepayLoan.this, R.layout.list_item, accountsList);
        autoCompleteTextView.setAdapter(arrayAdapter);

        loanNumber = findViewById(R.id.loanNumber);
        loanAmount = findViewById(R.id.loanAmount);
        amountDue = findViewById(R.id.amountDue);

        loanNumber.setText(loan.getId());
        loanAmount.setText("Ksh. " + loan.getAmount());
        amountDue.setText("Ksh. " + loan.getAmountDue());

        amountView = findViewById(R.id.amount);
        pinView = findViewById(R.id.pin);
        payAmount = findViewById(R.id.payAmount);
        progressCard = findViewById(R.id.progress);
        progressText = findViewById(R.id.progressText);

        payAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateInput();
            }
        });

    }

    private void validateInput() {
        amountView.setError(null);
        pinView.setError(null);
        autoCompleteTextView.setError(null);

        String amount = amountView.getText().toString();
        String pin = pinView.getText().toString();
        String accountNumber = autoCompleteTextView.getText().toString();

        Boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(accountNumber)) {
            cancel = true;
            focusView = autoCompleteTextView;
            autoCompleteTextView.setError("Please select account");
        } else if (TextUtils.isEmpty(amount)) {
            cancel = true;
            focusView = amountView;
            amountView.setError("Please enter amount to pay");
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

        if (TextUtils.isEmpty(pin)) {
            cancel = true;
            focusView = pinView;
            pinView.setError("Please enter pin");
        } else if (user.getPin() != Integer.valueOf(pin)){
            cancel = true;
            focusView = pinView;
            pinView.setError("Incorrect pin");
        }

        if (cancel)
            focusView.requestFocus();
        else {
            payAmount.setEnabled(false);
            progressText.setText("Making payment...");
            progressCard.setVisibility(View.VISIBLE);
            double amountToBePaid = Double.parseDouble(amount);
            if (amountToBePaid > loan.getAmountDue())
                amountToBePaid = loan.getAmountDue();
            LoanController.repayLoan(loan, accountNumber, amountToBePaid, RepayLoan.this, progressCard, payAmount, user, customer);
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent = new Intent(RepayLoan.this, CustomerPanel.class);
        intent.putExtra("User", user);
        intent.putExtra("Customer", customer);
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