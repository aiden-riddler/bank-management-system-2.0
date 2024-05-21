package com.example.bms;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class RequestLoan extends AppCompatActivity {
    private TextInputEditText loanAmountView;
    private TextInputEditText annualIncomeView;
    private TextInputEditText employerFirstNameView;
    private TextInputEditText employerLastNameView;
    private TextInputEditText occupationView;
    private TextInputEditText incomeView;
    private TextInputEditText mortgageView;
    private CheckBox termsCheck;
    private RadioGroup radioGroup;
    private Button submit;
    private String loanPurpose = "";
    private ConstraintLayout progressCard;
    private TextView progressText;
    private User user;
    private ArrayList<Account> accounts;
    private ArrayAdapter<String> arrayAdapter;
    private AutoCompleteTextView autoCompleteTextView;
    private Customer customer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_loan);

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
        arrayAdapter = new ArrayAdapter<>(RequestLoan.this, R.layout.list_item, accountsList);
        autoCompleteTextView.setAdapter(arrayAdapter);

        progressCard = findViewById(R.id.progress);
        progressText = findViewById(R.id.progressText);
        loanAmountView = findViewById(R.id.loan_amount);
        annualIncomeView = findViewById(R.id.annual_income);
        employerFirstNameView = findViewById(R.id.employer_firstname);
        employerLastNameView = findViewById(R.id.employer_lastname);
        occupationView = findViewById(R.id.occupation);
        incomeView = findViewById(R.id.income);
        mortgageView = findViewById(R.id.mortgage);
        termsCheck = findViewById(R.id.terms_check);
        radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (radioGroup.getCheckedRadioButtonId()){
                    case R.id.businessLaunching:
                        loanPurpose = "Business Launching";
                        break;
                    case R.id.homeImprovement:
                        loanPurpose = "Home Improvement";
                        break;
                    case R.id.education:
                        loanPurpose = "Education";
                        break;
                    case R.id.houseBuying:
                        loanPurpose = "House Buying";
                        break;
                    case R.id.investment:
                        loanPurpose = "Investment";
                        break;
                    case R.id.other:
                        loanPurpose = "Other";
                        break;
                }
            }
        });
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateInput();
            }
        });
    }

    private void validateInput() {
        loanAmountView.setError(null);
        annualIncomeView.setError(null);
        employerFirstNameView.setError(null);
        employerLastNameView.setError(null);
        occupationView.setError(null);
        incomeView.setError(null);
        mortgageView.setError(null);
        autoCompleteTextView.setError(null);

        String loanAmount = loanAmountView.getText().toString().trim();
        String annualIncome = annualIncomeView.getText().toString().trim();
        String firstName = employerFirstNameView.getText().toString().trim();
        String lastName = employerLastNameView.getText().toString().trim();
        String occupation = occupationView.getText().toString().trim();
        String income = incomeView.getText().toString().trim();
        String mortgage = mortgageView.getText().toString().trim();
        String accountNumber = autoCompleteTextView.getText().toString();

        Boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(accountNumber)) {
            cancel = true;
            focusView = autoCompleteTextView;
            autoCompleteTextView.setError("Please select account");
        }

        if (TextUtils.isEmpty(mortgage)) {
            cancel = true;
            focusView = mortgageView;
            mortgageView.setError("This field is required");
        }

        if (TextUtils.isEmpty(income)) {
            cancel = true;
            focusView = incomeView;
            incomeView.setError("This field is required");
        } else if (Double.parseDouble(income) <= 0){
            cancel = true;
            focusView = incomeView;
            incomeView.setError("Income cannot be zero");
        }

        if (TextUtils.isEmpty(occupation)) {
            cancel = true;
            focusView = occupationView;
            occupationView.setError("This field is required");
        }

        if (TextUtils.isEmpty(lastName)){
            cancel = true;
            focusView = employerLastNameView;
            employerLastNameView.setError("This field is required");
        }

        if (TextUtils.isEmpty(firstName)){
            cancel = true;
            focusView = employerFirstNameView;
            employerFirstNameView.setError("This field is required");
        }

        if (TextUtils.isEmpty(annualIncome)){
            cancel = true;
            focusView = annualIncomeView;
            annualIncomeView.setError("This field is required");
        } else if (Double.parseDouble(annualIncome) <= 0){
            cancel = true;
            focusView = annualIncomeView;
            annualIncomeView.setError("Annual Income cannot be zero");
        }

        if (TextUtils.isEmpty(loanAmount)){
            cancel = true;
            focusView = loanAmountView;
            loanAmountView.setError("This field is required");
        }

        if (cancel)
            focusView.requestFocus();
        else if (loanPurpose == "")
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Loan Purpose")
                    .setMessage("Please select loan purpose.")
                    .setPositiveButton("GOT IT", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    }).show();
        else if (!termsCheck.isChecked())
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Terms")
                    .setMessage("Please accept the terms to complete application.")
                    .setPositiveButton("GOT IT", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    }).show();
        else {
            submit.setEnabled(false);
            progressText.setText("Filling Application...");
            progressCard.setVisibility(View.VISIBLE);
            submit.setEnabled(false);
            LoanApplication loanApplication = new LoanApplication(Double.parseDouble(loanAmount),
                    Double.parseDouble(annualIncome),
                    InputValidation.toTitleCase(firstName) + " " + InputValidation.toTitleCase(lastName),
                    occupation,
                    Double.parseDouble(income), Double.parseDouble(mortgage), loanPurpose);
            loanApplication.setCustomer(user.getId());
            loanApplication.setAccount(accountNumber);
            LoanController.loanApplication(loanApplication, RequestLoan.this, progressCard, submit, user, customer);
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent = new Intent(RequestLoan.this, CustomerPanel.class);
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
