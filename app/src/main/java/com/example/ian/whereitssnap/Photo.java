package com.example.ian.whereitssnap;

/**
 * Created by Ian on 3/7/2018.
 */

import android.net.Uri;
import android.location.Location;

public class Photo {

    private String mTitle;
    private Uri mStorageLocation;
    private String mTag1;
    private String mTag2;
    private String mTag3;

    //for the Location feature
    private Location mGpsLocation;

    //Getter method for location
    public Location getGpsLocation(){
        return mGpsLocation;
    }

    //setter method for location
    public void setGpsLocation(Location mGpsLocation){
        this.mGpsLocation = mGpsLocation;
    }

    public String getTitle(){

        return mTitle;
    }

    public void setTitle(String title){
        this.mTitle = title;
    }

    public Uri getStorageLocation(){
        return mStorageLocation;
    }

    public void setStorageLocation(Uri storageLocation){
        this.mStorageLocation = storageLocation;
    }

    public String getTag1(){
        return mTag1;
    }

    public void setTag1(String tag1){
        this.mTag1 = tag1;
    }

    public String getTag2(){
        return mTag2;
    }

    public void setTag2(String tag2){
        this.mTag2 = tag2;
    }

    public String getTag3(){
        return mTag3;
    }

    public void setTag3(String tag3){
        this.mTag3 = tag3;
    }

    /*
    The Uri - Uniform Resource Identifier, holds the address of the file in the computer or network (if there is one).
    So, instead of saving the whole photo in the database, we store its location in internal storage and use it to get access to it.
     */
}
