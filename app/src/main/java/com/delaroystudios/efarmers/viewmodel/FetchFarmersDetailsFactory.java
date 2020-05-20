package com.delaroystudios.efarmers.viewmodel;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.delaroystudios.efarmers.database.AppDatabase;

public class FetchFarmersDetailsFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase mDb;
    private final int mId;

    public FetchFarmersDetailsFactory(AppDatabase database, int id) {
        mDb = database;
        this.mId = id;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new FetchFarmersDetails(mDb, mId);
    }
}