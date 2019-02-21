package com.example.richard.jrynplanner;

public class Package {

    public static final String PACKAGEID = "PackageID";
    public static final String PACKAGENAME = "PackageName";
    public static final String PACKAGEPRICE = "Price";
    public static final String PACKAGERATING = "Rating";

    private String PackageID,PackageName;
    private int Price = 0;
    private int Rating = 0;

    public Package(String packageID, String packageName, int price, int rating) {
        PackageID = packageID;
        PackageName = packageName;
        Price = price;
        Rating = rating;
    }

    public String getPackageID() {
        return PackageID;
    }

    public void setPackageID(String packageID) {
        PackageID = packageID;
    }

    public String getPackageName() {
        return PackageName;
    }

    public void setPackageName(String packageName) {
        PackageName = packageName;
    }

    public int getPrice() {
        return Price;
    }

    public void setPrice(int price) {
        Price = price;
    }

    public int getRating() {
        return Rating;
    }

    public void setRating(int rating) {
        Rating = rating;
    }
}
