package com.softmed.stockapp.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.softmed.stockapp.database.AppDatabase;
import com.softmed.stockapp.dom.entities.OtherUsers;

import java.util.List;

public class ContactChooserViewModel extends ViewModel {

    private AppDatabase appDatabase;

    public ContactChooserViewModel() {

    }

    public void setAppDatabase(AppDatabase appDatabase) {
        this.appDatabase = appDatabase;
    }

    public LiveData<List<OtherUsers>> getContacts(int excludingId) {
        return appDatabase.usersModelDao().getUsers(excludingId);
    }
}
