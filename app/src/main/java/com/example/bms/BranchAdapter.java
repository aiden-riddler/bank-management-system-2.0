package com.example.bms;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class BranchAdapter extends RecyclerView.Adapter<BranchAdapter.ViewHolder>{

    private ArrayList<Branch> branches;
    private Context context;
    private User user;
    private Employee employee;

    public BranchAdapter(ArrayList<Branch> branches, Context context) {
        this.branches = branches;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.branch, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Branch branch = branches.get(position);

        holder.branchId.setText(branch.getId());
        holder.phone.setText(branch.getPhone());
        holder.address.setText(branch.getAddress());

    }

    @Override
    public int getItemCount() {
        return branches.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView branchId;
        TextView phone;
        TextView address;
        ImageView delete;
        ImageView edit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            branchId = itemView.findViewById(R.id.branchId);
            phone = itemView.findViewById(R.id.phone);
            address = itemView.findViewById(R.id.address);
            delete = itemView.findViewById(R.id.delete);
            delete.setOnClickListener(this);
            edit = itemView.findViewById(R.id.edit);
            edit.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == delete.getId()) {
                Branch branch = branches.get(getAdapterPosition());
                new MaterialAlertDialogBuilder(context)
                        .setTitle("Alert")
                        .setMessage("Delete " + branch.getAddress() + " branch? This action is permanent.")
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
                                BranchController.removeBranch(branch, context, user, employee);
                                branches.remove(getAdapterPosition());
                                notifyDataSetChanged();
                            }
                        });
            } else if (view.getId() == edit.getId()){
                Branch branch = branches.get(getAdapterPosition());
                new MaterialAlertDialogBuilder(context)
                        .setTitle("Alert")
                        .setMessage("Edit " + branch.getAddress() + " branch?")
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
                                Intent intent = new Intent(context, BranchAdd.class);
                                intent.putExtra("Branch", branch);
                                intent.putExtra("Employee", employee);
                                context.startActivity(intent);
                            }
                        });
            }
        }
    }

    public void setBranches(ArrayList<Branch> branches){
        this.branches = branches;
        notifyDataSetChanged();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
        notifyDataSetChanged();
    }
}
