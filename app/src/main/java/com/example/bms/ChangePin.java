package com.example.bms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class ChangePin extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextInputEditText phoneView;
    private TextInputEditText otpCodeView;
    private TextInputEditText pinView;
    private TextInputEditText confirmPinView;
    private TextView counter;
    private TextView progressText;
    private Button sendOtp;
    private String mVerificationID;
    private Button verifyOtp;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private Timer timer;
    private int secs = 120;
    private ConstraintLayout progressCard;
    private User user;
    private String pin;
    private Customer customer;
    private Employee employee;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_pin);

        user = (User) getIntent().getSerializableExtra("User");
        customer = (Customer) getIntent().getSerializableExtra("Customer");
        employee = (Employee) getIntent().getSerializableExtra("Employee");

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();
        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        progressCard = findViewById(R.id.progress);
        progressText = findViewById(R.id.progressText);

//        FirebaseApp.initializeApp(this);
//        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
//        firebaseAppCheck.installAppCheckProviderFactory(
//                PlayIntegrityAppCheckProviderFactory.getInstance());

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        timer = new Timer();
        phoneView = findViewById(R.id.phone);
        phoneView.setText(user.getPhone().substring(4));
        otpCodeView = findViewById(R.id.otp_code);
        counter = findViewById(R.id.timer);
        pinView = findViewById(R.id.pin);
        confirmPinView = findViewById(R.id.confirm_pin);
        sendOtp = findViewById(R.id.send_otp);
        sendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateInput();
                counter.setVisibility(View.INVISIBLE);
                counter.setTextColor(Color.GRAY);
            }
        });

        verifyOtp = findViewById(R.id.verify_otp);
        verifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verify();
            }
        });
    }

    private void verify() {
        otpCodeView.setError(null);

        String otp = otpCodeView.getText().toString();

        if (TextUtils.isEmpty(otp)){
            otpCodeView.setError("Please enter OTP you received");
            otpCodeView.requestFocus();
        } else {
            sendOtp.setEnabled(false);
            verifyOtp.setEnabled(false);
            progressCard.setVisibility(View.VISIBLE);
            progressText.setText("Verifying Otp...");

            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationID, otp);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                String phone = mAuth.getCurrentUser().getPhoneNumber();
                                Log.d("BMS", "Phone number: " + phone);
                                db.collection("Users")
                                        .whereEqualTo("phone", mAuth.getCurrentUser().getPhoneNumber())
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                User user = null;
                                                for (QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots) {
                                                    user = documentSnapshot.toObject(User.class);
                                                }
                                                progressCard.setVisibility(View.INVISIBLE);

                                                if (queryDocumentSnapshots.size() > 0) {
                                                    timer.cancel();
                                                    User finalUser = user;
                                                    db.collection("Users").document(user.getId()).update("pin", Integer.valueOf(pin)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                Toast.makeText(ChangePin.this, "Pin updated", Toast.LENGTH_SHORT).show();
                                                                if (finalUser.getRole().equals("Customer")) {
                                                                    Intent intent = new Intent(ChangePin.this, CustomerPanel.class);
                                                                    intent.putExtra("User", finalUser);
                                                                    intent.putExtra("Customer", customer);
                                                                    intent.putExtra("OpenFragment", "Account");
                                                                    startActivity(intent);
                                                                } else if (finalUser.getRole().equals("Teller")){
                                                                    Intent intent = new Intent(ChangePin.this, EmployeePanel.class);
                                                                    intent.putExtra("User", finalUser);
                                                                    intent.putExtra("Employee", employee);
                                                                    intent.putExtra("OpenFragment", "Account");
                                                                    startActivity(intent);
                                                                } else if (finalUser.getRole().equals("Manager")){
                                                                    Intent intent = new Intent(ChangePin.this, AdminPanel.class);
                                                                    intent.putExtra("User", finalUser);
                                                                    intent.putExtra("Employee", employee);
                                                                    intent.putExtra("OpenFragment", "Account");
                                                                    startActivity(intent);
                                                                }
                                                            } else {
                                                                Toast.makeText(ChangePin.this, "An error occurred. Contact admin.", Toast.LENGTH_SHORT).show();
                                                                verifyOtp.setEnabled(true);
                                                            }
                                                        }
                                                    });

                                                } else {
                                                    Toast.makeText(ChangePin.this, "An error occurred. Contact admin.", Toast.LENGTH_SHORT).show();
                                                    verifyOtp.setEnabled(true);
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(ChangePin.this, "An error occurred.", Toast.LENGTH_SHORT).show();
                                                mAuth.signOut();
                                                sendOtp.setEnabled(true);
                                                otpCodeView.setEnabled(false);
                                            }
                                        });
                            } else {
                                progressCard.setVisibility(View.INVISIBLE);
                                Toast.makeText(ChangePin.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
//                                sendOtp.setEnabled(true);
                                verifyOtp.setEnabled(true);
                            }
                        }
                    });
        }
    }

    private void validateInput() {
        pinView.setError(null);
        confirmPinView.setError(null);

        boolean cancel = false;
        View focusView = null;

        pin = pinView.getText().toString();
        String confirmPin = confirmPinView.getText().toString();
        if (TextUtils.isEmpty(pin) || pin.length() != 4){
            cancel = true;
            pinView.setError("Enter 4 digit pin");
            focusView = pinView;
        } else if (Integer.valueOf(confirmPin) != Integer.parseInt(pin)){
            cancel = true;
            confirmPinView.setError("Pins do not match");
            focusView = confirmPinView;
        }

        if (cancel){
            focusView.requestFocus();
        } else {
            progressCard.setVisibility(View.VISIBLE);
            progressText.setText("Sending Otp...");
            otpSend(user.getPhone());
        }
    }

    private void otpSend(String phone) {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                progressCard.setVisibility(View.INVISIBLE);
                Toast.makeText(ChangePin.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("BMS", "onCodeSent:" + verificationId);
                progressCard.setVisibility(View.INVISIBLE);

                sendOtp.setEnabled(false);
                verifyOtp.setEnabled(true);
                otpCodeView.setEnabled(true);
                mVerificationID = verificationId;
                counter.setVisibility(View.VISIBLE);

                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        if (secs == 0){
                            timer.cancel();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    sendOtp.setEnabled(true);
                                    counter.setVisibility(View.INVISIBLE);
                                    verifyOtp.setEnabled(false);
                                    otpCodeView.setText("");
                                    otpCodeView.setEnabled(false);
                                }
                            });
                        } else {
                            if (secs == 20){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        counter.setTextColor(Color.RED);
                                    }
                                });
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    counter.setText(secToTime(secs));
                                }
                            });
                            secs--;
                        }
                    }
                }, 1000, 1000);

            }
        };

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phone)
                        .setTimeout(120L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public String secToTime(int seconds) {
        String min = String.valueOf(Math.round(Math.floor(seconds/60)));
        String sec = String.valueOf(seconds % 60);

        if (min.length() == 1)
            min = "0" + min;

        if (sec.length() == 1)
            sec = "0" + sec;

        return min + ":" + sec;
    }

    @Override
    public void onBackPressed() {
        Intent intent = null;
        if (user.getRole().equals("Teller")) {
            intent = new Intent(ChangePin.this, EmployeePanel.class);
            intent.putExtra("Employee", employee);
        } else if (user.getRole().equals("Customer")) {
            intent = new Intent(ChangePin.this, CustomerPanel.class);
            intent.putExtra("Customer", customer);
        } else if (user.getRole().equals("Manager")) {
            intent = new Intent(ChangePin.this, AdminPanel.class);
            intent.putExtra("Employee", employee);
        }
        intent.putExtra("User", user);
        intent.putExtra("OpenFragment", "Account");
        startActivity(intent);
//        super.onBackPressed();
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