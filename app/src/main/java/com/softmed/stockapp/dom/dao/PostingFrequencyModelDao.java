package com.softmed.stockapp.dom.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.softmed.stockapp.dom.entities.PostingFrequencies;

import static androidx.room.OnConflictStrategy.REPLACE;


@Dao
public interface PostingFrequencyModelDao {
    @Query("select * from PostingFrequencies WHERE id=:id")
    PostingFrequencies getPostingFrequencyById(int id);


    @Insert(onConflict = REPLACE)
    void addPostingFrequency(PostingFrequencies frequency);

    @Delete
    void deletePostingFrequency(PostingFrequencies frequency);

}
