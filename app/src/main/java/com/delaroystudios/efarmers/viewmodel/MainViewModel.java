package com.delaroystudios.efarmers.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.delaroystudios.efarmers.database.AppDatabase;
import com.delaroystudios.efarmers.database.FarmersEntity;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    // Constant for logging
    private static final String TAG = MainViewModel.class.getSimpleName();

    private LiveData<List<FarmersEntity>> entries;

    public MainViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the tasks from the DataBase");
        entries = database.farmersDao().loadAllFarmers();
    }

    public LiveData<List<FarmersEntity>> getFarmers() {
        return entries;
    }
}
