package com.timotiusoktorio.inventoryapp.dom.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.timotiusoktorio.inventoryapp.dom.objects.Category;
import com.timotiusoktorio.inventoryapp.dom.objects.Product;
import com.timotiusoktorio.inventoryapp.dom.objects.ProductBalance;
import com.timotiusoktorio.inventoryapp.dom.objects.ProductList;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;


@Dao
public interface ProductsModelDao {

    @Query("select * from Product")
    List<Product> getAllProducts();

    @Query("select Product.id,SubCategory.name || ' - ' || Product.name AS name ,Unit.name as unit,Balances.balance, Balances.price  FROM Product " +
            "INNER JOIN SubCategory ON Product.subcategoryId = SubCategory.id " +
            "INNER JOIN Unit ON Product.unitId = Unit.id " +
            "INNER JOIN Balances ON Product.id = Balances.product_id " +
            " ")
    LiveData<List<ProductList>> getAvailableProducts();



    @Query("select Product.id,SubCategory.name || ' - ' || Product.name AS name ,Unit.name as unit,Balances.balance, Balances.price  FROM Product " +
            "INNER JOIN SubCategory ON Product.subcategoryId = SubCategory.id " +
            "INNER JOIN Unit ON Product.unitId = Unit.id " +
            "INNER JOIN Balances ON Product.id = Balances.product_id " +
            " ")
    List<ProductList> getAvailableProductsTest();


    @Query("select Product.id,Product.subcategoryId,Product.description,SubCategory.name || ' - ' || Product.name AS name ,Product.unitId,Product.uuid FROM Product " +
            "INNER JOIN SubCategory ON Product.subcategoryId = SubCategory.id " +
            "where subcategoryId = :subCategoryId")
    List<Product> getProductsBySubCategoryId(int subCategoryId);


    @Query("select * from Product inner join SubCategory on SubCategory.id = Product.subcategoryId where SubCategory.categoryId = :categoryId")
    List<Product> getProductsByCategoryId(int categoryId);


    @Query("select name || ' ' from Product where id = :id")
    String getProductNameById(int id);

    @Insert(onConflict = REPLACE)
    void addProduct(Product product);

    @Delete
    void deleteProduct(Product product);

}
