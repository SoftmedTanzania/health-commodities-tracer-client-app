package com.softmed.rucodia.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.softmed.rucodia.Database.AppDatabase;
import com.softmed.rucodia.Dom.entities.ProductBalance;
import com.softmed.rucodia.Dom.entities.ProductList;

import java.util.List;


public class ProductsViewModel extends AndroidViewModel {

    private final LiveData<List<ProductBalance>> productBalances;

    private AppDatabase appDatabase;

    public ProductsViewModel(Application application) {
        super(application);
        appDatabase = AppDatabase.getDatabase(this.getApplication());
        productBalances = appDatabase.balanceModelDao().getBalances();

    }

    public LiveData<List<ProductList>> getAvailableProducts() {
        return appDatabase.productsModelDao().getAvailableProducts();
    }

    public LiveData<ProductBalance> getProdictById(int productId) {
        return appDatabase.balanceModelDao().getProductBalanceById(productId);
    }

    public LiveData<List<ProductBalance>> getProductBalances() {
        return productBalances;
    }

}
