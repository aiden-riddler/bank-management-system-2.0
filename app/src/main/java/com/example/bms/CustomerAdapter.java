package com.example.bms;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
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

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ViewHolder>{
    private ArrayList<Customer> customers;

    private ArrayList<Customer> prevCustomers = new ArrayList<>();
    private ArrayList<Account> accounts;
    private Context context;
    private AccountAdapter accountAdapter;
    private UserEmailPhoneIds userEmailPhoneIds = new UserEmailPhoneIds();
    private ArrayList<Branch> branches = new ArrayList<>();
    private User user;
    private Employee employee;

    public CustomerAdapter(ArrayList<Customer> customers, ArrayList<Account> accounts, Context context) {
        this.customers = customers;
        this.accounts = accounts;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Customer customer = customers.get(position);
        String firstname = InputValidation.toTitleCase(customer.getFirstName());
        String lastname = InputValidation.toTitleCase(customer.getLastName());

        holder.fullname.setText( firstname + " " + lastname);
        holder.customerid.setText(customer.getCustomerId());
        holder.phone.setText(customer.getPhone());
        holder.email.setText(customer.getEmail());

        ArrayList<Account> customerAccounts = new ArrayList<>();
        for (Account account:accounts) {
            if (account.getCustomer().equals(customer.getCustomerId()))
                customerAccounts.add(account);
        }

        accountAdapter = new AccountAdapter(customerAccounts, context);
        accountAdapter.setBranches(branches);
        holder.accountsRecycler.setHasFixedSize(true);
        holder.accountsRecycler.setLayoutManager(new LinearLayoutManager(context));
        holder.accountsRecycler.setAdapter(accountAdapter);

        if (!user.getRole().equals("Manager"))
            holder.delete.setVisibility(View.GONE);

        if (user.getRole().equals("Teller"))
            holder.deposit.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }

    public void setBranches(ArrayList<Branch> branches) {
        this.branches = branches;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView fullname;
        TextView customerid;
        TextView phone;
        TextView email;
        ImageView delete;
        ImageView edit;
        ImageView deposit;
        RecyclerView accountsRecycler;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fullname = itemView.findViewById(R.id.fullname);
            customerid = itemView.findViewById(R.id.customerid);
            phone = itemView.findViewById(R.id.phone);
            email = itemView.findViewById(R.id.email);
            accountsRecycler = itemView.findViewById(R.id.accountsRecycler);
            delete = itemView.findViewById(R.id.delete);
            delete.setOnClickListener(this);
            edit = itemView.findViewById(R.id.edit);
            edit.setOnClickListener(this);
            deposit = itemView.findViewById(R.id.deposit);
            deposit.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == delete.getId()) {
                Customer customer = customers.get(getAdapterPosition());
                String firstname = InputValidation.toTitleCase(customer.getFirstName());
                String lastname = InputValidation.toTitleCase(customer.getLastName());
                new MaterialAlertDialogBuilder(context)
                        .setTitle("Alert")
                        .setMessage("Delete Customer \nID:" + customers.get(getAdapterPosition()).getCustomerId() + " and \nName: " + firstname + " " + lastname + "? This action is permanent.")
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
                                CustomerController.removeCustomer(customer, context, user, employee);
                                customers.remove(getAdapterPosition());
                                notifyDataSetChanged();
                            }
                        }).show();
            } else if (view.getId() == edit.getId()){
                Customer customer = customers.get(getAdapterPosition());
                String firstname = InputValidation.toTitleCase(customer.getFirstName());
                String lastname = InputValidation.toTitleCase(customer.getLastName());
                new MaterialAlertDialogBuilder(context)
                        .setTitle("Alert")
                        .setMessage("Edit Customer \nID:" + customers.get(getAdapterPosition()).getCustomerId() + "\nName: " + firstname + " " + lastname)
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
                                Intent intent = new Intent(context, CustomerAdd.class);
                                intent.putExtra("User", user);
                                intent.putExtra("Customer", customer);
                                intent.putExtra("Employee", employee);
                                intent.putExtra("UserEmailIDs", userEmailPhoneIds);
                                context.startActivity(intent);
                            }
                        }).show();
            } else if ( view.getId() == deposit.getId()){
                Customer customer = customers.get(getAdapterPosition());
                String firstname = InputValidation.toTitleCase(customer.getFirstName());
                String lastname = InputValidation.toTitleCase(customer.getLastName());
                new MaterialAlertDialogBuilder(context)
                        .setTitle("Alert")
                        .setMessage("Deposit funds for customer \nID:" + customers.get(getAdapterPosition()).getCustomerId() + "\nName: " + firstname + " " + lastname)
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
                                ArrayList<Account> customerAccounts = new ArrayList<>();
                                for (Account account:accounts) {
                                    if (account.getCustomer().equals(customer.getCustomerId()))
                                        customerAccounts.add(account);
                                }
                                Intent intent = new Intent(context, Deposit.class);
                                intent.putExtra("User", user);
                                intent.putExtra("Customer", customer);
                                intent.putExtra("Employee", employee);
                                intent.putExtra("Accounts", customerAccounts);
                                context.startActivity(intent);
                            }
                        }).show();
            }
        }
    }
    public void setCustomers(ArrayList<Customer> customers){
        this.customers = customers;
        this.prevCustomers = customers;
        notifyDataSetChanged();
    }
    public void setAccounts(ArrayList<Account> accounts){
        this.accounts = accounts;
        notifyDataSetChanged();
    }

    public void setUserEmailPhoneIds(UserEmailPhoneIds userEmailPhoneIds) {
        this.userEmailPhoneIds = userEmailPhoneIds;
    }

    public void setUser(User user) {
        this.user = user;
        notifyDataSetChanged();
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
        notifyDataSetChanged();
    }

    public ArrayList<Customer> getCustomers() {
        return customers;
    }

    public ArrayList<Account> getAccounts() {
        return accounts;
    }

    public void updatePrevCustomers(){
        this.prevCustomers = this.customers;
    }

    public void search(String query) {
        this.customers = this.prevCustomers;
        ArrayList<Customer> customerArrayList = new ArrayList<>();
        for (Customer customer:customers) {
            String searchString = customer.getFirstName() + " " + customer.getLastName() + " " + customer.getEmail();
            if (searchString.toLowerCase().contains(query.toLowerCase())) {
                customerArrayList.add(customer);
            }
        }
        this.customers = customerArrayList;
        notifyDataSetChanged();
    }

    public void revert() {
        this.customers = this.prevCustomers;
        notifyDataSetChanged();
    }
}
