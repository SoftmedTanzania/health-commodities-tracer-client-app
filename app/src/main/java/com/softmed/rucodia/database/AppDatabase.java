package com.softmed.rucodia.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.softmed.rucodia.dom.dao.BalanceModelDao;
import com.softmed.rucodia.dom.dao.CategorieModelDao;
import com.softmed.rucodia.dom.dao.LocationsModelDao;
import com.softmed.rucodia.dom.dao.OrderModelDao;
import com.softmed.rucodia.dom.dao.ProductsModelDao;
import com.softmed.rucodia.dom.dao.SubCategorieModelDao;
import com.softmed.rucodia.dom.dao.TransactionModelDao;
import com.softmed.rucodia.dom.dao.TransactionTypeModelDao;
import com.softmed.rucodia.dom.dao.UnitsModelDao;
import com.softmed.rucodia.dom.dao.UserInfoModelDao;
import com.softmed.rucodia.dom.objects.Balances;
import com.softmed.rucodia.dom.objects.Category;
import com.softmed.rucodia.dom.objects.Location;
import com.softmed.rucodia.dom.objects.Orders;
import com.softmed.rucodia.dom.objects.Product;
import com.softmed.rucodia.dom.objects.SubCategory;
import com.softmed.rucodia.dom.objects.TransactionType;
import com.softmed.rucodia.dom.objects.Transactions;
import com.softmed.rucodia.dom.objects.Unit;
import com.softmed.rucodia.dom.objects.UsersInfo;


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
                Balances.class,
                Orders.class
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
    public abstract OrderModelDao orderModelDao();

}
