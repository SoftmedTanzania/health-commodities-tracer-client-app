package com.softmed.rucodia.dom.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.softmed.rucodia.dom.objects.SubCategory;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;


@Dao
public interface SubCategorieModelDao {

    @Query("select * from SubCategory where categoryId = :categoryId")
    List<SubCategory> getSubCategoryByCategoryId(int categoryId);

    @Query("select * from SubCategory ")
    List<SubCategory> getSubCategories();


    @Insert(onConflict = REPLACE)
    void addSubCategory(SubCategory subCategory);


    @Delete
    void deleteSubCategory(SubCategory subCategory);

}
