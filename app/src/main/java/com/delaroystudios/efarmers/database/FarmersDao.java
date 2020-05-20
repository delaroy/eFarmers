package com.delaroystudios.efarmers.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FarmersDao {

    @Query("SELECT * FROM farmersentity")
    LiveData<List<FarmersEntity>> loadAllFarmers();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFarmers(FarmersEntity farmersEntity);

    @Query("SELECT * FROM farmersentity WHERE id = :id")
    LiveData<FarmersEntity> loadFarmersById(int id);

    @Query("DELETE FROM farmersentity")
    void deleteAllFarmers();

    @Query("DELETE FROM farmersentity WHERE id = :id")
    void deletFarmer(int id);
}
