package com.example.richard.jrynplanner;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PackageDetailActivity extends AppCompatActivity {

    TextView packageDetailName, packageDetailPrice, packageDetailRating, btnDate;
    Button reserveBtn;
    DatePickerDialog datePickerDialog;
    EditText notes;
    AlertDialog.Builder dialogBuilder;
    Database db;
    int diff = 0;
    String purchaseid = "";
    String chosenDate="";

    private int getLastPurchaseID(){

        Cursor rs = db.viewPurchase();
        int idNumber = 0;

        if( rs.getCount() != 0 ){

            rs.moveToLast();

            String idInString = rs.getString(0);
            idNumber = Integer.parseInt(idInString.substring(2,idInString.length()));

        }

        return idNumber;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_detail);

        Toolbar toolbar = findViewById(R.id.packagedetail_toolbar);
        toolbar.setTitle("Make a Purchase");
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        packageDetailName = findViewById(R.id.package_detail_name);
        packageDetailPrice = findViewById(R.id.package_detail_price);
        packageDetailRating = findViewById(R.id.package_detail_rating);
        notes = findViewById(R.id.Notes);
        btnDate = findViewById(R.id.btn_date);
        reserveBtn = findViewById(R.id.reserve_btn);
        db = new Database(PackageDetailActivity.this);
        dialogBuilder = new AlertDialog.Builder(PackageDetailActivity.this);

        Intent i = getIntent();

        final String packageID = i.getStringExtra("packageID");
        String packageName = i.getStringExtra("packagename");
        int packageRating  = i.getIntExtra("packagerating", 0);
        String packagePrice = NumberFormat.getInstance(Locale.GERMAN).format(i.getIntExtra("packageprice", 0));

        packageDetailName.setText(packageName);
        packageDetailPrice.setText("Rp. "+ packagePrice);
        packageDetailRating.setText(Integer.toString(packageRating));

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });

        reserveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( btnDate.getText().toString().equals("Choose Date") ){
                    Toast.makeText(PackageDetailActivity.this, "Date must be filled", Toast.LENGTH_SHORT).show();
                } else {

                    dialogBuilder.setTitle("Make Purchase")
                            .setMessage("Are you sure the data is correct?")
                            .setCancelable(true)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    int idnumber = getLastPurchaseID()+1;
                                    String formattedId = String.format("%03d",idnumber);
                                    purchaseid = "PC" + formattedId;

                                    db.insertPurchase(purchaseid,MainActivity.loggedInUserID,packageID,chosenDate,notes.getText().toString());

                                    Toast.makeText(PackageDetailActivity.this, "Purchase Recorded", Toast.LENGTH_SHORT).show();

                                    Intent i = new Intent(PackageDetailActivity.this,PackageActivity.class);
                                    startActivity(i);
                                    finish();

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if( id == android.R.id.home){
            Intent i = new Intent(PackageDetailActivity.this,PackageActivity.class);
            startActivity(i);
            finish();
        }

        return super.onOptionsItemSelected(item);
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

                if (diff >= 7){
                    String formatDay = String.format("%02d",day);
                    String formatMonth = String.format("%02d",month);

                    chosenDate = formatDay + "/" + formatMonth + "/"+ year;
                    btnDate.setText(chosenDate);
                } else {
                    Toast.makeText(PackageDetailActivity.this, "Date must be one week from now!", Toast.LENGTH_SHORT).show();
                }
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }
}
