package com.softmed.rucodia.Dom.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.softmed.rucodia.Dom.entities.TransactionType;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface TransactionTypeModelDao {

    @Query("select * from TransactionType")
    List<TransactionType> getTransactionTypes();

    @Query("select name || ' '  from TransactionType WHERE id=:id")
    String getTransactionTypeName(int id);


    @Insert(onConflict = REPLACE)
    void addTransactionsTypes(TransactionType transactionType);

}
