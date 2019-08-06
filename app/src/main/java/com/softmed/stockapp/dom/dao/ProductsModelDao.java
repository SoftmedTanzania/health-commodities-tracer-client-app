package com.softmed.stockapp.dom.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.softmed.stockapp.dom.dto.ProducToBeReportedtList;
import com.softmed.stockapp.dom.entities.Product;
import com.softmed.stockapp.dom.dto.ProductList;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;


@Dao
public interface ProductsModelDao {

    @Query("select * from Product")
    List<Product> getAllProducts();


    @Query("select * from Product where status = 0")
    List<Product> getUnpostedProducts();

    @Query("select Product.id, Product.name AS name ,Unit.name as unit,Balances.balance FROM Product " +
            "INNER JOIN Category ON Product.category_id = Category.id " +
            "INNER JOIN Unit ON Product.unit_id = Unit.id " +
            "INNER JOIN Balances ON Product.id = Balances.productId WHERE Balances.healthFacilityId = :facilityId " +
            " ")
    LiveData<List<ProductList>> getAvailableProducts(int facilityId);


    @Query("select Product.id,Product.name AS name ,Unit.name as unit,Balances.balance,ProductReportingSchedule.id as scheduleId  FROM Product " +
            "INNER JOIN Category ON Product.category_id = Category.id " +
            "INNER JOIN Unit ON Product.unit_id = Unit.id " +
            "INNER JOIN Balances ON Product.id = Balances.productId " +
            "INNER JOIN ProductReportingSchedule ON Product.id = ProductReportingSchedule.productId " +
            "WHERE Balances.balance=0 ")
    List<ProducToBeReportedtList> getUninitializedProducts();

    @Query("select  Product.id,Product.name AS name ,Unit.name as unit,Balances.balance,ProductReportingSchedule.scheduledDate, ProductReportingSchedule.id as scheduleId  FROM Product " +
            "INNER JOIN Category ON Product.category_id = Category.id " +
            "INNER JOIN Unit ON Product.unit_id = Unit.id " +
            "INNER JOIN Balances ON Product.id = Balances.productId " +
            "INNER JOIN ProductReportingSchedule ON Product.id = ProductReportingSchedule.productId " +
            "WHERE ProductReportingSchedule.scheduledDate <= :today AND ProductReportingSchedule.status='pending' AND ProductReportingSchedule.facilityId  = :healthFacilityId group by ProductReportingSchedule.id")
    List<ProducToBeReportedtList> getUnreportedProductStocks(long today, int healthFacilityId);


    @Query("select Product.id,Product.category_id,Product.description,Category.name || ' - ' || Product.name AS name ,Product.unit_id,Product.status,track_number_of_patients,track_wastage,track_quantity_expired FROM Product " +
            "INNER JOIN Category ON Product.category_id = Category.id " +
            "where category_id = :categoryId")
    List<Product> getProductsSummaryByCategoryId(int categoryId);


    @Query("select Product.id,Product.name,Product.category_id,Product.unit_id,Product.description,Product.status,track_quantity_expired,track_wastage,track_number_of_patients from Product inner join Category on Category.id = Product.category_id where Category.id = :categoryId")
    List<Product> getProductsByCategoryId(int categoryId);


    @Query("select name || ' ' from Product where id = :id")
    String getProductNameById(int id);

    @Query("select * from Product where id = :id")
    Product getProductById(int id);

    @Insert(onConflict = REPLACE)
    void addProduct(Product product);

    @Delete
    void deleteProduct(Product product);

}
