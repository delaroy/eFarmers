package com.delaroystudios.efarmers.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.delaroystudios.efarmers.database.AppDatabase;
import com.delaroystudios.efarmers.database.FarmersEntity;

public class FetchFarmersDetails extends ViewModel {
    private LiveData<FarmersEntity> details;

    public FetchFarmersDetails(AppDatabase database, int id) {
        details = database.farmersDao().loadFarmersById(id);
    }

    public LiveData<FarmersEntity> ggetFarmers() {
        return details;
    }
}

