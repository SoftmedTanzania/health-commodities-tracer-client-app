package com.timotiusoktorio.inventoryapp.dom.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.timotiusoktorio.inventoryapp.dom.objects.Unit;
import com.timotiusoktorio.inventoryapp.dom.objects.UsersInfo;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;


@Dao
public interface UserInfoModelDao {

    @Query("select * from UsersInfo")
    LiveData<List<UsersInfo>> getUnit();


    @Insert(onConflict = REPLACE)
    void addUserInfo(UsersInfo usersInfo);

    @Update
    void UpdateUserInfo(UsersInfo usersInfo);

    @Delete
    void deleteUserInfo(UsersInfo usersInfo);

}
