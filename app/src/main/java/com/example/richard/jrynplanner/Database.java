package com.example.richard.jrynplanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Database extends SQLiteOpenHelper {

    private static final String CREATE_TABLE_USER =
            "CREATE TABLE users ( " + User.USERID + " CHAR(5) PRIMARY KEY," +
                    User.NAME + " VARCHAR(100)," +
                    User.EMAIL+ " VARCHAR(100)," +
                    User.PASSWORD+ " VARCHAR(50)," +
                    User.PHONENUMBER+ " VARCHAR(50) )";

    private static final String CREATE_TABLE_PACKAGE =
            "CREATE TABLE packages ( "+ Package.PACKAGEID +" CHAR(5) PRIMARY KEY," +
                    Package.PACKAGENAME +" VARCHAR(.100)," +
                    Package.PACKAGEPRICE +" INTEGER," +
                    Package.PACKAGERATING+" INTEGER )";

    private static final String CREATE_TABLE_PURCHASE =
            "CREATE TABLE purchases ( "+ Purchase.PURCHASEID +" CHAR(5) PRIMARY KEY," +
                    User.USERID +" CHAR(5)," +
                    Package.PACKAGEID +" CHAR(5)," +
                    Purchase.NOTES +" VARCHAR(100),"+
                    Purchase.DATE +" VARCHAR(10),"+
                    "FOREIGN KEY ("+ User.USERID +") REFERENCES users ("+ User.USERID +"),"+
                    "FOREIGN KEY ("+ Package.PACKAGEID +") REFERENCES packages ("+ Package.PACKAGEID +") )";



    public Database(Context context) {
        super(context, "Jrynplanner", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_PURCHASE);
        db.execSQL(CREATE_TABLE_PACKAGE);

        Log.d("DATABASE OPERATION", "TABLE CREATED");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void clear(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE users");
        db.execSQL("DROP TABLE purchases");
        db.execSQL("DROP TABLE packages");
        Log.d("DATABASE OPERATION", "TABLE REFRESH");
    }

    public void insertUser(String userid, String name, String email, String password, String phonenumber){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues userValues = new ContentValues();
        userValues.put(User.USERID,userid);
        userValues.put(User.NAME,name);
        userValues.put(User.EMAIL,email);
        userValues.put(User.PASSWORD,password);
        userValues.put(User.PHONENUMBER,phonenumber);

        db.insert("users",null,userValues);
        Log.d("DATABASE OPERATION", "ONE ROW INSERT TO USERS");

    }

    public Cursor viewUserDatabyEmail(String email){

        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM users WHERE "+User.EMAIL+" = '"+email+"'";

        Cursor rs = db.rawQuery(query,null);

        return rs;
    }

    public int countUserData(){

        SQLiteDatabase db = getReadableDatabase();

        int count = (int) DatabaseUtils.queryNumEntries(db,"users");

        return count;
    }

    public int countPackageData(){

        SQLiteDatabase db = getReadableDatabase();

        int count = (int) DatabaseUtils.queryNumEntries(db,"packages");

        return count;
    }


    public void insertPackage(String id,String name,int price,int rating){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues packageValues = new ContentValues();
        packageValues.put(Package.PACKAGEID,id);
        packageValues.put(Package.PACKAGENAME,name);
        packageValues.put(Package.PACKAGEPRICE,price);
        packageValues.put(Package.PACKAGERATING,rating);

        db.insert("packages",null,packageValues);
        Log.d("DATABASE OPERATION", "DATA INSERTED TO PACKAGES");
    }

    public Cursor getPackageData(){

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM packages";

        Cursor rs = db.rawQuery(query,null);

        return rs;
    }

    public void insertPurchase(String id,String userid,String packageid,String date,String notes){

        SQLiteDatabase db = getWritableDatabase();

        ContentValues purchaseValues = new ContentValues();
        purchaseValues.put(Purchase.PURCHASEID,id);
        purchaseValues.put(User.USERID,userid);
        purchaseValues.put(Package.PACKAGEID,packageid);
        purchaseValues.put(Purchase.DATE,date);
        purchaseValues.put(Purchase.NOTES,notes);

        db.insert("purchases",null,purchaseValues);
        Log.d("DATABASE OPERATION", "DATA INSERTED TO PURCHASES");
    }

    public void updatePurchase(String id, String date){

        SQLiteDatabase db = getWritableDatabase();

        ContentValues updatePurchaseValues = new ContentValues();
        updatePurchaseValues.put(Purchase.DATE,date);

        String where = ""+Purchase.PURCHASEID+"=?";
        String[] whereArgs = new String[] {id};

        db.update("purchases", updatePurchaseValues, where, whereArgs);
    }

    public void deletePurchase(String id){

        SQLiteDatabase db = getWritableDatabase();

        String where = ""+Purchase.PURCHASEID+"=?";
        String[] whereArgs = new String[] {id};

        db.delete("purchases", where, whereArgs);
    }

    public Cursor viewPurchaseDataByUserID(String userid){

        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM purchases WHERE "+User.USERID+" = '"+userid+"'";

        Cursor rs = db.rawQuery(query,null);

        return rs;

    }


    public Cursor viewPackageByID(String packageid){

        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT "+Package.PACKAGENAME+" FROM packages WHERE "+Package.PACKAGEID+" = '"+packageid+"'";

        Cursor rs = db.rawQuery(query,null);

        return rs;
    }

    public Cursor viewPurchase(){

        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM purchases";

        Cursor rs = db.rawQuery(query,null);

        return rs;

    }


}
