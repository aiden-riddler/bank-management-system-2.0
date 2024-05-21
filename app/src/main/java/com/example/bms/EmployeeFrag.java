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

public class EmployeeFrag extends Fragment {

    public EmployeeFrag() {
        // Required empty public constructor
    }

    private FloatingActionButton addEmployee;
    private EmployeeViewModel employeeViewModel;
    private RecyclerView employeeRecycler;
    private EmployeeAdapter employeeAdapter;
    private DataViewModel dataViewModel;
    private UserEmailPhoneIds emailPhoneIds;
    private User user;
    private Employee employee;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = (User) getActivity().getIntent().getSerializableExtra("User");
        employee = (Employee) getActivity().getIntent().getSerializableExtra("Employee");

        employeeRecycler = view.findViewById(R.id.employeeRecycler);
        employeeRecycler.setHasFixedSize(true);
        employeeRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        // get data
        employeeAdapter = new EmployeeAdapter(new ArrayList<>(), getContext());
        employeeAdapter.setUser(user);
        employeeAdapter.setEmployee(employee);
        employeeViewModel = new ViewModelProvider(requireActivity()).get(EmployeeViewModel.class);
        employeeViewModel.getEmployees().observe(getViewLifecycleOwner(), new Observer<ArrayList<Employee>>() {
            @Override
            public void onChanged(ArrayList<Employee> employees) {
                employeeAdapter.setEmployees(employees);
            }
        });

        emailPhoneIds = new UserEmailPhoneIds();
        dataViewModel = new ViewModelProvider(requireActivity()).get(DataViewModel.class);
        dataViewModel.getUserEmailPhoneIds().observe(getViewLifecycleOwner(), new Observer<UserEmailPhoneIds>() {
            @Override
            public void onChanged(UserEmailPhoneIds userEmailPhoneIds) {
                emailPhoneIds.addPhoneNumbers(userEmailPhoneIds.getPhoneNumbers());
                emailPhoneIds.addEmails(userEmailPhoneIds.getEmails());
                emailPhoneIds.addUserIds(userEmailPhoneIds.getUserIds());
                employeeAdapter.setUserEmailPhoneIds(emailPhoneIds);
            }
        });

        employeeRecycler.setAdapter(employeeAdapter);

        addEmployee = view.findViewById(R.id.add_employee);
        addEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EmployeeAdd.class);
                intent.putExtra("UserEmailIDs", emailPhoneIds);
                intent.putExtra("User", user);
                intent.putExtra("Employee", employee);
                startActivity(intent);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_employee, container, false);
    }
}