package com.example.bms;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.textfield.TextInputEditText;

public class BranchAdd extends AppCompatActivity {
    private TextInputEditText addressView;
    private TextInputEditText phoneView;
    private Button submit;
    private boolean isUpdate = false;
    private Branch prevBranch;
    private ConstraintLayout progressCard;
    private TextView progressText;
    private User user;
    private Employee employee;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_branch);

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        user = (User) getIntent().getSerializableExtra("User");
        employee = (Employee) getIntent().getSerializableExtra("Employee");
        progressCard = findViewById(R.id.progress);
        progressText = findViewById(R.id.progressText);

        addressView = findViewById(R.id.address);
        phoneView = findViewById(R.id.phone);
        submit = findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateInput();
            }
        });

        // get intents
        prevBranch = (Branch) getIntent().getSerializableExtra("Branch");
        if (prevBranch != null) {
            isUpdate = true;
            submit.setText("UPDATE");
            addressView.setText(prevBranch.getAddress());
            phoneView.setText(prevBranch.getPhone());
        }
    }

    private void validateInput() {
        phoneView.setError(null);
        phoneView.setError(null);

        boolean cancel = false;
        View focusView = null;

        String address = addressView.getText().toString();
        String phone = phoneView.getText().toString();

        if (!InputValidation.phoneValidate(phone)){
            cancel = true;
            focusView = phoneView;
            phoneView.setError("Phone number should be 9 characters long");
        }

        if (TextUtils.isEmpty(address)){
            cancel = true;
            focusView = addressView;
            addressView.setError("This field is required");
        }

        if (cancel)
            focusView.requestFocus();
        else {
            progressCard.setVisibility(View.VISIBLE);
            submit.setEnabled(false);
            if (!isUpdate)
                BranchController.createBranch(new Branch(address, "+254" + phone), BranchAdd.this, progressCard, submit, user, employee);
            else
                BranchController.updateBranch(new Branch(address, "+254" + phone), BranchAdd.this, progressCard, submit, user, employee);
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent = new Intent(BranchAdd.this, AdminPanel.class);
        intent.putExtra("User", user);
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
