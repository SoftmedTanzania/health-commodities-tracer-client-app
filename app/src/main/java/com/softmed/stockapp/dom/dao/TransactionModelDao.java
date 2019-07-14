package com.softmed.stockapp.dom.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.softmed.stockapp.dom.dto.TransactionSummary;
import com.softmed.stockapp.dom.entities.Transactions;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface TransactionModelDao {

    @Query("select " +
            "Category.name as CategoryName, " +
            "Product.name as productName, " +
            "SUM(Transactions.amount) as amount, " +
            "Transactions.clientsOnRegime ," +
            "TransactionType.name as transactionType,  " +
            "Transactions.created_at as created_at  " +
            "from Transactions " +
            "INNER JOIN Product ON Transactions.product_id=Product.id " +
            "INNER JOIN TransactionType ON Transactions.transactiontype_id=TransactionType.id " +
            "INNER JOIN Unit ON Product.unit_id=Unit.id " +
            "INNER JOIN Category ON Product.category_id = category.id "+
            " WHERE TransactionType.name<>'stock' "+
            " GROUP BY Category.name,Product.name, Transactions.clientsOnRegime ,TransactionType.name, Transactions.created_at ")
    LiveData<List<TransactionSummary>> getTransactionSummary();



    @Query("select * from Transactions" +
            " INNER JOIN ProductReportingSchedule ON ProductReportingSchedule.id = Transactions.scheduleId" +
            " WHERE product_id = :productId AND ProductReportingSchedule.facilityId = :facilityId")
    LiveData<List<Transactions>> getLiveTransactionsByProductId(int productId,int facilityId);


    @Query("select * from Transactions " +
            " INNER JOIN ProductReportingSchedule ON ProductReportingSchedule.id = Transactions.scheduleId" +
            " WHERE product_id = :productId AND ProductReportingSchedule.facilityId = :facilityId " +
            "ORDER BY created_at DESC Limit 1")
    LiveData<Transactions> getLastTransactionByProductId(int productId,int facilityId);


    @Query("select * from Transactions WHERE product_id = :productId")
    List<Transactions> getTransactionsByProductId(int productId);

    @Query("select * from Transactions WHERE syncStatus = 0")
    List<Transactions> getUnPostedTransactions();


    @Insert(onConflict = REPLACE)
    void addTransactions(Transactions transactions);

    @Delete
    void deleteTransactions(Transactions transactions);

}
