package com.softmed.stockapp.Dom.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.softmed.stockapp.Dom.entities.TransactionType;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface TransactionTypeModelDao {

    @Query("select * from TransactionType")
    List<TransactionType> getTransactionTypes();

    @Query("select name || ' '  from TransactionType WHERE id=:id")
    String getTransactionTypeName(int id);


    @Insert(onConflict = REPLACE)
    void addTransactionsTypes(TransactionType transactionType);

}
