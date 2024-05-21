package com.example.bms;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

import javax.xml.transform.Result;

public class AccountFrag extends Fragment {
    public AccountFrag() {
        // Required empty public constructor
    }

    private ImageView logout;
    private TextView logoutText;
    private FirebaseAuth mAuth;
    private TextView fullnameView;
    private TextView emailTextView;
    private TextView phoneTextView;
    private User user;
    private Customer customer;
    private ImageView emailUs;
    private TextView emailUsTextView;
    private ImageView edit;
    private TextView editText;
    private ConstraintLayout editLayout;
    private CardView changePin;
    private TextInputEditText emailView;
    private TextInputEditText firstNameView;
    private TextInputEditText phoneView;
    private TextInputEditText lastNameView;
    private TextInputEditText idView;
    private TextInputEditText pinView;
    private Button update;
    private DataViewModel dataViewModel;
    private UserEmailPhoneIds userEmailPhoneIds;
    private ConstraintLayout progressCard;
    private TextView progressText;
    private Employee employee;
    private static final int PICK_IMAGE_REQUEST = 1;
    private CardView imageCard;
    private FirebaseStorage storage;
    private StorageReference profileRef;
    private ImageView profile;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        user = (User) getActivity().getIntent().getSerializableExtra("User");
        customer = (Customer) getActivity().getIntent().getSerializableExtra("Customer");
        employee = (Employee) getActivity().getIntent().getSerializableExtra("Employee");
        progressCard = getActivity().findViewById(R.id.progress);
        progressText = getActivity().findViewById(R.id.progressText);

        storage = FirebaseStorage.getInstance();
        profile = view.findViewById(R.id.profile);

        userEmailPhoneIds = new UserEmailPhoneIds();
        dataViewModel = new ViewModelProvider(requireActivity()).get(DataViewModel.class);
        dataViewModel.getUserEmailPhoneIds().observe(getViewLifecycleOwner(), new Observer<UserEmailPhoneIds>() {
            @Override
            public void onChanged(UserEmailPhoneIds userEmailPhoneIds) {
                userEmailPhoneIds.addPhoneNumbers(userEmailPhoneIds.getPhoneNumbers());
                userEmailPhoneIds.addEmails(userEmailPhoneIds.getEmails());
                userEmailPhoneIds.addUserIds(userEmailPhoneIds.getUserIds());
            }
        });

        dataViewModel.getProfile().observe(getViewLifecycleOwner(), new Observer<Uri>() {
            @Override
            public void onChanged(Uri uri) {
                Glide.with(getContext())
                        .load(uri)
                        .into(profile);
            }
        });

