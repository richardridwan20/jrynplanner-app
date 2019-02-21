package com.example.richard.jrynplanner;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class RegisterActivity extends AppCompatActivity {

    EditText edt_name, edt_email, edt_password, edt_phone, edt_code;
    Button btn_register,btn_login,btn_code;
    Database db;
    String confirmationCode="";
    AlertDialog.Builder dialogBuilder;
    String id="";

    private boolean isEmailAvailable(String email){

        Cursor rs = db.viewUserDatabyEmail(email);

        if(rs.getCount() == 0){
            return true;
        }

        return false;
    }

    private boolean isAlNumSym(String email){

        int alpha = 0;
        int num = 0;
        int other = 0;
        int allowedSym = 0;

        for(int i = 0; i < email.length(); i++){
            if( (email.codePointAt(i) >= 65 && email.codePointAt(i) <= 90) ||
                    (email.codePointAt(i) >= 97 && email.codePointAt(i) <= 122) ){
                alpha++;
            } else if( (email.codePointAt(i) >= 48 && email.codePointAt(i) <= 57) ){
                num++;
            } else if( email.codePointAt(i) == 64 || email.codePointAt(i) == 95 || email.codePointAt(i) == 46 ){
                allowedSym++;
            } else {
                other++;
            }
        }

        if( alpha > 0 && num > 0 && other == 0 ){
            return true;
        }

        return false;
    }

    private boolean isSymbolBesideEachOther(String email){

        ArrayList<Integer> symbol = new ArrayList<>();

        for(int i = 0; i < email.length(); i++){
            if( email.codePointAt(i) == 64 || email.codePointAt(i) == 95 || email.codePointAt(i) == 46 ){
                symbol.add(i);
            }
        }

        for(int i = 0; i < symbol.size()-1; i++){
            if( (symbol.get(i) - symbol.get(i+1)) == -1 ){
                return true;
            }
        }

        return false;

    }

    private boolean isPasswordValid(String password){

        int upperCase = 0;
        int lowerCase = 0;
        int symbol = 0;
        int num = 0;

        for(int i = 0; i < password.length(); i++){
            if( (password.codePointAt(i) >= 65 && password.codePointAt(i) <= 90) ){
                upperCase++;
            } else if( (password.codePointAt(i) >= 97 && password.codePointAt(i) <= 122) ){
                lowerCase++;
            } else if( (password.codePointAt(i) >= 48 && password.codePointAt(i) <= 57) ) {
                num++;
            } else {
                symbol++;
            }
        }

        if( upperCase > 0 && lowerCase > 0 && symbol > 0 ){
            return true;
        }


        return false;
    }

    private boolean isDigit(String phone){

        int digit = 0;
        int plus = 0;
        int other = 0;

        for(int i = 0; i < phone.length(); i++){
            if( (phone.codePointAt(i) >= 48 && phone.codePointAt(i) <= 57) ){
                digit++;
            } else if( phone.codePointAt(i) == 43 ){
                plus++;
            } else {
                other++;
            }
        }

        if( other > 0 ){
            return false;
        }

        return true;
    }

    private boolean isCodeValid(String code){
        int upperCase = 0;
        int lowerCase =0;
        int num = 0;

        for(int i = 0; i < code.length(); i++){
            if( (code.codePointAt(i) >= 65 && code.codePointAt(i) <= 90) ){
                upperCase++;
            } else if( (code.codePointAt(i) >= 97 && code.codePointAt(i) <= 122) ){
                lowerCase++;
            } else if( (code.codePointAt(i) >= 48 && code.codePointAt(i) <= 57) ){
                num++;
            }
        }

        if( upperCase > 0 && lowerCase > 0 && num > 0 ){
            return true;
        }

        return false;
    }

    private String generateCode(){

        String smsCode="";
        String letter = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sbCode = new StringBuilder();

        while( sbCode.length() < 5 ){
            int idx = (random.nextInt(letter.length()));
            sbCode.append(letter.charAt(idx));
        }

        if( isCodeValid(sbCode.toString()) ){
            smsCode = sbCode.toString();
            return smsCode;
        }

        return generateCode();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        edt_name = findViewById(R.id.edt_name);
        edt_email = findViewById(R.id.edt_email);
        edt_password = findViewById(R.id.edt_password);
        edt_phone = findViewById(R.id.edt_phone);
        edt_code = findViewById(R.id.edt_confimationcode);
        btn_register = findViewById(R.id.btn_register);
        btn_code = findViewById(R.id.btn_code);
        btn_login = findViewById(R.id.btn_login);
        db = new Database(getApplicationContext());
        dialogBuilder = new AlertDialog.Builder(RegisterActivity.this);

        btn_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phone = edt_phone.getText().toString();
                confirmationCode = generateCode();

                if(!phone.equals("")){

                    if(checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
                        SmsManager sms = SmsManager.getDefault();

                        sms.sendTextMessage(phone,null,
                                "Please input this code to the Confirmation code field: "+confirmationCode,null,null);
                        Toast.makeText(RegisterActivity.this, "Confirmation code has been sent", Toast.LENGTH_SHORT).show();

                    } else {
                        requestPermissions(new String[]{
                                Manifest.permission.SEND_SMS
                        },0);
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Phone must be filled first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String name = edt_name.getText().toString();
                final String email = edt_email.getText().toString();
                final String password = edt_password.getText().toString();
                final String phone = edt_phone.getText().toString();
                final String code = edt_code.getText().toString();

                if(name.equals("")){
                    edt_name.setError("Name must be filled");
                } else if( !name.contains(" ") || name.startsWith(" ") || name.endsWith(" ") ){
                    edt_name.setError("Name must be 2 words");
                } else if( email.equals("") ){
                    edt_email.setError("Email must be filled");
                } else if( !email.contains("@") || !email.contains(".") ){
                    edt_email.setError("Email must have one '@' and '.' ");
                } else if( !isAlNumSym(email)  ){
                    edt_email.setError("Email must be alphanumeric");
                } else if( isSymbolBesideEachOther(email) ){
                    edt_email.setError("Symbol must not placed beside each other");
                } else if( !isEmailAvailable(email) ){
                    edt_email.setError("Email is not available");
                } else if( password.equals("") ){
                    edt_password.setError("Password must be filled");
                } else if( !isPasswordValid(password) ) {
                    edt_password.setError("Password must contain at least one uppercase letter, one lowercase letter, and one symbol");
                } else if( phone.length() < 10 || phone.length() > 14 ){
                    edt_phone.setError("Phone number length must be between 10 and 14 digits");
                } else if( !phone.startsWith("0") && !phone.startsWith("+62") ){
                    edt_phone.setError("Phone number must starts with '0' or '+62' ");
                } else if( !isDigit(phone) ){
                    edt_phone.setError("Phone must contains only digits");
                } else if( !code.equals(confirmationCode) || confirmationCode.equals("") ){
                    edt_code.setError("Confirmation code invalid");
                } else {

                    dialogBuilder.setTitle("Register")
                            .setMessage("Are you sure the data is correct?")
                            .setCancelable(true)
                            .setPositiveButton("Register", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int idnumber = db.countUserData()+1;
                                    String formattedId = String.format("%03d",idnumber);
                                    id = "US"+formattedId;

                                    db.insertUser(id,name,email,password,phone);
                                    confirmationCode = "";
                                    edt_name.setText("");
                                    edt_email.setText("");
                                    edt_password.setText("");
                                    edt_phone.setText("");
                                    edt_code.setText("");
                                    Toast.makeText(RegisterActivity.this, "Register Success, Please Login", Toast.LENGTH_SHORT).show();

                                    Intent i = new Intent(getApplicationContext(),MainActivity.class);
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

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
            }
        });



    }


}
