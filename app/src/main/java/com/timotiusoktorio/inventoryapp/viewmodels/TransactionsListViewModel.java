package com.timotiusoktorio.inventoryapp.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.timotiusoktorio.inventoryapp.database.AppDatabase;
import com.timotiusoktorio.inventoryapp.dom.objects.Transactions;

import java.util.List;


public class TransactionsListViewModel extends AndroidViewModel {

    private final LiveData<List<Transactions>> transactionsList;

    private AppDatabase appDatabase;

    public TransactionsListViewModel(Application application) {
        super(application);
        appDatabase = AppDatabase.getDatabase(this.getApplication());
        transactionsList = appDatabase.transactionsDao().getTransactions();

    }

    public LiveData<List<Transactions>> getTransactionsList() {
        return transactionsList;
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
