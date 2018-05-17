package com.softmed.rucodia.dom.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.softmed.rucodia.dom.objects.Unit;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;


@Dao
public interface UnitsModelDao {

    @Query("select * from Unit")
    LiveData<List<Unit>> getUnit();

    @Query("select * from Unit WHERE id=:unitId")
    Unit getUnit(int unitId);


    @Insert(onConflict = REPLACE)
    void addUnit(Unit unit);

    @Update
    void updateUnit(Unit unit);

    @Delete
    void deleteUnit(Unit unit);

}
