package com.softmed.stockapp.Dom.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.softmed.stockapp.Dom.entities.Location;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;


@Dao
public interface LocationsModelDao {

    @Query("select * from Location")
    LiveData<List<Location>> getLocations();


    @Insert(onConflict = REPLACE)
    void addLocation(Location location);

    @Update
    void updateLocation(Location location);

    @Delete
    void deleteLocation(Location location);

}
