package com.softmed.stockapp.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.softmed.stockapp.database.AppDatabase;
import com.softmed.stockapp.dom.entities.CategoryBalance;

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