        fullnameView = view.findViewById(R.id.fullname);
        emailTextView = view.findViewById(R.id.email);
        emailTextView.setText(user.getEmail());
        phoneTextView = view.findViewById(R.id.phone);
        phoneTextView.setText(user.getPhone());
        logout = view.findViewById(R.id.logout);
        logoutText = view.findViewById(R.id.logoutText);
        emailUs = view.findViewById(R.id.mail);
        emailUsTextView = view.findViewById(R.id.mailText);
        edit = view.findViewById(R.id.edit);
        editText = view.findViewById(R.id.editText);
        editLayout = view.findViewById(R.id.editLayout);
        changePin = view.findViewById(R.id.changePin);
        imageCard = view.findViewById(R.id.imageCard);
        imageCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle("Change profile picture")
                        .setMessage("Do you want to select an image from gallery?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                            }
                        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        }).show();

            }
        });

        emailView = view.findViewById(R.id.emailTextInput);
        phoneView = view.findViewById(R.id.phoneTextInput);
        firstNameView = view.findViewById(R.id.firstname);
        lastNameView = view.findViewById(R.id.lastname);
        idView = view.findViewById(R.id.id_num);
        pinView = view.findViewById(R.id.pin);
        update = view.findViewById(R.id.update);

        if (customer != null) {
            fullnameView.setText(InputValidation.toTitleCase(customer.getFirstName()) + " " + InputValidation.toTitleCase(customer.getLastName()));
            emailView.setText(customer.getEmail());
            firstNameView.setText(customer.getFirstName());
            lastNameView.setText(customer.getLastName());
            phoneView.setText(customer.getPhone().substring(4));
            idView.setText(String.valueOf(customer.getUserid()));
        } else if (employee != null) {
            fullnameView.setText(InputValidation.toTitleCase(employee.getFirstName()) + " " + InputValidation.toTitleCase(employee.getLastName()));
            emailView.setText(employee.getEmail());
            firstNameView.setText(employee.getFirstName());
            lastNameView.setText(employee.getLastName());
            phoneView.setText(employee.getPhone().substring(4));
            idView.setText(String.valueOf(employee.getUserid()));
        }
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateInput();
            }
        });
        changePin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ChangePin.class);
                intent.putExtra("User", user);
                if (user.getRole().equals("Customer"))
                    intent.putExtra("Customer", customer);
                else
                    intent.putExtra("Employee", employee);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

        logoutText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

        emailUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMail();
            }
        });

        emailUsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMail();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editLayout.setVisibility(View.VISIBLE);
            }
        });

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void sendMail() {
        String[] TO = {"seletemoses@gmail.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Bank Inquiry");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            Log.i("Finished sending email...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getContext(), "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }
    private void validateInput() {
        firstNameView.setError(null);
        lastNameView.setError(null);
        emailView.setError(null);
        phoneView.setError(null);
        idView.setError(null);
        pinView.setError(null);

        String firstName = firstNameView.getText().toString().trim();
        String lastName = lastNameView.getText().toString().trim();
        String email = emailView.getText().toString().trim();
        String phone = phoneView.getText().toString().trim();
        String id = idView.getText().toString().trim();
        String pin = pinView.getText().toString().trim();

        boolean isValid = true;
        View focusView = null;

        if ((TextUtils.isEmpty(pin) || pin.length() != 4)) {
            isValid = false;
            focusView = pinView;
            pinView.setError("Enter 4 digit pin");
        }

        if (!InputValidation.phoneValidate(phone)){
            isValid = false;
            focusView = phoneView;
            phoneView.setError("Phone number should be 9 characters long");
        } else if (userEmailPhoneIds.getPhoneNumbers().contains("+254" + phone.trim()) && !customer.getPhone().equals("+254" + phone.trim())) {
            isValid = false;
            focusView = phoneView;
            phoneView.setError("User with phone-number already exists!");
        }

        if (!InputValidation.emailValidate(email)) {
            isValid = false;
            focusView = emailView;
            emailView.setError("Invalid Email Address");
        } else if (userEmailPhoneIds.getEmails().contains(email) && !customer.getEmail().equals(email)) {
            isValid = false;
            focusView = emailView;
            emailView.setError("User with Email Address already exists");
        }

        if (TextUtils.isEmpty(id)){
            isValid = false;
            focusView = idView;
            idView.setError("This field is required");
        } else if (userEmailPhoneIds.getUserIds().contains(id.trim()) && !String.valueOf(customer.getUserid()).equals(id)) {
            isValid = false;
            focusView = idView;
            idView.setError("User with ID already exists");
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
            update.setEnabled(false);
            progressText.setText("Updating Info...");

            if (user.getRole().equals("Customer")){
                Customer customer1 = new Customer();
                customer1.setUserid(Integer.parseInt(id));
                customer1.setEmail(email);
                customer1.setPhone("+254" + phone);
                customer1.setFirstName(firstName);
                customer1.setLastName(lastName);
                customer1.setCustomerId(customer.getCustomerId());
                CustomerController.updateCustomer(customer1, getContext(), customer, progressCard, update, user, new Employee());
            } else {
                Employee employee1 = new Employee();
                employee1.setUserid(Integer.parseInt(id));
                employee1.setEmail(email);
                employee1.setPhone("+254" + phone);
                employee1.setFirstName(firstName);
                employee1.setLastName(lastName);
                employee1.setEmployeeID(employee.getEmployeeID());
                EmployeeController.updateEmployee(employee1, getContext(), employee, progressCard, update, user, employee1, "Account");
            }



        } else {
            focusView.requestFocus();
        }
    }

    private void signOut() {
        mAuth.signOut();
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == PICK_IMAGE_REQUEST){
                Uri uri = data.getData();
                Glide.with(getContext())
                        .load(uri)
                        .into(profile);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                    byte[] dataByte = baos.toByteArray();
                    UploadTask uploadTask = storage.getReference("ProfileImages/" + user.getId() + ".jpg").putBytes(dataByte);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Profile updated failed", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}