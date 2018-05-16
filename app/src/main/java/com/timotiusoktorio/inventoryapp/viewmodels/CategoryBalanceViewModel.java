package com.timotiusoktorio.inventoryapp.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.timotiusoktorio.inventoryapp.database.AppDatabase;
import com.timotiusoktorio.inventoryapp.dom.objects.CategoryBalance;
import com.timotiusoktorio.inventoryapp.dom.objects.Transactions;

import java.util.List;


public class CategoryBalanceViewModel extends AndroidViewModel {

    private final LiveData<List<CategoryBalance>> categoryBalances;

    private AppDatabase appDatabase;

    public CategoryBalanceViewModel(Application application) {
        super(application);
        appDatabase = AppDatabase.getDatabase(this.getApplication());
        categoryBalances = appDatabase.categoriesModel().getCategoriesBalance();

    }

    public LiveData<List<CategoryBalance>> getCategoryBalances() {
        return categoryBalances;
    }

}
