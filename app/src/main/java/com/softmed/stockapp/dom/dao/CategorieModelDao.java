package com.softmed.stockapp.dom.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.softmed.stockapp.dom.entities.Category;
import com.softmed.stockapp.dom.entities.CategoryBalance;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;


@Dao
public interface CategorieModelDao {

    @Query("select * from Category")
    List<Category> getAllCategories();

    @Query("select * from Category where id = :id")
    Category getCategoryById(long id);


    @Query("select Category.id as categoryId,Category.name as name, SUM(Balances.balance) as balance, Balances.consumptionQuantity from Category " +
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
