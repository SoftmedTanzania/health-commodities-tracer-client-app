package com.softmed.stockapp.Dom.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.softmed.stockapp.Dom.entities.Balances;
import com.softmed.stockapp.Dom.entities.ProductBalance;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface BalanceModelDao {

    @Query("select " +
            "Product.category_id as subcategoryId, " +
            "Product.description as productDescription, " +
            "Category.name as productCategory," +
            "Product.name as productName ," +
            "Unit.name as unit, " +
            "Balances.balance, " +
            "Balances.consumptionQuantity, " +
            "Product.id as productId " +

            "FROM Balances " +
            "INNER JOIN Product ON Balances.productId=Product.id " +
            "INNER JOIN Unit ON Product.unit_id=Unit.id " +
            "INNER JOIN Category ON Product.category_id = category.id " +
            "WHERE Balances.healthFacilityId = :locationId ")
    LiveData<List<ProductBalance>> getBalances(int locationId);




    @Query("select Product.id as productId, " +
            "Product.category_id as subcategoryId, " +
            "Product.description as productDescription, " +
            "Category.name as productCategory, " +
            "Product.name AS productName, " +
            "Unit.name as unit, " +
            "Balances.balance, " +
            "Balances.consumptionQuantity, " +
            "Product.id as productId " +
            " FROM Balances " +

            "INNER JOIN Product ON Balances.productId=Product.id " +
            "INNER JOIN Category ON Product.category_id = Category.id " +
            "INNER JOIN Unit ON Product.unit_id=Unit.id " +
            "WHERE Product.id = :productId AND " +
            " Balances.healthFacilityId = :locationId")
    LiveData<ProductBalance> getProductBalanceById(long productId, int locationId);




    @Query("select * from Balances WHERE Balances.healthFacilityId = :locationId AND Balances.productId=:product_id")
    Balances getBalance(int product_id,int locationId);

    @Query("select * from Balances WHERE healthFacilityId=:facilityId")
    List<Balances> getAllBalancesByFacility(int facilityId);

    @Query("select * from Balances WHERE syncStatus = 0")
    List<Balances> getUnPostedBalances();


    @Insert(onConflict = REPLACE)
    void addBalance(Balances balances);


    @Delete
    void deleteBalance(Balances balances);

}
