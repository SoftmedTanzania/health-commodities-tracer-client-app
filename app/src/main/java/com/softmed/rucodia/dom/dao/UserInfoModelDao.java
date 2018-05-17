package com.softmed.rucodia.dom.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.softmed.rucodia.dom.objects.UsersInfo;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;


@Dao
public interface UserInfoModelDao {

    @Query("select * from UsersInfo where userName = :username")
    List<UsersInfo> loggeInUser(String username);


    @Insert(onConflict = REPLACE)
    void addUserInfo(UsersInfo usersInfo);

    @Update
    void UpdateUserInfo(UsersInfo usersInfo);

    @Delete
    void deleteUserInfo(UsersInfo usersInfo);

}
