package com.softmed.rucodia.dom.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.softmed.rucodia.dom.objects.Product;
import com.softmed.rucodia.dom.objects.ProductList;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;


@Dao
public interface ProductsModelDao {

    @Query("select * from Product")
    List<Product> getAllProducts();


    @Query("select * from Product where status = 0")
    List<Product> getUnpostedProducts();

    @Query("select Product.id,SubCategory.name || ' - ' || Product.name AS name ,Unit.name as unit,Balances.balance, Balances.price  FROM Product " +
            "INNER JOIN SubCategory ON Product.sub_category_id = SubCategory.id " +
            "INNER JOIN Unit ON Product.unit_id = Unit.id " +
            "INNER JOIN Balances ON Product.id = Balances.product_id " +
            " ")
    LiveData<List<ProductList>> getAvailableProducts();



    @Query("select Product.id,SubCategory.name || ' - ' || Product.name AS name ,Unit.name as unit,Balances.balance, Balances.price  FROM Product " +
            "INNER JOIN SubCategory ON Product.sub_category_id = SubCategory.id " +
            "INNER JOIN Unit ON Product.unit_id = Unit.id " +
            "INNER JOIN Balances ON Product.id = Balances.product_id " +
            " ")
    List<ProductList> getAvailableProductsTest();


    @Query("select Product.id,Product.sub_category_id,Product.description,SubCategory.name || ' - ' || Product.name AS name ,Product.unit_id,Product.uuid,Product.status, price FROM Product " +
            "INNER JOIN SubCategory ON Product.sub_category_id = SubCategory.id " +
            "where sub_category_id = :subCategoryId")
    List<Product> getProductsBySubCategoryId(int subCategoryId);


    @Query("select * from Product inner join SubCategory on SubCategory.id = Product.sub_category_id where SubCategory.categoryId = :categoryId")
    List<Product> getProductsByCategoryId(int categoryId);


    @Query("select name || ' ' from Product where id = :id")
    String getProductNameById(int id);

    @Insert(onConflict = REPLACE)
    void addProduct(Product product);

    @Delete
    void deleteProduct(Product product);

}
