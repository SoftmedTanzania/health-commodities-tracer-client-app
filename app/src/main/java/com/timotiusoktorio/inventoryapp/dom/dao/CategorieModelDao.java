package com.timotiusoktorio.inventoryapp.dom.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import com.timotiusoktorio.inventoryapp.dom.objects.Category;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;


@Dao
public interface CategorieModelDao {

    @Query("select * from Category")
    List<Category> getAllCategories();

    @Query("select * from Category where id = :id")
    Category getCategoryById(long id);

    @Query("select name || ' ' from Category where id = :id")
    String getCategoryNameById(int id);

    @Insert(onConflict = REPLACE)
    void addCategory(Category category);

    @Delete
    void deleteCategory(Category category);

}
