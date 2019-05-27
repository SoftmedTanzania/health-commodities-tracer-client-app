package com.softmed.stockapp.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.softmed.stockapp.Database.AppDatabase;
import com.softmed.stockapp.Dom.entities.OrdersItems;

import java.util.List;


public class OrderViewModel extends AndroidViewModel {


    private AppDatabase appDatabase;

    public OrderViewModel(Application application) {
        super(application);
        appDatabase = AppDatabase.getDatabase(this.getApplication());

    }

    public LiveData<List<OrdersItems>> getOrdersByBatchNo(String batchNo) {
        return appDatabase.orderModelDao().getOrdersByBatchNo(batchNo);
    }


}
