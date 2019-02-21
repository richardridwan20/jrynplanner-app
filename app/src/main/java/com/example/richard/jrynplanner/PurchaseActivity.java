package com.example.richard.jrynplanner;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class PurchaseActivity extends AppCompatActivity {

    ListView lvPurchase;
    ArrayList<Purchase> purchases;
    Database db;
    PurchaseAdapter adapter;
    Button btnUpdate, btnDelete;
    AlertDialog.Builder purchaseDialog, dialogBuilder;
    TextView packageName, reserveDate, reserveNotes, changeDate;
    DatePickerDialog datePickerDialog;
    int diff = 0;
    String chosenDate = "";

    private void insertDataPurchasetoList(){

        purchases.clear();

        Cursor rsPurchase = db.viewPurchaseDataByUserID(MainActivity.loggedInUserID);
        String packageID="";
        String purchaseID="";

        Cursor rsPackage;

        if( rsPurchase.getCount() != 0 ){
            while (rsPurchase.moveToNext()){

                purchaseID = rsPurchase.getString(0);
                packageID = rsPurchase.getString(2);
                String notes = rsPurchase.getString(3);
                String date = rsPurchase.getString(4);

                rsPackage = db.viewPackageByID(packageID);

                while(rsPackage.moveToNext()){

                    String packageName = rsPackage.getString(0);
                    purchases.add(new Purchase(purchaseID,packageName,notes,date));

                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);

        Toolbar toolbar = findViewById(R.id.purchase_toolbar);
        toolbar.setTitle("Purchases");
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        lvPurchase = findViewById(R.id.lv_purchase);
        db = new Database(PurchaseActivity.this);
        purchases = new ArrayList<>();
        adapter = new PurchaseAdapter(PurchaseActivity.this,purchases);
        insertDataPurchasetoList();
        lvPurchase.setAdapter(adapter);
        dialogBuilder = new AlertDialog.Builder(PurchaseActivity.this);



        lvPurchase.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String currentID = purchases.get(position).getPurchaseID();
                String name = purchases.get(position).getPackageName();
                String date = purchases.get(position).getDate();
                String notes = purchases.get(position).getNotes();
                showDialog(name, date, notes, currentID);
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if( id == android.R.id.home){
            Intent i = new Intent(PurchaseActivity.this,PackageActivity.class);
            startActivity(i);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void showDialog(String name, String date, String notes, final String id){
        purchaseDialog = new AlertDialog.Builder(PurchaseActivity.this);

        final View purchaseView = getLayoutInflater().inflate(R.layout.dialog_purchase, null);
        purchaseDialog.setView(purchaseView);
        purchaseDialog.setCancelable(true);

        final AlertDialog alertDialog = purchaseDialog.create();

        packageName = purchaseView.findViewById(R.id.package_name);
        reserveDate = purchaseView.findViewById(R.id.reserve_date);
        reserveNotes = purchaseView.findViewById(R.id.reserve_notes);
        changeDate = purchaseView.findViewById(R.id.change_date);
        btnUpdate = purchaseView.findViewById(R.id.btn_update);
        btnDelete = purchaseView.findViewById(R.id.btn_delete);

        packageName.setText(name);
        reserveDate.setText(date);
        reserveNotes.setText(notes);
        btnUpdate.setEnabled(false);

        changeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogBuilder.setTitle("Update Data")
                        .setMessage("Are you sure the data is correct?")
                        .setCancelable(true)
                        .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.updatePurchase(id, reserveDate.getText().toString());

                                purchases = new ArrayList<>();
                                adapter = new PurchaseAdapter(PurchaseActivity.this,purchases);
                                insertDataPurchasetoList();

                                lvPurchase.setAdapter(adapter);

                                Toast.makeText(PurchaseActivity.this, "Data updated!", Toast.LENGTH_SHORT).show();

                                alertDialog.dismiss();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }

        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder.setTitle("Delete Data")
                        .setMessage("Are you sure you want to delete this data ?")
                        .setCancelable(true)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.deletePurchase(id);

                                purchases = new ArrayList<>();
                                adapter = new PurchaseAdapter(PurchaseActivity.this,purchases);
                                insertDataPurchasetoList();

                                lvPurchase.setAdapter(adapter);

                                Toast.makeText(PurchaseActivity.this, "Data deleted!", Toast.LENGTH_SHORT).show();

                                alertDialog.dismiss();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

        alertDialog.show();
    }

    public void showDateDialog(){

        final Calendar currentDate = Calendar.getInstance();

        datePickerDialog = new DatePickerDialog(this, R.style.DialogTheme ,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month+=1;

                int currYear = currentDate.get(Calendar.YEAR);
                int currMonth = currentDate.get(Calendar.MONTH) + 1;
                int currDate = currentDate.get(Calendar.DAY_OF_MONTH);
                currentDate.set(currYear,currMonth,currDate);

                Calendar newDate = Calendar.getInstance();
                newDate.set(year,month,day);

                long diffTemp = (newDate.getTimeInMillis() - currentDate.getTimeInMillis()) / (24*60*60*1000);
                diff = (int)diffTemp;

                String formatDay = String.format("%02d",day);
                String formatMonth = String.format("%02d",month);

                if (diff >= 7){
                    chosenDate = formatDay + "/" + formatMonth + "/"+ year;
                    reserveDate.setText(chosenDate);
                    btnUpdate.setEnabled(true);
                } else {
                    chosenDate = formatDay + "/" + formatMonth + "/"+ year;
                    reserveDate.setText(chosenDate);
                    btnUpdate.setEnabled(false);
                    Toast.makeText(PurchaseActivity.this, "Date must be one week from now!", Toast.LENGTH_SHORT).show();
                }
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

}
