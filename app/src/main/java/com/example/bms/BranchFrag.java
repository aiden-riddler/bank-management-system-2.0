
package com.example.bms;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class BranchFrag extends Fragment {

    public BranchFrag() {
        // Required empty public constructor
    }

    private FloatingActionButton addBranch;
    private BranchViewModel branchViewModel;
    private RecyclerView branchRecycler;
    private BranchAdapter branchAdapter;
    private User user;
    private Employee employee;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = (User) getActivity().getIntent().getSerializableExtra("User");
        employee = (Employee) getActivity().getIntent().getSerializableExtra("Employee");

        branchRecycler = view.findViewById(R.id.branchRecycler);
        branchRecycler.setHasFixedSize(true);
        branchRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        // get data
        branchAdapter = new BranchAdapter(new ArrayList<>(), getContext());
        branchAdapter.setUser(user);
        branchAdapter.setEmployee(employee);
        branchViewModel = new ViewModelProvider(requireActivity()).get(BranchViewModel.class);
        branchViewModel.getBranches().observe(getViewLifecycleOwner(), new Observer<ArrayList<Branch>>() {
            @Override
            public void onChanged(ArrayList<Branch> branches) {
                branchAdapter.setBranches(branches);
            }
        });
        branchRecycler.setAdapter(branchAdapter);

        addBranch = view.findViewById(R.id.addBranch);
        addBranch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), BranchAdd.class);
                intent.putExtra("User", user);
                intent.putExtra("Employee", employee);
                startActivity(intent);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_branch, container, false);
    }
}