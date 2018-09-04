package com.example.ian.whereitssnap;

/**
 * Created by Ian on 3/7/2018.
 */

import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.util.Log;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;

public class DataManager {

    //This is the actual database
    private SQLiteDatabase db;

    /*
    public static final strings that contains names of rows and columns that we need to refer to within and without this class
     */

    public static final String TABLE_ROW_ID = "_id";
    public static final String TABLE_ROW_TITLE = "image_title";
    public static final String TABLE_ROW_URI = "image_uri";

    /*
    New with version 2
     */
    public static final String TABLE_ROW_LOCATION_LAT = "gps_location_lat";
    public static final String TABLE_ROW_LOCATION_LONG = "gps_location_long";

    /*
    private static final Strings that we will need to refer to just inside the class.
     */

    private static final String DB_NAME = "wis_db";
    private static final int DB_VERSION = 2;//change db version so that an update can be done by system when constructing
    //custom sqliteopenhelper.
    private static final String TABLE_PHOTOS = "wis_table_photos";
    private static final String TABLE_TAGS = "wis_table_tags";
    private static final String TABLE_ROW_TAG1 = "tag1";
    private static final String TABLE_ROW_TAG2 = "tag2";
    private static final String TABLE_ROW_TAG3 = "tag3";
    public static final String TABLE_ROW_TAG = "tag"; //for the tags table

    //our constructor. Will simply initialize our internal CustomSQLiteOpenHelper class
    public DataManager(Context context){

        //create an instance of our CustomSQLiteOpenHelper internal class
        CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);

