package com.delaroystudios.efarmers;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.delaroystudios.efarmers.database.AppDatabase;
import com.delaroystudios.efarmers.database.FarmersEntity;
import com.delaroystudios.efarmers.viewmodel.AppExecutors;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements OnMapReadyCallback {
    @BindView(R.id.profile_image) CircleImageView profile_image;
    @BindView(R.id.name) TextInputLayout name;
    @BindView(R.id.input_name) TextInputEditText input_name;
    @BindView(R.id.age) TextInputLayout age;
    @BindView(R.id.input_age) TextInputEditText input_age;
    @BindView(R.id.years_farming) TextInputLayout years_farming;
    @BindView(R.id.input_years_farming) TextInputEditText input_years_farming;
    @BindView(R.id.farm_name) TextInputLayout farm_name;
    @BindView(R.id.input_farm_name) TextInputEditText input_farm_name;
    @BindView(R.id.type_farming) TextInputLayout type_farming;
    @BindView(R.id.input_type_farming) TextInputEditText input_type_farming;
    @BindView(R.id.farm_address) TextInputLayout farm_address;
    @BindView(R.id.input_farm_location) TextInputEditText input_farm_location;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.progress)ProgressBar progress;

    private GoogleMap mMap;
    private static final int REQUEST_TAKE_PHOTO = 0;
    private static final int REQUEST_PICK_PHOTO = 2;
    private static final int CAMERA_PIC_REQUEST = 1111;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private Uri fileUri;
    private String mediaPath;
    private String mImageFileLocation = "";
    public static final String IMAGE_DIRECTORY_NAME = "Android File Upload";
    private String postPath;
    private static final String TAG = ProfileActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private String path;
    private static final String POST_PATH = "post_path";
    AppDatabase db;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setTitle("Create Profile");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        profile_image.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
                    ActivityCompat.requestPermissions(ProfileActivity.this, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
                } else {
                    launchImagePicker();
                }
            } else {
                launchImagePicker();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        db = AppDatabase.getInstance(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchImagePicker();
            }else{
                Toast.makeText(ProfileActivity.this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void launchImagePicker(){
        new MaterialDialog.Builder(this)
                .title(R.string.uploadImages)
                .items(R.array.uploadImages)
                .itemsIds(R.array.itemIds)
                .itemsCallback((dialog, view, which, text) -> {
                    switch (which) {
                        case 0:
                            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(galleryIntent, REQUEST_PICK_PHOTO);
                            break;
                        case 1:
                            captureImage();
                            break;
                        case 2:
                            profile_image.setImageResource(R.drawable.agromall);
                            postPath.equals(null);
                            path.equals(null);
                            refreshActivity();
                            break;
                    }
                })
                .show();
    }

    /**
     * Launching camera app to capture image
     */
    private void captureImage() {
        if (Build.VERSION.SDK_INT > 21) { //use this if Lollipop_Mr1 (API 22) or above
            Intent callCameraApplicationIntent = new Intent();
            callCameraApplicationIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

            // We give some instruction to the intent to save the image
            File photoFile = null;

            try {
                // If the createImageFile will be successful, the photo file will have the address of the file
                photoFile = createImageFile();
                // Here we call the function that will try to catch the exception made by the throw function
            } catch (IOException e) {
                Logger.getAnonymousLogger().info("Exception error in generating the file");
                e.printStackTrace();
            }
            // Here we add an extra file to the intent to put the address on to. For this purpose we use the FileProvider, declared in the AndroidManifest.
            Uri outputUri = FileProvider.getUriForFile(
                    this,
                    "com.delaroystudios.efarmers" + ".provider",
                    photoFile);
            callCameraApplicationIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);

            // The following is a new line with a trying attempt
            callCameraApplicationIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

            Logger.getAnonymousLogger().info("Calling the camera App by intent");

            // The following strings calls the camera app and wait for his file in return.
            startActivityForResult(callCameraApplicationIntent, CAMERA_PIC_REQUEST);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

            // start the image capture Intent
            startActivityForResult(intent, CAMERA_PIC_REQUEST);
        }
    }

    File createImageFile() throws IOException {
        Logger.getAnonymousLogger().info("Generating the image - method started");

        // Here we create a "non-collision file name", alternatively said, "an unique filename" using the "timeStamp" functionality
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmSS").format(new Date());
        String imageFileName = "IMAGE_" + timeStamp;
        // Here we specify the environment location and the exact path where we want to save the so-created file
        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/photo_saving_app");
        Logger.getAnonymousLogger().info("Storage directory set");

        // Then we create the storage directory if does not exists
        if (!storageDirectory.exists()) storageDirectory.mkdir();

        // Here we create the file using a prefix, a suffix and a directory
        File image = new File(storageDirectory, imageFileName + ".jpg");
        // Here the location is saved into the string mImageFileLocation
        Logger.getAnonymousLogger().info("File name and path set");

        mImageFileLocation = image.getAbsolutePath();
        fileUri = Uri.parse(mImageFileLocation);
        // The file is returned to the previous intent across the camera application
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_PHOTO || requestCode == REQUEST_PICK_PHOTO) {
                if (data != null) {
                    // Get the Image from data
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    assert cursor != null;
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    mediaPath = cursor.getString(columnIndex);
                    // Set the Image in ImageView for Previewing the Media
                    profile_image.setImageBitmap(BitmapFactory.decodeFile(mediaPath));
                    cursor.close();

                    postPath = mediaPath;
                    path = mediaPath;
                }

            }else if (requestCode == CAMERA_PIC_REQUEST){
                if (Build.VERSION.SDK_INT > 21) {
                    Glide.with(this).load(mImageFileLocation).into(profile_image);
                    postPath = mImageFileLocation;
                    path = postPath;
                }else{
                    Glide.with(this).load(fileUri).into(profile_image);
                    postPath = fileUri.getPath();
                    path = postPath;
                }
            }
        }
        else if (resultCode != RESULT_CANCELED) {
            Toast.makeText(this, R.string.sorry_error, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putString(POST_PATH, path);
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        path = savedInstanceState.getString(POST_PATH);
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + ".jpg");
        }  else {
            return null;
        }

        return mediaFile;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.submit) {
            if (postPath == null) {
                Toast.makeText(this, R.string.select_image, Toast.LENGTH_SHORT).show();
            } else {
                verifyData();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void verifyData() {
        name.setError(null);
        age.setError(null);
        farm_address.setError(null);
        type_farming.setError(null);
        years_farming.setError(null);
        farm_name.setError(null);

        if (input_name.length() == 0) {
            name.setError(getString(R.string.error_name));
        } else if (input_age.length() == 0) {
            age.setError(getString(R.string.error_age));
        } else if(input_farm_name.length() == 0) {
            farm_name.setError(getString(R.string.error_farm_name));
        } else if (input_years_farming.length() == 0) {
            years_farming.setError(getString(R.string.error_years_farming));
        } else if (input_type_farming.length() == 0) {
            type_farming.setError(getString(R.string.error_type_farming));
        } else if (input_farm_location.length() == 0) {
            farm_address.setError(getString(R.string.error_address));
        } else {
            showProgress();
            String name = input_name.getText().toString().trim();
            String age = input_age.getText().toString().trim();
            String years = input_years_farming.getText().toString().trim();
            String farm_name = input_farm_name.getText().toString().trim();
            String type_farming = input_type_farming.getText().toString().trim();
            String farm_location = input_farm_location.getText().toString().trim();

            saveRecord(name, age, years, farm_name, type_farming, farm_location);
        }

    }

    private void saveRecord(String name, String age, String years, String farm_name, String type_farming, String farm_location) {
        AppExecutors.getInstance().diskIO().execute(() -> db.farmersDao().insertFarmers(new FarmersEntity(name, age, years, farm_name, type_farming, farm_location, postPath)));
        emptyInputEditText();
        hideProgress();
        Toast.makeText(this, "Successfully added Farmers record to database", Toast.LENGTH_SHORT).show();
    }

    /**
     * This method is to empty all input edit text
     */
    private void emptyInputEditText() {
        input_name.setText("");
        input_age.setText("");
        input_farm_location.setText("");
        input_type_farming.setText("");
        input_years_farming.setText("");
        input_farm_name.setText("");
        profile_image.setImageResource(R.drawable.agromall);
        postPath.equals(null);
        path.equals(null);
        refreshActivity();
    }

    private void showProgress() {
        progress.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progress.setVisibility(View.GONE);
    }

    private void refreshActivity(){
        recreate();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
