package com.example.ian.whereitssnap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ArrayAdapter;

public class MainActivity extends AppCompatActivity implements ActivityComs {

    private ListView mNavDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;

    //our datamanager instance
    public DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize the datamanager instance. No need to getActivity as this is the main activity.
        //I think we used getActivity as the rest were fragments, not activities
        dataManager = new DataManager(getApplicationContext());

        // We will come back here in a minute!
        mNavDrawerList = (ListView) findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mActivityTitle = getTitle().toString();

        //we will finish off this method next from here


        //initialize an array with our titles from strings.xml
        String[] navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        //initialize our array adapter
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, navMenuTitles);

        //set the adapter to the list view
        mNavDrawerList.setAdapter(mAdapter);
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        //The code above enables the drawer control we set up in the action bar

        mNavDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int whichItem, long id) {

                switchFragment(whichItem);
            }
        });

        switchFragment(0);
    }

    //A method called switchFragment that will help in switching between fragments when the user taps on an option in the navigation
    //drawer.
    private void switchFragment(int position) {

        //declare a Fragment object and set it to null. Will be initialized to appropriate class of Fragment as per user selection
        Fragment fragment = null;

        //string holding fragment ID, to identify each fragment currently showing or selected
        String fragmentID = "";

        //switch case to determine what has been tapped and initialize it
        switch (position) {
            case 0:
                fragmentID = "TITLES";
                Bundle args = new Bundle();
                args.putString("Tag", "_NO_TAG");

                //initialize fragment, to TitlesFragment type
                fragment = new TitlesFragment();
                fragment.setArguments(args);
                break;

            case 1:
                fragmentID = "TAGS";

                //initialize fragment to TagsFragment type
                fragment = new TagsFragment();
                break;

            case 2:
                fragmentID = "CAPTURE";

                //initialize fragment to CaptureFragment type
                fragment = new CaptureFragment();
                break;
            default:
                break;
        }

        //Now, to handle the replacement of existing fragment with new fragmet, sliding back the navigation drawer to
        //show the full fragment
        FragmentManager fragmentManager = getFragmentManager();
        //getFragmentManager therefore means FragmentManager is a singleton?

        fragmentManager.beginTransaction().replace(R.id.fragmentHolder, fragment, fragmentID).commit();
        //The above code seems to replace whatever fragment is in fragmentHolder (UI that holds our fragmetns), puts in the
        //new fragment and its tag in fragmentID, then commits it (shows it).
        //not seeing where we are using args we put in bundle for the tags selection

        //close the drawer
        mDrawerLayout.closeDrawer(mNavDrawerList);
    }

    //method that will help us set up the navigation drawer
    private void setupDrawer() {

        //initialize mDrawerToggle
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            //called when the drawer is opened
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                getSupportActionBar().setTitle("Make Selection");

                //triggers call to onPrepareOptionsMenu
                invalidateOptionsMenu();
            }

            //called when drawer close
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);

                getSupportActionBar().setTitle(mActivityTitle);

                //triggers call to onPrepareOptionsMenu
                invalidateOptionsMenu();
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {

        //close the drawer if open
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {

            //drawer is open so close it
            mDrawerLayout.closeDrawer(mNavDrawerList);
        } else {

            //go back to title's fragment. Quit if already in titles fragment
            Fragment f = getFragmentManager().findFragmentById(R.id.fragmentHolder);

            if (f instanceof TitlesFragment) {
                finish();
                System.exit(0);
            } else {
                switchFragment(0);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        /*//no inspection simplifiableIfStatement
        if (id == R.id.action_settings){
            return true;
        }*/

        //activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //implementing the onTagsListItemSelected method
    public void onTagsListItemSelected(String clickedTag) {

        //we have just received a string from the titles fragment. Put it in Bundle and send it over to the title's fragment,
        //who has already been coded on how to use it

        //prepare a new bundle
        Bundle args = new Bundle();

        //pack the string into the bundle
        args.putString("Tag", clickedTag);

        //create a new instance of TitlesFragment and send over the data. Just constructor invoked here.
        TitlesFragment fragment = new TitlesFragment();

        //load the bundle
        fragment.setArguments(args);

        //start the fragment. Already has bundle set up so OK.
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragmentHolder, fragment, "TAGS").commit();
        /*
        Arguments for replace are in the logic of, the fragmentholder UI/layout where we are going to do the replacement,
        the new fragment to be put there and the tag of the fragment. The tag of the fragment is more of its ID.

        When this is invoked, that's when onCreate starts I presume. Logically fits the presumption.
         */

        //update selected item and title then close the drawer. Don't understand the 1 and true
        mNavDrawerList.setItemChecked(1, true);
        mNavDrawerList.setSelection(1);
        mDrawerLayout.closeDrawer(mNavDrawerList);
    }

    //onTitlesListItemSelected will invoke View Fragment, to open selected photo
    public void onTitlesListItemSelected(int position) {

        //load up the bundle with row id
        //Bundle helps us pass data between classes and activities. All this is gotten in the onCreate section
        Bundle args = new Bundle();
        args.putInt("Position", position);

        //create the fragment and add the bundle
        ViewFragment fragment = new ViewFragment();
        fragment.setArguments(args);

        //start the fragment
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragmentHolder, fragment, "VIEW").commit();

            //update selected item and title then close drawer
            mNavDrawerList.setItemChecked(1, true);
            mNavDrawerList.setSelection(1);
            //Don't know why its commented. Proly cause of Array
            //setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mNavDrawerList);
        } else {
            //fragment instance not created
            Log.e("MainActivity", "Error in creating fragment");
        }
    }
}
