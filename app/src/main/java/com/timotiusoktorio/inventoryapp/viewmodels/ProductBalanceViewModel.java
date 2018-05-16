package com.timotiusoktorio.inventoryapp.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.timotiusoktorio.inventoryapp.database.AppDatabase;
import com.timotiusoktorio.inventoryapp.dom.objects.CategoryBalance;
import com.timotiusoktorio.inventoryapp.dom.objects.ProductBalance;

import java.util.List;


public class ProductBalanceViewModel extends AndroidViewModel {

    private final LiveData<List<ProductBalance>> productBalances;

    private AppDatabase appDatabase;

    public ProductBalanceViewModel(Application application) {
        super(application);
        appDatabase = AppDatabase.getDatabase(this.getApplication());
        productBalances = appDatabase.balanceModelDao().getBalances();

    }

    public LiveData<List<ProductBalance>> getProductBalances() {
        return productBalances;
    }

}
