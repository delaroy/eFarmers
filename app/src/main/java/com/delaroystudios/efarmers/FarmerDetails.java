package com.delaroystudios.efarmers;

import android.content.Intent;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class FarmerDetails extends AppCompatActivity {
    @BindView(R.id.profile_image) CircleImageView profile_image;
    @BindView(R.id.name) TextView name;
    @BindView(R.id.age) TextView age;
    @BindView(R.id.years_farming) TextView years_farming;
    @BindView(R.id.type_farming) TextView type_farming;
    @BindView(R.id.farm_name) TextView farm_name;
    @BindView(R.id.farm_location) TextView farm_location;
    @BindView(R.id.toolbar) Toolbar toolbar;

    public static final String FARMER_ID = "farmerId";
    private static final int DEFAULT_FARMER_ID = -1;
    private int mFarmerId = DEFAULT_FARMER_ID;
    public static final String INSTANCE_FARMER_ID = "instanceFarmerId";
    private AppDatabase mDb;
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
    }
}
