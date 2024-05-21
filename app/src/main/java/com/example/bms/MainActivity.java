package com.example.bms;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextInputEditText phoneView;
    private TextInputEditText otpCodeView;
    private TextView counter;
    private TextView progressText;
    private Button sendOtp;
    private String mVerificationID;
    private Button verifyOtp;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private Timer timer;
    private int secs = 60;
    private ConstraintLayout progressCard;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_verification);

        progressCard = findViewById(R.id.progress);
        progressText = findViewById(R.id.progressText);
        progressCard.setVisibility(View.VISIBLE);

        FirebaseApp.initializeApp(this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance());

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        timer = new Timer();
        phoneView = findViewById(R.id.phone);
        otpCodeView = findViewById(R.id.otp_code);
        counter = findViewById(R.id.timer);
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

        if (mAuth.getCurrentUser() != null){
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

                            if (queryDocumentSnapshots.size() > 0) {
                                Intent intent = new Intent(MainActivity.this, Login.class);
                                intent.putExtra("User", user);
                                startActivity(intent);
                            } else {
                                mAuth.signOut();
                                progressCard.setVisibility(View.INVISIBLE);
                                phoneView.setEnabled(true);
                                sendOtp.setEnabled(true);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mAuth.signOut();
                            progressCard.setVisibility(View.INVISIBLE);
                        }
                    });
        } else {
            progressCard.setVisibility(View.INVISIBLE);
            phoneView.setEnabled(true);
            sendOtp.setEnabled(true);
        }
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
                                                    Intent intent = new Intent(MainActivity.this, Login.class);
                                                    intent.putExtra("User", user);
                                                    startActivity(intent);
                                                } else {
                                                    Toast.makeText(MainActivity.this, "User does not exist. Contact admin.", Toast.LENGTH_SHORT).show();
                                                    verifyOtp.setEnabled(true);
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(MainActivity.this, "An error occurred.", Toast.LENGTH_SHORT).show();
                                                mAuth.signOut();
                                                sendOtp.setEnabled(true);
                                                otpCodeView.setEnabled(false);
                                            }
                                        });
                            } else {
                                progressCard.setVisibility(View.INVISIBLE);
                                Toast.makeText(MainActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
//                                sendOtp.setEnabled(true);
                                verifyOtp.setEnabled(true);
                            }
                        }
                    });
        }
    }

    private void validateInput() {
        phoneView.setError(null);
        boolean cancel = false;

        String phone = phoneView.getText().toString();

        if (!InputValidation.phoneValidate(phone)){
            cancel = true;
            phoneView.setError("Phone number should be 9 characters long");
        }

        if (cancel){
            phoneView.requestFocus();
        } else {
            progressCard.setVisibility(View.VISIBLE);
            progressText.setText("Sending Otp...");
            otpSend(phone);
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
                Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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
                        .setPhoneNumber("+254" + phone.trim())
                        .setTimeout(60L, TimeUnit.SECONDS)
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
}