        //get a writeable database
        db = helper.getWritableDatabase();
    }

    //Here are all our helper methods
    public void addPhoto (Photo photo){

        //add all the details to the photos table
        //With db version two, modified to add coordinates.
        String query = "INSERT INTO " + TABLE_PHOTOS + " (" + TABLE_ROW_TITLE + ", " + TABLE_ROW_URI + ", " + TABLE_ROW_LOCATION_LAT
        + ", " + TABLE_ROW_LOCATION_LONG + ", " + TABLE_ROW_TAG1 + ", " + TABLE_ROW_TAG2 + ", " + TABLE_ROW_TAG3 + ") " +
                "VALUES (" + "'" + photo.getTitle() + "'" + ", " + "'" + photo.getStorageLocation() + "'" +
                ", " + "'" + photo.getGpsLocation().getLatitude() + "'" + ", " + "'" + photo.getGpsLocation().getLongitude()
        + "'" + ", " + "'" + photo.getTag1() + "'" + ", " + "'" + photo.getTag2() + "'" + ", " +
                "'" + photo.getTag3() + "'" + ");";

        Log.i("addPhoto()",query);

        db.execSQL(query);

        //Add new tags to the tags table. The SQL statement ensures that a tag that exists is not added to the table
        //Only unique tables should be added
        query = "INSERT INTO " + TABLE_TAGS + "( " + TABLE_ROW_TAG + ") " + "SELECT '" + photo.getTag1() + "' " +
                "WHERE NOT EXISTS (SELECT 1 FROM " + TABLE_TAGS + " WHERE " + TABLE_ROW_TAG +
                " = " + "'" + photo.getTag1() + "');";
        db.execSQL(query);

        query = "INSERT INTO " + TABLE_TAGS + "(" + TABLE_ROW_TAG + ")" + "SELECT '" + photo.getTag2() + "' " +
                "WHERE NOT EXISTS (SELECT 1 FROM " + TABLE_TAGS + " WHERE " + TABLE_ROW_TAG + " = " + "'" +
                photo.getTag2() + "');";
        db.execSQL(query);

        query = "INSERT INTO " + TABLE_TAGS + "(" + TABLE_ROW_TAG + ")" + "SELECT '" + photo.getTag3() + "' " +
                "WHERE NOT EXISTS (SELECT 1 FROM " + TABLE_TAGS + " WHERE " + TABLE_ROW_TAG + " = " + "'" +
                photo.getTag3() + "');";
        db.execSQL(query);

        //close database once done with it, that is saving data in it.
        //database opened everytime an instance of datamanager is created by a Fragment/activity/class

    }//end addPhoto

    //getTitles method to retrieve titles from wis_table_photos
    public Cursor getTitles (){

        Cursor c = db.rawQuery("SELECT " + TABLE_ROW_ID + ", " + TABLE_ROW_TITLE + " from " + TABLE_PHOTOS,null);
        c.moveToFirst(); //So as to start from the first item

        //close the database
        db.close();

        return c;
    }

    //getTitlesWithTag method, to retrieve titles only with the given tag
    public Cursor getTitlesWithTag (String tag){

        Cursor c = db.rawQuery("SELECT " + TABLE_ROW_ID + ", " + TABLE_ROW_TITLE + " from " + TABLE_PHOTOS +
        " WHERE " + TABLE_ROW_TAG1 + " = " + "'" + tag + "' or " + TABLE_ROW_TAG2 + " = " + "'" + tag + "' or " +
        TABLE_ROW_TAG3 + " = '" + tag + "';",null);
        c.moveToFirst();

        //close the database
        db.close();

        return c;
    }

    //this method returns a whole column of a photo with a specific id
    public Cursor getPhoto (int id){

        Cursor c = db.rawQuery("SELECT * from " + TABLE_PHOTOS + " WHERE " + TABLE_ROW_ID + " = " + id,null);
        c.moveToFirst();

        //close the database
        db.close();

        return c;
    }

    //next method returns all tags, which is what we need for TagsFragment class
    public Cursor getTags (){

        Cursor c = db.rawQuery("SELECT " + TABLE_ROW_ID + ", " + TABLE_ROW_TAG + " from " + TABLE_TAGS,null);
        c.moveToFirst();

        //close the database
        db.close();

        return c;
    }

    //inner class CustomSQLiteOpenHelper
    //simple constructor, onCreate builds and executes two queries to create both of our required tables
    private class CustomSQLiteOpenHelper extends SQLiteOpenHelper {

        public CustomSQLiteOpenHelper(Context context){

            super(context,DB_NAME,null,DB_VERSION);
        }

        //this method only runs the first time the database is created
        //I think triggered when constructor could not access the dbs
        @Override
        public void onCreate (SQLiteDatabase db){

            //create a table for photos and all their details
            //When upgrading database, also add new code here to cater for new users who have never had a database before
            String newTableQueryString = "create table " + TABLE_PHOTOS + " (" +
                    TABLE_ROW_ID + " integer primary key autoincrement not null, " +
                    TABLE_ROW_TITLE + " text not null, " + TABLE_ROW_URI + " text not null, " +
                    TABLE_ROW_LOCATION_LAT + " real," + TABLE_ROW_LOCATION_LONG + " real," +
                    TABLE_ROW_TAG1 + " text not null, " + TABLE_ROW_TAG2 + " text not null, " +
                    TABLE_ROW_TAG3 + " text not null" + ");";

            db.execSQL(newTableQueryString);

            //create a separate table for tags
            newTableQueryString = "create table " + TABLE_TAGS + " (" + TABLE_ROW_ID + " integer primary key autoincrement not null,"
                    + TABLE_ROW_TAG + " text not null" + ");";

            db.execSQL(newTableQueryString);
        }

        //this method only runs when we increment DB_VERSION
        //Happens so for existing users
        @Override
        public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion){

            //Update for version 2
            //Query to add column for longitude
            String addLongColumn = "ALTER TABLE " + TABLE_PHOTOS + " ADD " + TABLE_ROW_LOCATION_LONG + " real;";

            //execute the query
            db.execSQL(addLongColumn);

            String addLatColumn = "ALTER TABLE " + TABLE_PHOTOS + " ADD " + TABLE_ROW_LOCATION_LAT + " real;";

            db.execSQL(addLatColumn);
        }


    }
}
