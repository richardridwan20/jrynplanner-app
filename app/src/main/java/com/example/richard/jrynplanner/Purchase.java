package com.example.richard.jrynplanner;

public class Purchase {

    public static final String PURCHASEID = "PurchaseID";
    public static final String PACKAGEID = "PackageID";
    public static final String USERID = "UserID";
    public static final String NOTES = "Notes";
    public static final String DATE = "Date";

    private String PurchaseID,PackageName,Notes,Date;

    public Purchase(String purchaseID, String packageName, String notes, String date) {
        PurchaseID = purchaseID;
        PackageName = packageName;
        Notes = notes;
        Date = date;
    }

    public String getPurchaseID() {
        return PurchaseID;
    }

    public void setPurchaseID(String purchaseID) {
        PurchaseID = purchaseID;
    }

    public String getPackageName() {
        return PackageName;
    }

    public void setPackageName(String packageName) {
        PackageName = packageName;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String notes) {
        Notes = notes;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }
}
