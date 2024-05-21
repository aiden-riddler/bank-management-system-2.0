package com.example.bms;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class LoanAdapter extends RecyclerView.Adapter<LoanAdapter.ViewHolder>{
    private ArrayList<Loan> loans;
    private Context context;
    private User user = new User();
    private Customer customer;

    private ArrayList<Account> accounts = new ArrayList<>();

    public LoanAdapter(ArrayList<Loan> loans, Context context) {
        this.loans = loans;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.loan, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Loan loan = loans.get(position);
        SimpleDateFormat DateFor = new SimpleDateFormat("dd MMMM yyyy");
        String startDate = DateFor.format(loan.getStartingDate());
        String dueDate = DateFor.format(loan.getDueDate());

        holder.loanNumber.setText(loan.getId());
        holder.startDate.setText(startDate);
        holder.dueDate.setText(dueDate);
        holder.loanAmount.setText("Ksh. " + loan.getAmount());
        holder.balance.setText("Ksh. " + loan.getAmountDue());

        if (!user.getRole().equals("Customer")){
            holder.repay.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return loans.size();
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView loanNumber;
        TextView startDate;
        TextView dueDate;
        TextView loanAmount;
        TextView balance;
        Button repay;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            loanNumber = itemView.findViewById(R.id.loanNumber);
            startDate = itemView.findViewById(R.id.startDate);
            dueDate = itemView.findViewById(R.id.dueDate);
            loanAmount = itemView.findViewById(R.id.loan_amount);
            balance = itemView.findViewById(R.id.balance);
            repay = itemView.findViewById(R.id.repay);
            repay.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (view.getId() == repay.getId()){
                Loan loan = loans.get(getAdapterPosition());
                Intent intent = new Intent(context, RepayLoan.class);
                intent.putExtra("User", user);
                intent.putExtra("Accounts", accounts);
                intent.putExtra("Customer", customer);
                intent.putExtra("Loan", loan);
                context.startActivity(intent);
            }
        }
    }

    public void setLoans(ArrayList<Loan> loans){
        this.loans = loans;
        notifyDataSetChanged();
    }

    public void setAccounts(ArrayList<Account> accounts) {
        this.accounts = accounts;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
