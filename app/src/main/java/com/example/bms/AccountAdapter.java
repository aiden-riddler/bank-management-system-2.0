package com.example.bms;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder>{
    private ArrayList<Account> accounts;
    private Context context;
    private ArrayList<Branch> branches = new ArrayList<>();
    private User user = new User();
    public AccountAdapter(ArrayList<Account> accounts, Context context) {
        this.accounts = accounts;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.account, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Account account = accounts.get(position);
        holder.accountNumber.setText(account.getAccountNumber());
        String branchName = "";
        for (Branch branch:branches){
            if (branch.getId().equals(account.getBranch()))
                branchName = branch.getAddress();
        }
        holder.branch.setText(branchName);

        SimpleDateFormat DateFor = new SimpleDateFormat("dd MMMM yyyy");
        String date = DateFor.format(account.getOpeningDate());
        holder.date.setText(date);

        holder.balance.setText(String.valueOf(account.getCurrentBalance()));
        holder.interest.setText(String.valueOf(account.getInterestRate()));
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    public void setBranches(ArrayList<Branch> branches) {
        this.branches = branches;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView accountNumber;
        TextView branch;
        TextView date;
        TextView balance;
        TextView interest;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            accountNumber = itemView.findViewById(R.id.accountNumber);
            branch = itemView.findViewById(R.id.branch);
            date = itemView.findViewById(R.id.date);
            balance = itemView.findViewById(R.id.balance);
            interest = itemView.findViewById(R.id.interest);

        }
    }

    public void setAccounts(ArrayList<Account> accounts){
        this.accounts = accounts;
        notifyDataSetChanged();
    }

    public void setUser(User user) {
        this.user = user;
    }
}
