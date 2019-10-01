package com.softmed.stockapp_staging.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.softmed.stockapp_staging.database.AppDatabase;
import com.softmed.stockapp_staging.dom.entities.OtherUsers;

import java.util.List;

public class ContactChooserViewModel extends ViewModel {

    private AppDatabase appDatabase;

    public ContactChooserViewModel() {

    }

    public void setAppDatabase(AppDatabase appDatabase) {
        this.appDatabase = appDatabase;
    }

    public LiveData<List<com.softmed.stockapp_staging.dom.dto.ContactUsersDTO>> getContacts(int excludingId) {
        return appDatabase.usersModelDao().getUsers(excludingId);
    }
}
