package com.example.bms;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.ViewHolder>{

    private ArrayList<Employee> employees;
    private Context context;
    private UserEmailPhoneIds userEmailPhoneIds = new UserEmailPhoneIds();
    private User user;
    private Employee adminEmployee;

    public EmployeeAdapter(ArrayList<Employee> employees, Context context) {
        this.employees = employees;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.employee, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Employee employee = employees.get(position);
        String firstname = InputValidation.toTitleCase(employee.getFirstName());
        String lastname = InputValidation.toTitleCase(employee.getLastName());

        holder.fullname.setText( firstname + " " + lastname);
        holder.employeeId.setText(employee.getEmployeeID());
        holder.phone.setText(employee.getPhone());
        holder.email.setText(employee.getEmail());
        holder.position.setText(employee.getPosition());
        holder.branch.setText(employee.getBranch());

    }

    @Override
    public int getItemCount() {
        return employees.size();
    }

    public void setUserEmailPhoneIds(UserEmailPhoneIds emailPhoneIds) {
        this.userEmailPhoneIds = emailPhoneIds;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView fullname;
        TextView employeeId;
        TextView phone;
        TextView email;
        TextView position;
        TextView branch;
        ImageView delete;
        ImageView edit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fullname = itemView.findViewById(R.id.fullname);
            employeeId = itemView.findViewById(R.id.employeeId);
            phone = itemView.findViewById(R.id.phone);
            email = itemView.findViewById(R.id.email);
            position = itemView.findViewById(R.id.position);
            branch = itemView.findViewById(R.id.branch);
            delete = itemView.findViewById(R.id.delete);
            delete.setOnClickListener(this);
            edit = itemView.findViewById(R.id.edit);
            edit.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == delete.getId()) {
                Employee employee = employees.get(getAdapterPosition());
                String firstname = InputValidation.toTitleCase(employee.getFirstName());
                String lastname = InputValidation.toTitleCase(employee.getLastName());
                new MaterialAlertDialogBuilder(context)
                        .setTitle("Alert")
                        .setMessage("Delete Employee \nID:" + employee.getEmployeeID() + " and \nName: " + firstname + " " + lastname + "?\nThis action is permanent.")
                        .setNeutralButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                EmployeeController.removeEmployee(employee, context, user, adminEmployee);
                                employees.remove(getAdapterPosition());
                                notifyDataSetChanged();
                            }
                        }).show();
            } else if (view.getId() == edit.getId()){
                Employee employee = employees.get(getAdapterPosition());
                String firstname = InputValidation.toTitleCase(employee.getFirstName());
                String lastname = InputValidation.toTitleCase(employee.getLastName());
                new MaterialAlertDialogBuilder(context)
                        .setTitle("Alert")
                        .setMessage("Edit Customer \nID:" + employee.getEmployeeID() + " and \nName: " + firstname + " " + lastname)
                        .setNeutralButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                Intent intent = new Intent(context, EmployeeAdd.class);
                                intent.putExtra("User", user);
                                intent.putExtra("Edit", true);
                                intent.putExtra("Employee", employee);
                                intent.putExtra("UserEmailIDs", userEmailPhoneIds);
                                intent.putExtra("AdminEmployee", adminEmployee);
                                context.startActivity(intent);
                            }
                        }).show();
            }
        }
    }

    public void setEmployees(ArrayList<Employee> employees){
        this.employees = employees;
        notifyDataSetChanged();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setEmployee(Employee employee) {
        this.adminEmployee = employee;
        notifyDataSetChanged();
    }
}
