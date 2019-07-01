package com.softmed.stockapp.Dom.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import com.softmed.stockapp.Dom.entities.ProductReportingSchedule;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface ProductReportingScheduleModelDao {

    @Query("select *" +
            "FROM ProductReportingSchedule " +
            "INNER JOIN Product ON ProductReportingSchedule.productId = Product.id " +
            "WHERE Product.id = :productId")
    LiveData<List<ProductReportingSchedule>> getMissedProductReportings(int productId);


    @Query("select * from ProductReportingSchedule WHERE productId = :productId and status=0 ")
    List<ProductReportingSchedule> getProductReportingScheduleByProductId(int productId);

    @Query("select * from ProductReportingSchedule WHERE status = 0")
    List<ProductReportingSchedule> getAllProductReportingSchedule();


    @Insert(onConflict = REPLACE)
    void addProductSchedule(ProductReportingSchedule productReportingSchedule);

    @Delete
    void deleteProductSchedule(ProductReportingSchedule productReportingSchedule);

}
