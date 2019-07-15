package com.softmed.stockapp.dom.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.softmed.stockapp.dom.entities.OtherUsers;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;


@Dao
public interface UsersModelDao {

    @Query("select * from OtherUsers ")
    List<OtherUsers> getUsers();


    @Insert(onConflict = REPLACE)
    void addUser(OtherUsers user);

    @Update
    void UpdateUser(OtherUsers user);

    @Delete
    void deleteUser(OtherUsers user);

}
