package com.delaroystudios.efarmers;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.delaroystudios.efarmers.database.AppDatabase;
import com.delaroystudios.efarmers.database.FarmersEntity;
import com.delaroystudios.efarmers.viewmodel.FetchFarmersDetails;
import com.delaroystudios.efarmers.viewmodel.FetchFarmersDetailsFactory;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class FarmerDetails extends AppCompatActivity implements OnMapReadyCallback {
    @BindView(R.id.profile_image) CircleImageView profile_image;
    @BindView(R.id.name) TextView name;
    @BindView(R.id.age) TextView age;
    @BindView(R.id.years_farming) TextView years_farming;
    @BindView(R.id.type_farming) TextView type_farming;
    @BindView(R.id.farm_name) TextView farm_name;
    @BindView(R.id.farm_location) TextView farm_location;
    @BindView(R.id.toolbar) Toolbar toolbar;
    private LatLng latLng;

    public static final String FARMER_ID = "farmerId";
    private static final int DEFAULT_FARMER_ID = -1;
    private int mFarmerId = DEFAULT_FARMER_ID;
    public static final String INSTANCE_FARMER_ID = "instanceFarmerId";
    private AppDatabase mDb;

    private static final int COLOR_BLACK_ARGB = 0xff000000;
    private static final int COLOR_WHITE_ARGB = 0xffffffff;
    private static final int COLOR_GREEN_ARGB = 0xff388E3C;
    private static final int COLOR_PURPLE_ARGB = 0xff81C784;
    private static final int COLOR_ORANGE_ARGB = 0xffF57F17;
    private static final int COLOR_BLUE_ARGB = 0xffF9A825;

    private static final int POLYGON_STROKE_WIDTH_PX = 8;
    private static final int PATTERN_DASH_LENGTH_PX = 20;
    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final PatternItem DOT = new Dot();
    private static final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);

    // Create a stroke pattern of a gap followed by a dash.
    private static final List<PatternItem> PATTERN_POLYGON_ALPHA = Arrays.asList(GAP, DASH);

    // Create a stroke pattern of a dot followed by a gap, a dash, and another gap.
    private static final List<PatternItem> PATTERN_POLYGON_BETA =
            Arrays.asList(DOT, GAP, DASH, GAP);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_details);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        mDb = AppDatabase.getInstance(getApplicationContext());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //intent data passed and extracting record details
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(FARMER_ID)) {
            // populate the UI
            mFarmerId = intent.getIntExtra(FARMER_ID, DEFAULT_FARMER_ID);

            FetchFarmersDetailsFactory factory = new FetchFarmersDetailsFactory(mDb, mFarmerId);
            final FetchFarmersDetails viewModel
                    = new ViewModelProvider(this, factory).get(FetchFarmersDetails.class);
            viewModel.getFarmers().observe(this, new Observer<FarmersEntity>() {
                @Override
                public void onChanged(@Nullable FarmersEntity farmersEntity) {
                    viewModel.getFarmers().removeObserver(this);
                    if (farmersEntity != null) {
                        populateUI(farmersEntity);
                    }
                }
            });
        }
    }

    private void populateUI(FarmersEntity farmersEntity) {
        String m_name = farmersEntity.getName();
        String m_age = farmersEntity.getAge();
        String m_years = farmersEntity.getYears();
        String m_type_farming = farmersEntity.getType_farming();
        String m_farm_name = farmersEntity.getFarm_name();
        String m_address  = farmersEntity.getFarm_address();
        String profile_img = farmersEntity.getProfile_img();

        latLng = getLocationFromAddress(this, m_address);

        setTitle(m_name);
        name.setText(m_name);
        age.setText(m_age);
        years_farming.setText(m_years);
        type_farming.setText(m_type_farming);
        farm_name.setText(m_farm_name);
        farm_location.setText(m_address);

        Glide.with(this)
                .load(profile_img)
                .into(profile_image);

        //initialize google map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (latLng != null) {
            Polygon polygon = googleMap.addPolygon(new PolygonOptions()
                    .add(new LatLng(latLng.latitude, latLng.longitude),
                            new LatLng(latLng.latitude + .002, latLng.longitude),
                            new LatLng(latLng.latitude + .002, latLng.longitude + .002),
                            new LatLng(latLng.latitude, latLng.longitude + .002),
                            new LatLng(latLng.latitude, latLng.longitude))
            );
            polygon.setTag("alpha");
            stylePolygon(polygon);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
        }
    }

    private void stylePolygon(Polygon polygon) {
        String type = "";
        // Get the data object stored with the polygon.
        if (polygon.getTag() != null) {
            type = polygon.getTag().toString();
        }

        List<PatternItem> pattern = null;
        int strokeColor = COLOR_BLACK_ARGB;
        int fillColor = COLOR_WHITE_ARGB;

        switch (type) {
            // If no type is given, allow the API to use the default.
            case "alpha":
                // Apply a stroke pattern to render a dashed line, and define colors.
                pattern = PATTERN_POLYGON_ALPHA;
                strokeColor = COLOR_GREEN_ARGB;
                fillColor = COLOR_PURPLE_ARGB;
                break;
            case "beta":
                // Apply a stroke pattern to render a line of dots and dashes, and define colors.
                pattern = PATTERN_POLYGON_BETA;
                strokeColor = COLOR_ORANGE_ARGB;
                fillColor = COLOR_BLUE_ARGB;
                break;
        }

        polygon.setStrokePattern(pattern);
        polygon.setStrokeWidth(POLYGON_STROKE_WIDTH_PX);
        polygon.setStrokeColor(strokeColor);
        polygon.setFillColor(fillColor);
    }

    //get latlng coordinates from street address
    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }

}
