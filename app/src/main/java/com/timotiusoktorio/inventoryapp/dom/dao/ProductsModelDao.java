package com.timotiusoktorio.inventoryapp.dom.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.timotiusoktorio.inventoryapp.dom.objects.Category;
import com.timotiusoktorio.inventoryapp.dom.objects.Product;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;


@Dao
public interface ProductsModelDao {

    @Query("select * from Product")
    List<Product> getAllProducts();

    @Query("select * from Product where subcategoryId = :subCategoryId")
    List<Product> getProductsBySubCategoryId(int subCategoryId);


    @Query("select * from Product inner join SubCategory on SubCategory.id = Product.subcategoryId where SubCategory.categoryId = :categoryId")
    List<Product> getProductsByCategoryId(int categoryId);


    @Query("select * from Product where id = :id")
    Product getProductById(long id);

    @Query("select name || ' ' from Product where id = :id")
    String getProductNameById(int id);

    @Insert(onConflict = REPLACE)
    void addProduct(Product product);

    @Delete
    void deleteProduct(Product product);

}
