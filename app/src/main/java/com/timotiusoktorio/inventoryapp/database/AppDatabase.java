package com.timotiusoktorio.inventoryapp.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.Transaction;
import android.content.Context;

import com.timotiusoktorio.inventoryapp.dom.dao.BalanceModelDao;
import com.timotiusoktorio.inventoryapp.dom.dao.CategorieModelDao;
import com.timotiusoktorio.inventoryapp.dom.dao.LocationsModelDao;
import com.timotiusoktorio.inventoryapp.dom.dao.ProductsModelDao;
import com.timotiusoktorio.inventoryapp.dom.dao.SubCategorieModelDao;
import com.timotiusoktorio.inventoryapp.dom.dao.TransactionModelDao;
import com.timotiusoktorio.inventoryapp.dom.dao.TransactionTypeModelDao;
import com.timotiusoktorio.inventoryapp.dom.dao.UnitsModelDao;
import com.timotiusoktorio.inventoryapp.dom.dao.UserInfoModelDao;
import com.timotiusoktorio.inventoryapp.dom.objects.Balances;
import com.timotiusoktorio.inventoryapp.dom.objects.Category;
import com.timotiusoktorio.inventoryapp.dom.objects.Location;
import com.timotiusoktorio.inventoryapp.dom.objects.Product;
import com.timotiusoktorio.inventoryapp.dom.objects.SubCategory;
import com.timotiusoktorio.inventoryapp.dom.objects.TransactionType;
import com.timotiusoktorio.inventoryapp.dom.objects.Transactions;
import com.timotiusoktorio.inventoryapp.dom.objects.Unit;
import com.timotiusoktorio.inventoryapp.dom.objects.UsersInfo;


@Database(
        entities = {
                Category.class,
                SubCategory.class,
                Product.class,
                UsersInfo.class,
                Location.class,
                Unit.class,
                Transactions.class,
                TransactionType.class,
                Balances.class
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

    public abstract SubCategorieModelDao subCategoriesModel();

    public abstract ProductsModelDao productsModelDao();

    public abstract UnitsModelDao unitsDao();

    public abstract TransactionModelDao transactionsDao();

    public abstract TransactionTypeModelDao transactionTypeModelDao();

    public abstract BalanceModelDao balanceModelDao();

    public abstract UserInfoModelDao userInfoDao();

    public abstract LocationsModelDao locationsModelDao();

}
