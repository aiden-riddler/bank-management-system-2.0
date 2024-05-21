package com.example.bms;

import static android.content.Context.STORAGE_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CustomerFrag extends Fragment {
    public CustomerFrag() {
        // Required empty public constructor
    }

    private FloatingActionButton addCustomer;
    private FloatingActionButton printPdf;
    private CustomerViewModel customerViewModel;
    private AccountViewModel accountViewModel;
    private DataViewModel dataViewModel;
    private RecyclerView customerRecycler;
    private CustomerAdapter customerAdapter;
    private UserEmailPhoneIds emailPhoneIds;
    private BranchViewModel branchViewModel;
    private User user;
    private Employee employee;
    int pageHeight = 1120;
    int pagewidth = 792;
    File pdfFile;
    private TextInputEditText searchBar;
    private ImageView searchBack;
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = (User) getActivity().getIntent().getSerializableExtra("User");
        employee = (Employee) getActivity().getIntent().getSerializableExtra("Employee");

        customerRecycler = view.findViewById(R.id.customerRecycler);
        customerRecycler.setHasFixedSize(true);
        customerRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        // get data
        customerAdapter = new CustomerAdapter(new ArrayList<>(), new ArrayList<>(), getContext());
        customerAdapter.setUser(user);
        customerAdapter.setEmployee(employee);
        emailPhoneIds = new UserEmailPhoneIds();
        dataViewModel = new ViewModelProvider(requireActivity()).get(DataViewModel.class);
        dataViewModel.getUserEmailPhoneIds().observe(getViewLifecycleOwner(), new Observer<UserEmailPhoneIds>() {
            @Override
            public void onChanged(UserEmailPhoneIds userEmailPhoneIds) {
                emailPhoneIds.addPhoneNumbers(userEmailPhoneIds.getPhoneNumbers());
                emailPhoneIds.addEmails(userEmailPhoneIds.getEmails());
                emailPhoneIds.addUserIds(userEmailPhoneIds.getUserIds());
                customerAdapter.setUserEmailPhoneIds(emailPhoneIds);
            }
        });
        customerViewModel = new ViewModelProvider(requireActivity()).get(CustomerViewModel.class);
        customerViewModel.getCustomers().observe(getViewLifecycleOwner(), new Observer<ArrayList<Customer>>() {
            @Override
            public void onChanged(ArrayList<Customer> customers) {
                customerAdapter.setCustomers(customers);
            }
        });

        accountViewModel = new ViewModelProvider(requireActivity()).get(AccountViewModel.class);
        accountViewModel.getAccounts().observe(getViewLifecycleOwner(), new Observer<ArrayList<Account>>() {
            @Override
            public void onChanged(ArrayList<Account> accounts) {
                customerAdapter.setAccounts(accounts);
            }
        });

        branchViewModel = new ViewModelProvider(requireActivity()).get(BranchViewModel.class);
        branchViewModel.getBranches().observe(getViewLifecycleOwner(), new Observer<ArrayList<Branch>>() {
            @Override
            public void onChanged(ArrayList<Branch> branches) {
                customerAdapter.setBranches(branches);
            }
        });

        customerRecycler.setAdapter(customerAdapter);
        addCustomer = view.findViewById(R.id.add_customer);
        addCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CustomerAdd.class);
                intent.putExtra("UserEmailIDs", emailPhoneIds);
                intent.putExtra("User", user);
                intent.putExtra("Employee", employee);
                intent.putExtra("CurrentAdmin", "Teller");
                startActivity(intent);
            }
        });

        StorageManager storageManager = (StorageManager) getActivity().getSystemService(STORAGE_SERVICE);
        StorageVolume storageVolume = storageManager.getStorageVolumes().get(0);

        pdfFile = new File(storageVolume.getDirectory().getPath() + "/Download/CustomerData_" + new Date().getTime() + ".pdf");
        printPdf = view.findViewById(R.id.print_pdf);
        printPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generatePDF();
            }
        });

        searchBar = view.findViewById(R.id.searchBar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String query = charSequence.toString();
                if (query.length() > 0) {
                    customerAdapter.search(query);
                    searchBack.setVisibility(View.VISIBLE);
                } else {
//                    searchBar.setText("");
                    customerAdapter.revert();
                    searchBack.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        searchBack = view.findViewById(R.id.searchBack);
        searchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchBar.setText("");
                customerAdapter.revert();
                searchBack.setVisibility(View.INVISIBLE);
            }
        });
    }


    public void generatePDF(){
        PdfDocument pdfDocument = new PdfDocument();

        Paint title = new Paint();
        Paint text = new Paint();
        Paint name = new Paint();
        Paint name2 = new Paint();
        PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(pagewidth, pageHeight, 1).create();


        PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);
        ArrayList<Customer> customers = customerAdapter.getCustomers();


