package com.softmed.stockapp.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.softmed.stockapp.database.AppDatabase;
import com.softmed.stockapp.dom.entities.ProductBalance;
import com.softmed.stockapp.dom.dto.ProductList;

import java.util.List;


public class ProductsViewModel extends AndroidViewModel {
    private AppDatabase appDatabase;

    public ProductsViewModel(Application application) {
        super(application);
        appDatabase = AppDatabase.getDatabase(this.getApplication());

    }

    public LiveData<List<ProductList>> getAvailableProducts(int locationId) {
        return appDatabase.productsModelDao().getAvailableProducts(locationId);
    }

    public LiveData<ProductBalance> getProdictById(int productId,int locationId) {
        return appDatabase.balanceModelDao().getProductBalanceById(productId,locationId);
    }

    public LiveData<List<ProductBalance>> getProductBalances(int locationId) {
        return appDatabase.balanceModelDao().getBalances(locationId);
    }

}
