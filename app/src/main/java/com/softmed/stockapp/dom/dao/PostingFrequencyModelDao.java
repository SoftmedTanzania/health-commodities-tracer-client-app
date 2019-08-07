package com.softmed.stockapp.dom.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.softmed.stockapp.dom.dto.ProducToBeReportedtList;
import com.softmed.stockapp.dom.dto.ProductList;
import com.softmed.stockapp.dom.entities.PostingFrequencies;
import com.softmed.stockapp.dom.entities.Product;

import java.util.List;

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
