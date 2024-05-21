package com.example.bms;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class LoanApplicationsAdapter extends RecyclerView.Adapter<LoanApplicationsAdapter.ViewHolder>{
    private ArrayList<LoanApplication> loanApplications;
    private Context context;
    private User user;
    private ConstraintLayout progressCard;
    private TextView progressText;
    private Employee employee;

    public LoanApplicationsAdapter(ArrayList<LoanApplication> loanApplications, Context context, ConstraintLayout progressCard, TextView progressText) {
        this.loanApplications = loanApplications;
        this.context = context;
        this.progressCard = progressCard;
        this.progressText = progressText;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.loan_application, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LoanApplication loanApplication = loanApplications.get(position);
        holder.loanNumber.setText(loanApplication.getLoanNumber());
        holder.employer.setText(loanApplication.getEmployer());
        holder.occupation.setText(loanApplication.getOccupation());
        holder.annualIncome.setText(String.valueOf(loanApplication.getAnnualIncome()));
        holder.monthlyIncome.setText(String.valueOf(loanApplication.getIncome()));
        holder.mortgage.setText(String.valueOf(loanApplication.getMortgage()));
        holder.loanAmount.setText(String.valueOf(loanApplication.getLoanAmount()));
        holder.loanPurpose.setText(loanApplication.getLoanPurpose());
        holder.status.setText(loanApplication.getStatus());
        if (user.getRole().equals("Customer") || user.getRole().equals("Teller") || loanApplication.getStatus().equals("Approved") || loanApplication.getStatus().equals("Rejected")) {
            holder.approve.setVisibility(View.GONE);
            holder.reject.setVisibility(View.GONE);
        }

        if (loanApplication.getStatus().equals("Approved"))
            holder.status.setTextColor(Color.GREEN);
        else if (loanApplication.getStatus().equals("Rejected")) {
            holder.status.setTextColor(Color.RED);
            holder.status.setText("Not Approved");
        }

    }

    @Override
    public int getItemCount() {
        return loanApplications.size();
    }
    public void setLoanApplications(ArrayList<LoanApplication> loanApplicationArrayList) {
        this.loanApplications = loanApplicationArrayList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView loanNumber;
        TextView employer;
        TextView occupation;
        TextView annualIncome;
        TextView monthlyIncome;
        TextView mortgage;
        TextView loanAmount;
        TextView loanPurpose;
        TextView status;
        Button approve;
        Button reject;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            loanNumber = itemView.findViewById(R.id.loanNumber);
            employer = itemView.findViewById(R.id.employer);
            occupation = itemView.findViewById(R.id.occupation);
            annualIncome = itemView.findViewById(R.id.annual_income);
            monthlyIncome = itemView.findViewById(R.id.monthly_income);
            mortgage = itemView.findViewById(R.id.mortgage);
            loanAmount = itemView.findViewById(R.id.loan_amount);
            loanPurpose = itemView.findViewById(R.id.loanPurpose);
            status = itemView.findViewById(R.id.status);
            approve = itemView.findViewById(R.id.approve);
            approve.setOnClickListener(this);
            reject = itemView.findViewById(R.id.reject);
            reject.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == approve.getId()){
                LoanApplication loanApplication = loanApplications.get(getAdapterPosition());
                new MaterialAlertDialogBuilder(context)
                        .setTitle("Approve Loan")
                        .setMessage("Do you want to approve customer's loan?")
                        .setPositiveButton("APPROVE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                progressText.setText("Approving Loan...");
                                progressCard.setVisibility(View.VISIBLE);
                                LoanController.createLoan(loanApplication, progressCard, context, user, employee);
                            }
                        })
                        .setNeutralButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        }).show();
            } else if (view.getId() == reject.getId()){
                LoanApplication loanApplication = loanApplications.get(getAdapterPosition());
                new MaterialAlertDialogBuilder(context)
                        .setTitle("Reject Loan")
                        .setMessage("Do you want to reject customer's loan?")
                        .setNegativeButton("REJECT", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                progressText.setText("Approving Loan...");
                                progressCard.setVisibility(View.VISIBLE);
                                LoanController.rejectApplication(loanApplication, progressCard, context, user, employee);
                            }
                        })
                        .setNeutralButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        }).show();
            }
        }
    }

    public void setUser(User user) {
        this.user = user;
        notifyDataSetChanged();
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
        notifyDataSetChanged();
    }
}
