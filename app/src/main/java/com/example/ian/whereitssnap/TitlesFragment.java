package com.example.ian.whereitssnap;

/**
 * Created by Ian on 12/30/2017.
 */
import android.app.ListFragment;
import android.os.Bundle;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;
import android.widget.ListView;
import android.view.View;
import android.app.Activity;

public class TitlesFragment extends ListFragment {

    private Cursor mCursor;
    private ActivityComs mActivityComs; //initialized in onAttach methods, by simply typecasting instance of
    // Activity into activitycoms. This to avoid null pointer exception error. Set to null onDetach

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //get the tag whose titles to show, if it has been passed in Bundle instance
        String tag = getArguments().getString("Tag");

        //get an instance of dataManager, to do a db query
        DataManager d = new DataManager (getActivity().getApplicationContext());

        //do a query based on whether a tag has been provided or not
        if (tag == "_NO_TAG"){

            //get all the titles from the database
            mCursor = d.getTitles();
        }
        else{

            //get titles under the given tag
            mCursor = d.getTitlesWithTag(tag);
        }

        //create new adapter. Supposed to handle the cursor data, put it in a list
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(getActivity(),android.R.layout.simple_list_item_1,mCursor,
                new String[] {DataManager.TABLE_ROW_TITLE},new int[]{android.R.id.text1},0);
        /*
        Arguments I know of so far: first one is the context, second, default layout where it will be shown, third where the data is,
        four - an array holding the columnn whose data is to be used, fifth is I think how to identify the data
         */

        //now set it as our adapter
        setListAdapter(cursorAdapter);
    }

    //onListItemClick method to handle clicks on the list of titles
    public void onListItemClick (ListView l, View v, int position, long id){

        //move the cursor to the clicked item in the list
        mCursor.moveToPosition(position);

        //get the database id of this item
        int dBID = mCursor.getInt(mCursor.getColumnIndex(DataManager.TABLE_ROW_ID));

        //use the interface to send the id
        mActivityComs.onTitlesListItemSelected(dBID);
    }

    /*
    I think onAttach is for when attaching to the fragment Holder/layout, and onDetach is for when
    detaching from the fragment holder/layout
     */
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

        //initialize ActivityComs instance
        mActivityComs = (ActivityComs) activity;
    }

    @Override
    public void onDetach(){
        super.onDetach();

        //set the instance to null
        mActivityComs = null;
    }
}
