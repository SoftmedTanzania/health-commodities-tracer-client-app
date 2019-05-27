package com.softmed.stockapp.Dom.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.softmed.stockapp.Dom.entities.Product;
import com.softmed.stockapp.Dom.entities.ProductList;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;


@Dao
public interface ProductsModelDao {

    @Query("select * from Product")
    List<Product> getAllProducts();


    @Query("select * from Product where status = 0")
    List<Product> getUnpostedProducts();

    @Query("select Product.id,Category.name || ' - ' || Product.name AS name ,Unit.name as unit,Balances.balance, Balances.price  FROM Product " +
            "INNER JOIN Category ON Product.category_id = Category.id " +
            "INNER JOIN Unit ON Product.unit_id = Unit.id " +
            "INNER JOIN Balances ON Product.id = Balances.product_id " +
            " ")
    LiveData<List<ProductList>> getAvailableProducts();



    @Query("select Product.id,Category.name || ' - ' || Product.name AS name ,Unit.name as unit,Balances.balance, Balances.price  FROM Product " +
            "INNER JOIN Category ON Product.category_id = Category.id " +
            "INNER JOIN Unit ON Product.unit_id = Unit.id " +
            "INNER JOIN Balances ON Product.id = Balances.product_id " +
            " ")
    List<ProductList> getAvailableProductsTest();


    @Query("select Product.id,Product.category_id,Product.description,Category.name || ' - ' || Product.name AS name ,Product.unit_id,Product.uuid,Product.status, price FROM Product " +
            "INNER JOIN Category ON Product.category_id = Category.id " +
            "where category_id = :categoryId")
    List<Product> getProductsSummaryByCategoryId(int categoryId);


    @Query("select * from Product inner join Category on Category.id = Product.category_id where Category.id = :categoryId")
    List<Product> getProductsByCategoryId(int categoryId);


    @Query("select name || ' ' from Product where id = :id")
    String getProductNameById(int id);

    @Insert(onConflict = REPLACE)
    void addProduct(Product product);

    @Delete
    void deleteProduct(Product product);

}
