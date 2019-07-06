package com.softmed.stockapp.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.softmed.stockapp.Database.AppDatabase;
import com.softmed.stockapp.Dom.entities.ProductBalance;
import com.softmed.stockapp.Dom.dto.ProductList;

import java.util.List;


public class ProductsViewModel extends AndroidViewModel {
    private AppDatabase appDatabase;

    public ProductsViewModel(Application application) {
        super(application);
        appDatabase = AppDatabase.getDatabase(this.getApplication());

    }

    public LiveData<List<ProductList>> getAvailableProducts() {
        return appDatabase.productsModelDao().getAvailableProducts();
    }

    public LiveData<ProductBalance> getProdictById(int productId,int locationId) {
        return appDatabase.balanceModelDao().getProductBalanceById(productId,locationId);
    }

    public LiveData<List<ProductBalance>> getProductBalances(int locationId) {
        return appDatabase.balanceModelDao().getBalances(locationId);
    }

}
