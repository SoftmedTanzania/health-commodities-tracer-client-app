package com.softmed.stockapp_staging.dom.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.softmed.stockapp_staging.dom.entities.OtherUsers;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;


@Dao
public interface UsersModelDao {

    @Query("select * from OtherUsers WHERE id!=:excludingId ")
    LiveData<List<OtherUsers>> getUsers(int excludingId);

    @Query("select * from OtherUsers WHERE id = :id ")
    OtherUsers getUser(int id);


    @Insert(onConflict = REPLACE)
    void addUser(OtherUsers user);

    @Update
    void UpdateUser(OtherUsers user);

    @Delete
    void deleteUser(OtherUsers user);

}
