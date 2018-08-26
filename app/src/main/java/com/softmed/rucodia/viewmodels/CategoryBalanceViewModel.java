package com.softmed.rucodia.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.softmed.rucodia.Database.AppDatabase;
import com.softmed.rucodia.Dom.entities.CategoryBalance;

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
