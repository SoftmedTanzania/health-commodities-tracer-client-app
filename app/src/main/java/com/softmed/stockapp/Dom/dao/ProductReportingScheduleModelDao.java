package com.softmed.stockapp.Dom.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.softmed.stockapp.Dom.entities.ProductReportingSchedule;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ProductReportingScheduleModelDao {

    @Query("select *" +
            "FROM ProductReportingSchedule " +
            "INNER JOIN Product ON ProductReportingSchedule.productId = Product.id " +
            "WHERE Product.id = :productId AND ProductReportingSchedule.scheduledDate<=:date")
    List<ProductReportingSchedule> getMissedProductReportings(int productId,long date);


    @Query("select DISTINCT  ProductReportingSchedule.scheduledDate " +
            "FROM ProductReportingSchedule " +
            "INNER JOIN Product ON ProductReportingSchedule.productId = Product.id " +
            "WHERE ProductReportingSchedule.scheduledDate > :today")
    LiveData<List<Long>> getUpcomingReportingsDates(Long today);

    @Query("select Product.name " +
            "FROM ProductReportingSchedule " +
            "INNER JOIN Product ON ProductReportingSchedule.productId = Product.id " +
            "WHERE ProductReportingSchedule.scheduledDate = :time")
    List<String> getUpcomingReportingsProductsByDate(Long time);

    @Query("select * from ProductReportingSchedule WHERE productId = :productId and status=0 ")
    List<ProductReportingSchedule> getProductReportingScheduleByProductId(int productId);


    @Query("select * from ProductReportingSchedule WHERE id = :id ")
    ProductReportingSchedule getProductReportingScheduleById(int id);

    @Query("select * from ProductReportingSchedule")
    List<ProductReportingSchedule> getAllProductReportingSchedule();

    @Insert(onConflict = REPLACE)
    void addProductSchedule(ProductReportingSchedule productReportingSchedule);

    @Delete
    void deleteProductSchedule(ProductReportingSchedule productReportingSchedule);

}
