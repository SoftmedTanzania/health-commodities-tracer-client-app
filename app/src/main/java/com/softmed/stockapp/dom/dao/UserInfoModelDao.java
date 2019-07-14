package com.softmed.stockapp.dom.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.softmed.stockapp.dom.entities.UsersInfo;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;


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
