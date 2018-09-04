package com.example.ian.whereitssnap;

/**
 * Created by Ian on 12/30/2017.
 */
import android.app.ActionBar;
import android.app.ListFragment;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;
import android.app.Activity;
import android.database.Cursor;
import android.view.View;
import android.widget.ListView;
import android.util.Log;

public class TagsFragment extends ListFragment {

    //instance of our interface
    private ActivityComs mActivityComs;

    @Override
    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //DataManager instance for us to access the database
        //need to get application context for us to access the correct db
        DataManager d = new DataManager(getActivity().getApplicationContext());

        //now get the tags
        Cursor c = d.getTags();

        //create a new adapter to show this data in a list
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(getActivity(),android.R.layout.simple_list_item_1,c,
                new String[] {DataManager.TABLE_ROW_TAG}, new int[]{android.R.id.text1},0);

        //Attach the cursor to the adapter
        setListAdapter(cursorAdapter);
    }

    //handling clicks on the adapter
    public void onListItemClick (ListView l, View v, int position, long id){

        //find which tag has been clicked.
        /*
        What we do here is that we get the adapter of the listview, typecast it to
        the SimpleCursorAdapter, then retrieve the cursor object associated with the data
         */
        Cursor c = ((SimpleCursorAdapter)l.getAdapter()).getCursor();

        //Move to the appropriate position of the tag clicked
        c.moveToPosition(position);

        //get the tag name in its string format
        String clickedTag = c.getString(1);
        //where 1 is the column index/position of the string
        Log.e("clickedTag = ", " " + clickedTag);

        //now, call the appropriate method in the interface
        mActivityComs.onTagsListItemSelected(clickedTag);
    }

    //creation and destruction of our ActivityComs instance (initialization and de-initialization.
    @Override
    public void onAttach (Activity activity){
        super.onAttach(activity);

        //Now initialize
        mActivityComs = (ActivityComs) activity;
    }

    @Override
    public void onDetach(){
        super.onDetach();

        //de-initialize/set to null
        mActivityComs = null;
    }
}
