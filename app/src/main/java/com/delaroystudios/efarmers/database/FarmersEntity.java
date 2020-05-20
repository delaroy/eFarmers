package com.delaroystudios.efarmers.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class FarmersEntity {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "age")
    private String age;

    @ColumnInfo(name = "years")
    private String years;

    @ColumnInfo(name = "farm_name")
    private String farm_name;

    @ColumnInfo(name = "type_farming")
    private String type_farming;

    @ColumnInfo(name = "farm_address")
    private String farm_address;

    @ColumnInfo(name = "profile_img")
    private String profile_img;


    @Ignore
    public FarmersEntity(String name, String age, String years, String farm_name, String type_farming, String farm_address, String profile_img ) {
        this.name = name;
        this.age = age;
        this.years = years;
        this.farm_name = farm_name;
        this.type_farming = type_farming;
        this.farm_address = farm_address;
        this.profile_img = profile_img;
    }

    public FarmersEntity(int id, String name, String age, String years, String farm_name, String type_farming, String farm_address, String profile_img  ) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.years = years;
        this.farm_name = farm_name;
        this.type_farming = type_farming;
        this.farm_address = farm_address;
        this.profile_img = profile_img;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getYears() {
        return years;
    }

    public void setYears(String years) {
        this.years = years;
    }

    public String getType_farming() {
        return type_farming;
    }

    public void setType_farming(String type_farming) {
        this.type_farming = type_farming;
    }

    public String getFarm_address() {
        return farm_address;
    }

    public void setFarm_address(String farm_address) {
        this.farm_address = farm_address;
    }

    public String getFarm_name() {
        return farm_name;
    }

    public void setFarm_name(String farm_name) {
        this.farm_name = farm_name;
    }

    public String getProfile_img() {
        return profile_img;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img = profile_img;
    }
}
