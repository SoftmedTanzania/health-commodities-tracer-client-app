package com.softmed.stockapp.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.softmed.stockapp.Database.AppDatabase;
import com.softmed.stockapp.Dom.dto.TransactionSummary;
import com.softmed.stockapp.Dom.entities.Transactions;

import java.util.List;


public class ProductReportingScheduleViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public ProductReportingScheduleViewModel(Application application) {
        super(application);
        appDatabase = AppDatabase.getDatabase(this.getApplication());

    }

    public LiveData<List<Long>> getUpcomingReportingsDates(long today) {
        return appDatabase.productReportingScheduleModelDao().getUpcomingReportingsDates(today);
    }


}
