package com.timotiusoktorio.inventoryapp.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.timotiusoktorio.inventoryapp.dom.dao.CategorieModelDao;
import com.timotiusoktorio.inventoryapp.dom.dao.SubCategorieModelDao;
import com.timotiusoktorio.inventoryapp.dom.dao.UnitsModelDao;
import com.timotiusoktorio.inventoryapp.dom.dao.UserInfoModelDao;
import com.timotiusoktorio.inventoryapp.dom.objects.Category;
import com.timotiusoktorio.inventoryapp.dom.objects.SubCategory;
import com.timotiusoktorio.inventoryapp.dom.objects.Unit;
import com.timotiusoktorio.inventoryapp.dom.objects.UsersInfo;


@Database(
        entities = {
                Category.class,
                SubCategory.class,
                UsersInfo.class,
                Unit.class
        },
        version = 1)

public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "InventoryAppDb")
                            .build();
        }
        return INSTANCE;
    }

    public abstract CategorieModelDao categoriesModel();

    public abstract SubCategorieModelDao subCategoryModel();

    public abstract UnitsModelDao UnitDao();

    public abstract UserInfoModelDao userInfoDao();

}
