package com.timotiusoktorio.inventoryapp.dom.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.timotiusoktorio.inventoryapp.dom.objects.Transactions;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by issy on 1/8/18.
 *
 * @issyzac issyzac.iz@gmail.com
 * On Project HFReferralApp
 */

@Dao
public interface TransactionModelDao {

    @Query("select * from Transactions")
    LiveData<List<Transactions>> getTransactions();


    @Insert(onConflict = REPLACE)
    void addTransactions(Transactions transactions);

}
