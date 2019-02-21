package com.example.richard.jrynplanner;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class PackageActivity extends AppCompatActivity {

    Database db;
    ListView lv;
    ArrayList<Package> packages;
    PackageAdapter adapter;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package);

        Toolbar toolbar = findViewById(R.id.package_toolbar);
        toolbar.setTitle("Package List");
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        db = new Database(PackageActivity.this);
        lv = findViewById(R.id.lv_package);
        packages = new ArrayList<>();
        adapter = new PackageAdapter(PackageActivity.this,packages);
        insertDataPackagetoList();
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String currID = packages.get(position).getPackageID();
                String currName = packages.get(position).getPackageName();
                int currPrice = packages.get(position).getPrice();
                int currRating = packages.get(position).getRating();

                Intent i = new Intent(PackageActivity.this, PackageDetailActivity.class);

                i.putExtra("packageID", currID);
                i.putExtra("packagename", currName);
                i.putExtra("packageprice", currPrice);
                i.putExtra("packagerating", currRating);

                startActivity(i);
            }
        });

    }

    private void insertDataPackagetoList(){

        Cursor rs = db.getPackageData();

        if( rs.getCount() != 0 ){
            while( rs.moveToNext() ){
                String packageid = rs.getString(0);
                String packagename = rs.getString(1);
                int price = rs.getInt(2);
                int rating = rs.getInt(3);

                packages.add(new Package(packageid, packagename, price, rating));
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu, this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_package, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.map_menu) {
            Intent i = new Intent(PackageActivity.this, MapsActivity.class);
            startActivity(i);
        } else if (id == R.id.purchase_menu) {
            Intent i = new Intent(PackageActivity.this, PurchaseActivity.class);
            startActivity(i);
        } else if ( id == R.id.logout_menu ){
            Intent i = new Intent( PackageActivity.this, MainActivity.class );
            startActivity(i);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
