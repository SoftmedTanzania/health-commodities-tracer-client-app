package com.softmed.stockapp.Dom.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.softmed.stockapp.Dom.entities.Category;
import com.softmed.stockapp.Dom.entities.CategoryBalance;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;


@Dao
public interface CategorieModelDao {

    @Query("select * from Category")
    List<Category> getAllCategories();

    @Query("select * from Category where id = :id")
    Category getCategoryById(long id);


    @Query("select Category.name as name, SUM(Balances.balance) as balance from Category " +
            "INNER JOIN Product ON Category.id = Product.category_id  " +
            "INNER JOIN Balances ON Product.id = Balances.productId  " +
            "GROUP BY Category.name")
    LiveData<List<CategoryBalance>> getCategoriesBalance();


    @Query("select name || ' ' from Category where id = :id")
    String getCategoryNameById(int id);

    @Insert(onConflict = REPLACE)
    void addCategory(Category category);

    @Delete
    void deleteCategory(Category category);

}
