package com.softmed.stockapp_staging.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.softmed.stockapp_staging.database.AppDatabase;

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
