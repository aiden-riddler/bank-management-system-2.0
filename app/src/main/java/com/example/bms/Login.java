package com.example.bms;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Login extends AppCompatActivity {
    private TextView phone;
    private FirebaseAuth mAuth;
    private User user;
    private Button login;
    private TextInputEditText pinView;
    private String phoneNumber;
    private ConstraintLayout progressCard;
    private TextView progressText;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = (User) getIntent().getSerializableExtra("User");
        phoneNumber = user.getPhone();

        phone = findViewById(R.id.phone);
        phone.setText(phoneNumber);
        login = findViewById(R.id.login);
        pinView = findViewById(R.id.pin);
        progressCard = findViewById(R.id.progress);
        progressText = findViewById(R.id.progressText);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pinView.setError(null);
                String pin = pinView.getText().toString();
                if (TextUtils.isEmpty(pin) || pin.length() != 4){
                    pinView.setError("Enter 4 digit pin");
                    pinView.requestFocus();
                } else {
                    if (Integer.parseInt(pin) == user.getPin()){
                        progressText.setText("Logging in...");
                        progressCard.setVisibility(View.VISIBLE);
                        login.setEnabled(false);
                        Intent intent = null;

                        if (user.getRole().equals("Teller")) {
                            intent = new Intent(Login.this, EmployeePanel.class);
                            intent.putExtra("User", user);
                            Intent finalIntent1 = intent;
                            db.collection("Employees")
                                    .document(user.getId())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()){

                                                Employee employee = task.getResult().toObject(Employee.class);
                                                employee.setEmployeeID(task.getResult().getId());
                                                finalIntent1.putExtra("Employee", employee);
                                                startActivity(finalIntent1);
                                            } else {
                                                Toast.makeText(Login.this, "An error occurred. Try again later", Toast.LENGTH_SHORT).show();
                                                progressCard.setVisibility(View.INVISIBLE);
                                                login.setEnabled(true);
                                            }
                                        }
                                    });
                            startActivity(intent);
                        } else if (user.getRole().equals("Manager")) {
                            intent = new Intent(Login.this, AdminPanel.class);
                            intent.putExtra("User", user);
                            Intent finalIntent1 = intent;
                            db.collection("Employees")
                                    .document(user.getId())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()){
                                                Employee employee = task.getResult().toObject(Employee.class);
                                                employee.setEmployeeID(task.getResult().getId());
                                                finalIntent1.putExtra("Employee", employee);
                                                startActivity(finalIntent1);
                                            } else {
                                                Toast.makeText(Login.this, "An error occurred. Try again later", Toast.LENGTH_SHORT).show();
                                                progressCard.setVisibility(View.INVISIBLE);
                                                login.setEnabled(true);
                                            }
                                        }
                                    });
                        } else {
                            intent = new Intent(Login.this, CustomerPanel.class);
                            intent.putExtra("User", user);
                            Intent finalIntent = intent;
                            db.collection("Customers")
                                    .document(user.getId())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()){

                                                Customer customer = task.getResult().toObject(Customer.class);
                                                customer.setCustomerId(task.getResult().getId());
                                                finalIntent.putExtra("Customer", customer);
                                                startActivity(finalIntent);
                                            } else {
                                                Toast.makeText(Login.this, "An error occurred. Try again later", Toast.LENGTH_SHORT).show();
                                                progressCard.setVisibility(View.INVISIBLE);
                                                login.setEnabled(true);
                                            }
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(Login.this, "Incorrect Pin", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
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
//            Login.this.finish();
//            System.exit(0);
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
