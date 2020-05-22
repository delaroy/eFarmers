package com.delaroystudios.efarmers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.delaroystudios.efarmers.adapter.FarmersAdapter;
import com.delaroystudios.efarmers.database.AppDatabase;
import com.delaroystudios.efarmers.database.FarmersEntity;
import com.delaroystudios.efarmers.utils.PreferenceUtils;
import com.delaroystudios.efarmers.viewmodel.MainViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.delaroystudios.efarmers.FarmerDetails.FARMER_ID;

public class MainActivity extends AppCompatActivity implements FarmersAdapter.ItemClickListener{

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.farmers_number) TextView farmers_number;
    @BindView(R.id.no_record) LinearLayout no_record;
    @BindView(R.id.recycler_view) RecyclerView recycler_view;
    @BindView(R.id.floating_action_button) FloatingActionButton floating_action_button;
    private FarmersAdapter adapter;

    AppDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setTitle("Dashboard");
        
        db = AppDatabase.getInstance(this);

        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FarmersAdapter(this, this);
        recycler_view.setAdapter(adapter);
        recycler_view.addItemDecoration(new DividerItemDecoration(recycler_view.getContext(), DividerItemDecoration.VERTICAL));
        setupViewModel();

        floating_action_button.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    private void setupViewModel() {
        MainViewModel viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.getFarmers().observe(this, (List<FarmersEntity> farmersEntities) -> {
            int size = farmersEntities.size();
            if (size == 0) {
                no_record.setVisibility(View.VISIBLE);
            }
            farmers_number.setText(String.valueOf(size));
            if (farmersEntities != null) {
                adapter.setFarmer(farmersEntities);
            }
        });
    }

    @Override
    public void onItemClickListener(int itemId) {
        Intent intent = new Intent(getApplicationContext(), FarmerDetails.class);
        intent.putExtra(FARMER_ID, itemId);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logout) {
            PreferenceUtils.saveUsername("", getApplicationContext());
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
