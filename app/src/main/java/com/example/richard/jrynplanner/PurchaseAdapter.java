package com.example.richard.jrynplanner;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class PurchaseAdapter extends ArrayAdapter<Purchase> {

    Context context;
    ArrayList<Purchase> purchases;

    public PurchaseAdapter(Context context, ArrayList<Purchase> purchases) {
        super(context, 0, purchases);
        this.context = context;
        this.purchases = purchases;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.content_purchase,parent,false);
        }

        TextView packageName = convertView.findViewById(R.id.package_name);
        TextView reserveDate = convertView.findViewById(R.id.reserve_date);
        TextView reserveNotes = convertView.findViewById(R.id.reserve_notes);

        packageName.setText(this.purchases.get(position).getPackageName());
        reserveDate.setText(this.purchases.get(position).getDate());
        reserveNotes.setText(this.purchases.get(position).getNotes());


        return convertView;
    }
}
