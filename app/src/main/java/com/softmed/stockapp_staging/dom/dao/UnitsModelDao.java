package com.softmed.stockapp_staging.dom.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.softmed.stockapp_staging.dom.entities.Unit;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;


@Dao
public interface UnitsModelDao {

    @Query("select * from Unit")
    List<Unit> getUnit();

    @Query("select * from Unit WHERE id=:unitId")
    Unit getUnit(int unitId);


    @Insert(onConflict = REPLACE)
    void addUnit(Unit unit);

    @Update
    void updateUnit(Unit unit);

    @Delete
    void deleteUnit(Unit unit);

}
