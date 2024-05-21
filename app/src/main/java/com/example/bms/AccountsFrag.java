package com.example.bms;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class AccountsFrag extends Fragment {

    public AccountsFrag() {
        // Required empty public constructor
    }
    private AccountViewModel accountViewModel;
    private LoanViewModel loanViewModel;
    private RecyclerView accountsRecycler;
    private RecyclerView loanApplicationsRecycler;
    private RecyclerView loansRecycler;
    private AccountAdapter accountAdapter;
    private LoanAdapter loanAdapter;
    private LoanApplicationsAdapter loanApplicationsAdapter;
    private User user;
    private BranchViewModel branchViewModel;
    private ConstraintLayout progressCard;
    private TextView progressText;
    private Customer customer;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = (User) getActivity().getIntent().getSerializableExtra("User");
        customer = (Customer) getActivity().getIntent().getSerializableExtra("Customer");
        progressCard = getActivity().findViewById(R.id.progress);
        progressText = getActivity().findViewById(R.id.progressText);

        accountsRecycler = view.findViewById(R.id.accounts);
        accountsRecycler.setHasFixedSize(true);
        accountsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        loanApplicationsRecycler = view.findViewById(R.id.loanApplications);
        loanApplicationsRecycler.setHasFixedSize(true);
        loanApplicationsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        loansRecycler = view.findViewById(R.id.loans);
        loansRecycler.setHasFixedSize(true);
        loansRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        // get data
        loanAdapter = new LoanAdapter(new ArrayList<>(), getContext());
        loanAdapter.setUser(user);
        loanAdapter.setCustomer(customer);
        accountAdapter = new AccountAdapter(new ArrayList<>(), getContext());
        accountAdapter.setUser(user);
        branchViewModel = new ViewModelProvider(requireActivity()).get(BranchViewModel.class);
        branchViewModel.getBranches().observe(getViewLifecycleOwner(), new Observer<ArrayList<Branch>>() {
            @Override
            public void onChanged(ArrayList<Branch> branches) {
                accountAdapter.setBranches(branches);
            }
        });

        accountViewModel = new ViewModelProvider(requireActivity()).get(AccountViewModel.class);
        accountViewModel.getAccounts().observe(getViewLifecycleOwner(), new Observer<ArrayList<Account>>() {
            @Override
            public void onChanged(ArrayList<Account> accounts) {
                accountAdapter.setAccounts(accounts);
                loanAdapter.setAccounts(accounts);
            }
        });

        loanApplicationsAdapter = new LoanApplicationsAdapter(new ArrayList<>(), getContext(), progressCard, progressText);
        loanApplicationsAdapter.setUser(user);
        loanViewModel = new ViewModelProvider(requireActivity()).get(LoanViewModel.class);
        loanViewModel.getLoanApplications().observe(getViewLifecycleOwner(), new Observer<ArrayList<LoanApplication>>() {
            @Override
            public void onChanged(ArrayList<LoanApplication> loanApplications) {
                loanApplicationsAdapter.setLoanApplications(loanApplications);
            }
        });

        loanViewModel.getLoans().observe(getViewLifecycleOwner(), new Observer<ArrayList<Loan>>() {
            @Override
            public void onChanged(ArrayList<Loan> loans) {
                loanAdapter.setLoans(loans);
            }
        });

        loanApplicationsRecycler.setAdapter(loanApplicationsAdapter);
        accountsRecycler.setAdapter(accountAdapter);
        loansRecycler.setAdapter(loanAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_accounts, container, false);
    }
}