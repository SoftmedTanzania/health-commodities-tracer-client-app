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
    LiveData<List<TransactionType>> getTransactionTypes();


    @Insert(onConflict = REPLACE)
    void addTransactionsTypes(TransactionType transactionType);

}
