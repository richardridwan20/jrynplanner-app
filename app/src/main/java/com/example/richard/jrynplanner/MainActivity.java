package com.example.richard.jrynplanner;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.database.Cursor;
import android.opengl.ETC1;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    EditText edt_email,edt_password;
    Button btn_register,btn_login;
    Database db;
    RequestQueue queue;
    public static String loggedInUserID = "";
    public static boolean animate = false;
    RelativeLayout relativeOne;
    Handler handler = new Handler();

    private boolean isUserValid(String email,String password){

        Cursor rs = db.viewUserDatabyEmail(email);

        if(rs.getCount() == 0){
            return false;
        } else {
            while( rs.moveToFirst() ){
                if( email.equals(rs.getString(2)) && password.equals(rs.getString(3)) ){
                    loggedInUserID = rs.getString(0);
                    return true;
                } else {
                    return false;
                }
            }
        }

        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new Database(MainActivity.this);
        queue = Volley.newRequestQueue(MainActivity.this);
        edt_email = findViewById(R.id.edt_email);
        edt_password = findViewById(R.id.edt_password);
        btn_register = findViewById(R.id.btn_register);
        btn_login = findViewById(R.id.btn_login);
        relativeOne = findViewById(R.id.relative_one);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ((ViewGroup) findViewById(R.id.relative)).getLayoutTransition()
                    .enableTransitionType(LayoutTransition.CHANGING);
        }

        if (animate == false){
            relativeOne.setVisibility(View.GONE);

            Runnable runnable =  new Runnable() {
                @Override
                public void run() {
                    relativeOne.setVisibility(View.VISIBLE);
                    animate = true;
                }
            };

            handler.postDelayed(runnable, 2000);
        } else {
            relativeOne.setVisibility(View.VISIBLE);
        }

        if( db.countPackageData() == 0 ){
            jsonParse();
        }

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edt_email.getText().toString();
                String password = edt_password.getText().toString();

                if( email.equals("") ){
                    edt_email.setError("Email must be filled");
                } else if( password.equals("") ){
                    edt_password.setError("Password must be filled");
                } else if( !isUserValid(email,password) ){
                    Toast.makeText(MainActivity.this, "Credentials not recorded", Toast.LENGTH_SHORT).show();
                } else {
                    edt_email.setText("");
                    edt_password.setText("");
                    Intent i = new Intent(MainActivity.this,PackageActivity.class);
                    startActivity(i);
                }

            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(i);
            }
        });
    }

    private void jsonParse(){
        String url = "https://api.myjson.com/bins/byvt2";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                try {

                    for(int i = 0; i < response.length(); i++){

                        JSONObject singleObject = response.getJSONObject(i);

                        String packageid = singleObject.getString("PackageID");
                        String packagename = singleObject.getString("PackageName");
                        int price = singleObject.getInt("Price");
                        int rating = singleObject.getInt("Rating");

                        db.insertPackage(packageid,packagename,price,rating);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        queue.add(request);

    }


}
