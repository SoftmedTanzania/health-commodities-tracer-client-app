package com.softmed.stockapp.Dom.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.softmed.stockapp.Dom.entities.Location;
import com.softmed.stockapp.Dom.entities.TransactionType;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface LocationModelDao {

    @Query("select * from Location")
    List<Location> getAllLocations();

    @Query("select * from Location WHERE id=:id")
    Location getLocationById(int id);

    @Query("select *  from Location WHERE parentId=:id")
    List<Location> getLocationsByParentId(int id);


    @Insert(onConflict = REPLACE)
    void addLocation(Location location);

    @Update
    void updateLocation(Location location);

    @Delete
    void deleteLocation(Location location);

}
