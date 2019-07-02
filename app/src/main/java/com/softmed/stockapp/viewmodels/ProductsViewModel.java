package com.softmed.stockapp.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.softmed.stockapp.Database.AppDatabase;
import com.softmed.stockapp.Dom.entities.ProductBalance;
import com.softmed.stockapp.Dom.dto.ProductList;

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
