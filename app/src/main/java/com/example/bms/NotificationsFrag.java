package com.example.bms;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class NotificationsFrag extends Fragment {
    public NotificationsFrag() {
        // Required empty public constructor
    }

    private CustomerViewModel customerViewModel;
    private RecyclerView notificationsRecycler;
    private BMSTransactionAdapter transactionAdapter;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        notificationsRecycler = view.findViewById(R.id.notifications);
        notificationsRecycler.setHasFixedSize(true);
        notificationsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        transactionAdapter = new BMSTransactionAdapter(new ArrayList<>(), getContext());

        customerViewModel = new ViewModelProvider(requireActivity()).get(CustomerViewModel.class);
        customerViewModel.getBMSTransactions().observe(getViewLifecycleOwner(), new Observer<ArrayList<BMSTransaction>>() {
            @Override
            public void onChanged(ArrayList<BMSTransaction> bmsTransactions) {
                transactionAdapter.setBmsTransactions(bmsTransactions);
            }
        });
        notificationsRecycler.setAdapter(transactionAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }
}