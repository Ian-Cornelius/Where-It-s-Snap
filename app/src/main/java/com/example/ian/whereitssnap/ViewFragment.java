package com.example.ian.whereitssnap;

import android.app.Fragment;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;
import android.os.Bundle;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import android.net.Uri;
import java.util.Locale;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Ian on 3/21/2018.
 */

public class ViewFragment extends Fragment {

    private Cursor mCursor;
    private ImageView mImageView;

    @Override
    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //Where is the photo object we want to show?
        //load it from the Bundle instance passed
        int position = getArguments().getInt("Position");

        //Load the appropriate photo from db, using the position int value
        //First, instantiate the DataManager Class
        DataManager d = new DataManager(getActivity().getApplicationContext());
        //now retrieve the photo, in a cursor object
        mCursor = d.getPhoto(position); //this position corresponds to the id
    }

    //onCreateView method for creating our view of the fragment. Usually called internally in the Fragment class
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_view,container,false);

        //Get references to our widgets
        TextView textView = view.findViewById(R.id.textView);
        Button buttonShowLocation = view.findViewById(R.id.buttonShowLocation);

        //set the text from the title column of the data
        textView.setText(mCursor.getString(mCursor.getColumnIndex(DataManager.TABLE_ROW_TITLE)));

        mImageView = view.findViewById(R.id.imageView);

        //load the image into the imageView widget via the URI
        mImageView.setImageURI(Uri.parse(mCursor.getString(mCursor.getColumnIndex(DataManager.TABLE_ROW_URI))));

        //Now, handling the show map button
        buttonShowLocation.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick (View v){

                //Code to show error message if trying to show map with no coordinates
                if (mCursor.getString(mCursor.getColumnIndex(DataManager.TABLE_ROW_LOCATION_LONG)) == null ||
                        mCursor.getString(mCursor.getColumnIndex(DataManager.TABLE_ROW_LOCATION_LAT)) == null){
                    Toast.makeText(getActivity(),"Cannot show map. Location was not set", Toast.LENGTH_LONG).show();
                }else{

                    double latitude = Double.valueOf(mCursor.getString(mCursor.getColumnIndex(DataManager.TABLE_ROW_LOCATION_LAT)));
                    double longitude = Double.valueOf(mCursor.getString(mCursor.getColumnIndex(DataManager.TABLE_ROW_LOCATION_LONG)));

                    //create an URI from the latitude and the longitude
                    String uri = String.format(Locale.ENGLISH,"geo:%f,%f",latitude,longitude);

                    //create google maps intent
                    Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(uri));

                    //start maps activity
                    getActivity().startActivity(intent);
                }

            }
        });

        //close sqlite db object
        //mCursor.close();

        return view;
    }

    //onDestroy method to help us free RAM once the user exits the viewFragment. Removes image from memory and sets resource value
    //of imageview widget to null. It is being overridden. That is why we are calling super.onDestroy (the original superclass version)
    //but not explicitly writing @Override at the top

    /*
    I think onDestroy is called to destroy the fragment. We are using it here for memory management manenos.
     */

    public void onDestroy(){

        super.onDestroy();

        //make sure we don't run out of memory
        BitmapDrawable bd = (BitmapDrawable) mImageView.getDrawable();

        //I think the resources are usually bitmap images.
        /*
        So here, we get the resource, (the bitmap image), recycle it, which throws it out of ram, and the last line of
        code sets the new bitmap resource to null, as we required.
         */
        bd.getBitmap().recycle();
        mImageView.setImageBitmap(null);
        mCursor.close();
    }
}