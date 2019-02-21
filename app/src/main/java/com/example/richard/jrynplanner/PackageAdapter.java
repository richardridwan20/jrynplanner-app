package com.example.richard.jrynplanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class PackageAdapter extends ArrayAdapter<Package> {

    Context context;
    ArrayList<Package> packages;

    public PackageAdapter(Context context, ArrayList<Package> packages) {
        super(context, 0,packages);
        this.context = context;
        this.packages = packages;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.content_package,parent,false);
        }

        TextView ID = convertView.findViewById(R.id.tv_packageid);
        TextView PackageName = convertView.findViewById(R.id.tv_packagename);
        TextView Price = convertView.findViewById(R.id.tv_packageprice);
        TextView Rating = convertView.findViewById(R.id.tv_packagerating);

        String id = this.packages.get(position).getPackageID();
        String name = this.packages.get(position).getPackageName();
        String price = NumberFormat.getInstance(Locale.GERMAN).format(this.packages.get(position).getPrice());
        String rating = Integer.toString(this.packages.get(position).getRating());

        ID.setText(id);
        PackageName.setText(name);
        Price.setText("Rp. " + price);
        Rating.setText("Rating: "+ rating);

        return convertView;
    }
}
