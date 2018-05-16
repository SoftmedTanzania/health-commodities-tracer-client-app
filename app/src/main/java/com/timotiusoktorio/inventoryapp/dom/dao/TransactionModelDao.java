package com.timotiusoktorio.inventoryapp.dom.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.timotiusoktorio.inventoryapp.dom.objects.TransactionSummary;
import com.timotiusoktorio.inventoryapp.dom.objects.Transactions;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface TransactionModelDao {

    @Query("select SubCategory.name as subCategoryName, Product.name as productName, SUM(Transactions.amount) as amount, Transactions.price ,TransactionType.name as transactionType  from Transactions " +
            "INNER JOIN Product ON Transactions.product_id=Product.id " +
            "INNER JOIN TransactionType ON Transactions.transactiontype_id=TransactionType.id " +
            "INNER JOIN Unit ON Product.unitId=Unit.id " +
            "INNER JOIN SubCategory ON Product.subcategoryId = Subcategory.id "+
            " GROUP BY SubCategory.name,Product.name, Transactions.price ,TransactionType.name ")
    LiveData<List<TransactionSummary>> getTransactionSummary();

    @Query("select * from Transactions WHERE product_id = :productId")
    LiveData<List<Transactions>> getTransactionsByProductId(int productId);


    @Insert(onConflict = REPLACE)
    void addTransactions(Transactions transactions);

    @Delete
    void deleteTransactions(Transactions transactions);

}
