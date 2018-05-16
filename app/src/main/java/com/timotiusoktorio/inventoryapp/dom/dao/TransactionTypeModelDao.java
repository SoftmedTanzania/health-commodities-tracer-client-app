package com.timotiusoktorio.inventoryapp.dom.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.timotiusoktorio.inventoryapp.dom.objects.TransactionType;
import com.timotiusoktorio.inventoryapp.dom.objects.Transactions;

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
