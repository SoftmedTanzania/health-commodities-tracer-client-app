package com.timotiusoktorio.inventoryapp.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.timotiusoktorio.inventoryapp.database.AppDatabase;
import com.timotiusoktorio.inventoryapp.dom.objects.TransactionSummary;
import com.timotiusoktorio.inventoryapp.dom.objects.Transactions;

import java.util.List;


public class TransactionsListViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public TransactionsListViewModel(Application application) {
        super(application);
        appDatabase = AppDatabase.getDatabase(this.getApplication());

    }

    public LiveData<List<TransactionSummary>> getTransactionSummaryList() {
        return appDatabase.transactionsDao().getTransactionSummary();
    }

    public LiveData<List<Transactions>> getTransactionsListByProductId(int productId) {
        return appDatabase.transactionsDao().getTransactionsByProductId(productId);
    }

    private static class deleteAsyncTask extends AsyncTask<Transactions, Void, Void> {
        private AppDatabase db;
        deleteAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Transactions... params) {
            db.transactionsDao().deleteTransactions(params[0]);
            return null;
        }

    }

}
