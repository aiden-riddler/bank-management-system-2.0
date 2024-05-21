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

public class BMSTransactionAdapter extends RecyclerView.Adapter<BMSTransactionAdapter.ViewHolder>{
    private ArrayList<BMSTransaction> bmsTransactions;
    private Context context;

    public BMSTransactionAdapter(ArrayList<BMSTransaction> bmsTransactions, Context context) {
        this.bmsTransactions = bmsTransactions;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bms_transaction, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BMSTransaction transaction = bmsTransactions.get(position);
        String output = "";
        if (transaction.getAction().equals("Receive")){
            output = transaction.getId() + " Confirmed you have received Ksh." + transaction.getAmount() + " from " + transaction.getReceiver() + " on " + transaction.getDate();
        } else if (transaction.getAction().equals("Send")){
            output = transaction.getId() + " Confirmed you sent Ksh." + transaction.getAmount() + " to " + transaction.getReceiver() + " on " + transaction.getDate();
        }
        holder.noteView.setText(output);
    }

    @Override
    public int getItemCount() {
        return bmsTransactions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView noteView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            noteView = itemView.findViewById(R.id.noteView);
        }
    }

    public void setBmsTransactions(ArrayList<BMSTransaction> bmsTransactions) {
        this.bmsTransactions = bmsTransactions;
        notifyDataSetChanged();
    }
}
