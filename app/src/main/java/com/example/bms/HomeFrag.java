package com.example.bms;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class HomeFrag extends Fragment {
    public HomeFrag() {
        // Required empty public constructor
    }
    private CardView requestLoan;
    private CardView mobileTransfer;
    private CardView accountsCard;
    private CardView repayCard;
    private CardView withdrawCard;
    private TextView amountView;
    private AccountViewModel accountViewModel;
    private ArrayList<Account> accountArrayList;
    private User user;
    private Customer customer;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = (User) getActivity().getIntent().getSerializableExtra("User");
        customer = (Customer) getActivity().getIntent().getSerializableExtra("Customer");

        amountView = view.findViewById(R.id.amountView);
        accountArrayList = new ArrayList<>();
        accountViewModel = new ViewModelProvider(requireActivity()).get(AccountViewModel.class);
        accountViewModel.getAccounts().observe(getViewLifecycleOwner(), new Observer<ArrayList<Account>>() {
            @Override
            public void onChanged(ArrayList<Account> accounts) {
                Double amount = 0.0;
                for (Account account:accounts) {
                    amount += account.getCurrentBalance();
                    accountArrayList.add(account);
                }
                amountView.setText("Ksh " + amount);
            }
        });

        repayCard = view.findViewById(R.id.repayLoanCard);
        repayCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flFragment, new AccountsFrag())
                        .addToBackStack(null)
                        .commit();

                Toast.makeText(getContext(), "Select Loan to repay.", Toast.LENGTH_SHORT).show();
            }
        });

        accountsCard = view.findViewById(R.id.accountsCard);
        accountsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flFragment, new AccountsFrag())
                        .addToBackStack(null)
                        .commit();
            }
        });

        requestLoan = view.findViewById(R.id.requestLoanCard);
        requestLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), RequestLoan.class);
                intent.putExtra("User", user);
                intent.putExtra("Customer", customer);
                intent.putExtra("Accounts", accountArrayList);
                startActivity(intent);
            }
        });

        mobileTransfer = view.findViewById(R.id.mobileTransfersCard);
        mobileTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MoneyTransfer.class);
                intent.putExtra("User", user);
                intent.putExtra("Customer", customer);
                intent.putExtra("Accounts", accountArrayList);
                startActivity(intent);
            }
        });

        withdrawCard = view.findViewById(R.id.withdrawCard);
        withdrawCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), Withdraw.class);
                intent.putExtra("User", user);
                intent.putExtra("Customer", customer);
                intent.putExtra("Accounts", accountArrayList);
                startActivity(intent);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
}