//        customerRecycler.draw(myPage.getCanvas());
        Canvas canvas = myPage.getCanvas();
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        title.setTextSize(15);
        title.setColor(ContextCompat.getColor(getContext(), R.color.black));

        text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        text.setTextSize(15);
        text.setColor(ContextCompat.getColor(getContext(), R.color.light_green));

        name.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        name.setTextSize(16);
        name.setColor(ContextCompat.getColor(getContext(), R.color.red));

        name2.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        name2.setTextSize(14);
        name2.setColor(ContextCompat.getColor(getContext(), R.color.red));


        int startY = 80;
        int x = 1;
        int pageNumber = 2;
        ArrayList<Integer> closedPages = new ArrayList<>();
        for (Customer customer:customers){
            if ((startY+150) > pageHeight){
                pdfDocument.finishPage(myPage);
                closedPages.add(pageNumber);
                myPage = pdfDocument.startPage(new PdfDocument.PageInfo.Builder(pagewidth, pageHeight, pageNumber).create());
                canvas = myPage.getCanvas();
                pageNumber++;
                startY = 80;

            }
            String firstname = InputValidation.toTitleCase(customer.getFirstName());
            String lastname = InputValidation.toTitleCase(customer.getLastName());

            canvas.drawText( x + ". " + firstname + " " + lastname, 50, startY, name);
            startY += 20;
            canvas.drawText("Customer ID:", 50, startY, title);
            canvas.drawText(customer.getCustomerId(), 150, startY, text);
            canvas.drawText("Email:", 360, startY, title);
            canvas.drawText(customer.getEmail(), 410, startY, text);

            startY += 20;
            canvas.drawText("Phone:", 50, startY, title);
            canvas.drawText("+254" + customer.getPhone(), 110, startY, text);

            startY += 30;
            canvas.drawText("Accounts", 50, startY, name2);
            for (Account account:customerAdapter.getAccounts()) {
                if (account.getCustomer().equals(customer.getCustomerId())) {
                    startY += 20;
                    canvas.drawText("Account Number:", 50, startY, title);
                    canvas.drawText(account.getAccountNumber(), 170, startY, text);
                    canvas.drawText("Branch:", 380, startY, title);
                    canvas.drawText(account.getBranch(), 440, startY, text);
                    startY += 20;
                    canvas.drawText("Date Opened:", 50, startY, title);
                    SimpleDateFormat DateFor = new SimpleDateFormat("dd MMMM yyyy");
                    String date = DateFor.format(account.getOpeningDate());
                    canvas.drawText(date, 170, startY, text);
                    canvas.drawText("Balance:", 300, startY, title);
                    canvas.drawText(String.valueOf(account.getCurrentBalance()), 450, startY, text);
                    canvas.drawText("Interest:", 500, startY, title);
                    canvas.drawText(String.valueOf(account.getInterestRate()), 580, startY, text);
                    startY += 20;
                }
            }
            startY += 20;
            x++;
        }

//        System.out.println("START Y " + startY);
//        Toast.makeText(getContext(), "starty" + startY, Toast.LENGTH_SHORT).show();

        if (!closedPages.contains(pageNumber))
            pdfDocument.finishPage(myPage);

//        File file = new File(Environment.getExternalStorageDirectory(), "Customers.pdf");

        try {
            // after creating a file name we will
            // write our PDF file to that location.
            pdfDocument.writeTo(new FileOutputStream(pdfFile));

            // below line is to print toast message
            // on completion of PDF generation.
            Toast.makeText(getContext(), "PDF file generated successfully. Check file in downloads", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            // below line is used
            // to handle error
            e.printStackTrace();
        }
        // after storing our pdf to that
        // location we are closing our PDF file.
        pdfDocument.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customers, container, false);
    }


}