package com.softmed.stockapp.Dom.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.softmed.stockapp.Dom.entities.TransactionSummary;
import com.softmed.stockapp.Dom.entities.Transactions;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface TransactionModelDao {

    @Query("select " +
            "Category.name as CategoryName, " +
            "Product.name as productName, " +
            "SUM(Transactions.amount) as amount, " +
            "Transactions.price ," +
            "TransactionType.name as transactionType,  " +
            "Transactions.created_at as created_at  " +
            "from Transactions " +
            "INNER JOIN Product ON Transactions.product_id=Product.id " +
            "INNER JOIN TransactionType ON Transactions.transactiontype_id=TransactionType.id " +
            "INNER JOIN Unit ON Product.unit_id=Unit.id " +
            "INNER JOIN Category ON Product.category_id = category.id "+
            " WHERE TransactionType.name<>'stock' "+
            " GROUP BY Category.name,Product.name, Transactions.price ,TransactionType.name, Transactions.created_at ")
    LiveData<List<TransactionSummary>> getTransactionSummary();


    @Query("select " +
            "Category.name as subCategoryName, " +
            "Product.name as productName, " +
            "SUM(Transactions.amount) as amount, " +
            "Transactions.price ," +
            "TransactionType.name as transactionType,  " +
            "Transactions.created_at as created_at  " +
            "from Transactions " +
            "INNER JOIN Product ON Transactions.product_id=Product.id " +
            "INNER JOIN TransactionType ON Transactions.transactiontype_id=TransactionType.id " +
            "INNER JOIN Unit ON Product.unit_id=Unit.id " +
            "INNER JOIN Category ON Product.category_id = category.id "+
            " WHERE TransactionType.name='stock' "+
            " GROUP BY Category.name,Product.name, Transactions.price ,TransactionType.name, Transactions.created_at ")
    LiveData<List<TransactionSummary>> getReceivedStock();


    @Query("select * from Transactions WHERE product_id = :productId")
    LiveData<List<Transactions>> getLiveTransactionsByProductId(int productId);


    @Query("select * from Transactions WHERE product_id = :productId")
    List<Transactions> getTransactionsByProductId(int productId);

    @Query("select * from Transactions WHERE syncStatus = 0")
    List<Transactions> getUnPostedTransactions();


    @Insert(onConflict = REPLACE)
    void addTransactions(Transactions transactions);

    @Delete
    void deleteTransactions(Transactions transactions);

}
