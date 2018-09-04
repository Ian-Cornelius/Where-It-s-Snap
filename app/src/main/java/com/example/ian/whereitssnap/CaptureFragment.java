package com.example.ian.whereitssnap;

/**
 * Created by Ian on 12/30/2017.
 */
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.net.Uri;
import android.content.Intent;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.os.Environment;
import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.widget.Toast;
import android.location.LocationListener;
import android.location.Location;
import android.location.LocationManager;
import android.location.Criteria;

//Added for my own additions
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class CaptureFragment extends Fragment implements LocationListener{

    private static final int CAMERA_REQUEST = 123;
    private ImageView mImageView;

    //the file path for the photo
    String mCurrentPhotoPath;

    //where the captured image is stored
    private Uri mImageUri = Uri.EMPTY;

    //an instance for our DataManager
    private DataManager mDataManager;

    //For the location
    private Location mLocation = new Location("");
    private LocationManager mLocationManager;
    private String mProvider;

    /*
    PART OF MY ADDED CODE
     */
    ActivityComs activityComs;
    //ActivityComs initialized at onAttach

    //photoFile has class Activity scope, for easier access
    private File photoFile = null;

    //Initialize DataManager in the onCreate method
    @Override
    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mDataManager = new DataManager(getActivity().getApplicationContext());

        //Initialize mLocationManager
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        mProvider = mLocationManager.getBestProvider(criteria,false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState){

        //inflate the layout file and get all the necessary references
        View view = inflater.inflate(R.layout.fragment_capture,container,false);

        mImageView = view.findViewById(R.id.imageView);
        Button btnCapture = view.findViewById(R.id.btnCapture);
        Button btnSave = view.findViewById(R.id.btnSave);

        final EditText mEditTextTitle = view.findViewById(R.id.editTextTitle);
        final EditText mEditTextTag1 = view.findViewById(R.id.editTextTag1);
        final EditText mEditTextTag2 = view.findViewById(R.id.editTextTag2);
        final EditText mEditTextTag3 = view.findViewById(R.id.editTextTag3);

        //listen for clicks on the capture button
        btnCapture.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick (View v){

                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                //File photoFile = null; Put at top. To ease image loading problem through new code
                try{
                    //create an image file where the photo taken will be stored
                    //createImageFile is a user defined method. Find it below
                    photoFile = createImageFile();
                }catch (IOException ex){

                    //error occured while creating the file
                    Log.e("Error","Error creating imagefile");
                    Toast.makeText(getActivity(),"Error creating image file",Toast.LENGTH_LONG).show();
                    //have a problem with context issues. Not so sure however
                    //Toast.makeText(getContext(),"Error creating image file",Toast.LENGTH_LONG);
                }

                //continue only if file was successfully created
                if (photoFile != null){

                    //apparently this Uri points to the file object in memory. Physical memory
                    //We getting the Uri of the file we have just created
                    mImageUri = Uri.fromFile(photoFile);
                    //photo taken to be saved in that file
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(photoFile));
                    //Now starting the camera activity to take photos
                    startActivityForResult(cameraIntent,CAMERA_REQUEST);
                }
            }
        });

        //Now, button for saving image. Use it to write to our database
        //Listen to clicks on btnSave
        btnSave.setOnClickListener (new View.OnClickListener(){

            @Override
            public void onClick(View v){

                if (mImageUri != null){
                    if (!mImageUri.equals(Uri.EMPTY)){

                        //we have a photo to save.
                        //create a photo object. Save to it the location of the captured photo (URI), title and the tags description
                        Photo photo = new Photo();
                        photo.setTitle(mEditTextTitle.getText().toString());
                        photo.setStorageLocation(mImageUri);

                        //set the current GPS location
                        photo.setGpsLocation(mLocation);

                        //Check to confirm location is Okay
                        if (mLocation == null){
                            Toast.makeText(getActivity(),"Location not found",Toast.LENGTH_LONG).show();
                        }

                        //Extract the content/description in the tags edit text widgets
                        String tag1 = mEditTextTag1.getText().toString();
                        String tag2 = mEditTextTag2.getText().toString();
                        String tag3 = mEditTextTag3.getText().toString();

                        //Assign the strings to the photo object
                        photo.setTag1(tag1);
                        photo.setTag2(tag2);
                        photo.setTag3(tag3);

                        //send the new object to our datamanager, for saving
                        mDataManager.addPhoto(photo);
                        Toast.makeText(getActivity(),"Saved",Toast.LENGTH_LONG).show();

                        /*
                        A TRIAL CODE SECTION. IDEA IS TO TAKE US DIRECTLY TO VIEW FRAGMENT ONCE WE HAVE SAVED A PHOTO.
                         */

                        //Now, a trial to have it go to the view fragment directly
                        //That means, I communicate with the MainActivity to launch ViewFragment, after accessing the dbID
                        //of the recently saved photo from the database. So:
                        //I need an instance of datamanager and our interface, activitycoms

                        Cursor c = mDataManager.getTitles();
                        //move to the last title, which should be the title of the last photo we have taken
                        c.moveToLast();

                        //now get the id of the photo
                        int dbID = c.getInt(c.getColumnIndex(DataManager.TABLE_ROW_ID));
                        c.close();
                        activityComs.onTitlesListItemSelected(dbID);
                        //No need to replace fragments, as it is done by the function called above

                    }
                    else{
                        //No image
                        //Look at how context is represented by getActivity(), and it works
                        Toast.makeText(getActivity(),"No Image To Save",Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    //URI object was not initialized
                    Log.e("error","Uri is null");
                    Toast.makeText(getActivity(),"Uri is null",Toast.LENGTH_LONG).show();
                }
            }
        });
        
        return view;
    }//End of onCreateView

    //Overriding the necessary methods of LocationListener
    @Override
    public void onLocationChanged(Location location){
        //update the location if changed
        mLocation = location;
    }

    @Override
    public void onStatusChanged(String provider,int status, Bundle extras){

    }

    @Override
    public void onProviderEnabled(String provider){

    }

    @Override
    public void onProviderDisabled(String provider){

    }

    //Start updates when app starts or resumes
    @Override
    public void onResume(){
        super.onResume();

        //get location updates
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,100,1,this);
    }

    //Pause the location manager when app is paused or stopped
    @Override
    public void onPause(){
        super.onPause();

        //stop getting location updates
        mLocationManager.removeUpdates(this);
    }

    //now, the createImageFile method
    private File createImageFile() throws IOException{

        //create an imagefile name, using the simple date format
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,//the filename
                ".jpg",//the extension
                storageDir//the folder
                 );
        //save for use with ACTION_VIEW intent
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;//return the image file we have created.
    }

    //onActivityResult method to take image captured and put it in imageView
    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data){

        //check that we are retrieving the right data and that it actually exists. Slight cahnge here. RESULT_OK used is
        //static variable of activity class.
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK){

            try{

                //mImageView.setImageURI(Uri.parse(mImageUri.toString()));//whatever the hell is happening here.
                //Now, a break down of what's going on here, the Uri is being set on the image view widget, but since it
                //is an object, it is parsed into a string, using a static public function of the Uri class.

                //Own code. Using custom code to load sampled Bitmap image, for slower devices, and RAM limited devices.

                mImageView.setImageBitmap(decodeSampledBitmapFromFile(mImageView.getWidth(),mImageView.getHeight()));
                //370dp and 370dp are the dimensions of our image view widget
                //alternatively used methods native to the widget to get the values

                //Not used bitmap as in the simple photo app. Still puts the photo though. probably by getting it from file
            }catch (Exception e){
                Log.e("Error","Uri not set");
                Toast.makeText(getActivity(),"Uri not set",Toast.LENGTH_LONG).show();
            }
        }else{
            mImageUri = Uri.EMPTY;
            Log.e("Error","Got no data from Camera");
            Toast.makeText(getActivity(),"Got no data from Camera",Toast.LENGTH_LONG).show();
        }
    }

    //override onDestroy so that our app does not run out of memory
    //interestingly, we do not need to write @Override
    public void onDestroy(){
        super.onDestroy();

        //make sure we don't run out of memory
        BitmapDrawable bd = (BitmapDrawable) mImageView.getDrawable();
        bd.getBitmap().recycle();
        mImageView.setImageBitmap(null);

        //Null pointer reference issues. Slow bitmap load. Eating lots of memory. Will fix later.

    }

    //the other two classes didn't have onCreateView method because they have a ListView built in that will have its functionality
    //and appearance handled by an array adapter later in the project.
    //problem with this code is that back camera in some instances fails to load images. Say's bitmap image too big. Other times,
    //just doesn't load.

    /*
    PART OF MY ADDED PIECE OF CODE
     */
    public void onAttach(Activity activity){
        super.onAttach(activity);

        //now initialize our instance of our interface
        activityComs = (ActivityComs) activity;
    }

    public void onDetach(){
        super.onDetach();

        activityComs = null;
    }

    //Methods to assist in loading bitmaps
    //It gets the appropriate sample size to use
    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight){

        //get the raw width and height that was obtained before calling this method, when we set options.inDecodeBounds to true
        final int height = options.outHeight;
        final int width = options.outWidth;
        //sample size default value. Will calculate required size shortly.
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth){

            //This means the image to be loaded is larger than the widget.
            final int halfHeight = height/2;
            final int halfWidth = width/2;

            //We half coz samplesize in options.inSampleSize will be rounded down to nearest power of two
            //In effect, we double the initial inSampleSize value
            //Do this using the while loop

            //What if it passes with a sample size of 1 yet we had halfed the dimensions before? Don't really know. Below is just a guess
            //Well, the default value of 1, in the options object will be rounded down to two (the nearest power of 2). Or so I think;
            while ((halfHeight/inSampleSize) >= reqHeight && (halfWidth/inSampleSize) >= reqWidth){
                //multiply inSampleSize by 2
                //This is because the sample size does not yet drop us to the required height and width of our imageview widget
                inSampleSize*= 2;
            }
        }

        return inSampleSize;
    }

    //Now, the method to load the whole image
    private Bitmap decodeSampledBitmapFromFile(int reqWidth, int reqHeight){

        //First, decode with inJustDecodeBounds set to true, to just get the size of the image
        final BitmapFactory.Options options = new BitmapFactory.Options();
        //to get the height and width of image we want to load
        options.inJustDecodeBounds = true;
        //decode the file. Remember it is a member of the Activity class, so we can just get it directly
        BitmapFactory.decodeFile(photoFile.getAbsolutePath(),options);

        //calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options,reqWidth,reqHeight);

        //now decode the Bitmap with inSampleSize set
        //inJustDecodeBounds has to be set to false so that a bitmap object is returned
        options.inJustDecodeBounds = false;
        //return our final, sampled bitmap
        return BitmapFactory.decodeFile(photoFile.getAbsolutePath(),options);

    }
}
