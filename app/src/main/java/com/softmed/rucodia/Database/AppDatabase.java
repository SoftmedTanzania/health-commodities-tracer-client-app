package com.softmed.rucodia.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.softmed.rucodia.Dom.dao.BalanceModelDao;
import com.softmed.rucodia.Dom.dao.CategorieModelDao;
import com.softmed.rucodia.Dom.dao.LocationsModelDao;
import com.softmed.rucodia.Dom.dao.OrderModelDao;
import com.softmed.rucodia.Dom.dao.ProductsModelDao;
import com.softmed.rucodia.Dom.dao.SubCategorieModelDao;
import com.softmed.rucodia.Dom.dao.TransactionModelDao;
import com.softmed.rucodia.Dom.dao.TransactionTypeModelDao;
import com.softmed.rucodia.Dom.dao.UnitsModelDao;
import com.softmed.rucodia.Dom.dao.UserInfoModelDao;
import com.softmed.rucodia.Dom.entities.Balances;
import com.softmed.rucodia.Dom.entities.Category;
import com.softmed.rucodia.Dom.entities.Location;
import com.softmed.rucodia.Dom.entities.Orders;
import com.softmed.rucodia.Dom.entities.Product;
import com.softmed.rucodia.Dom.entities.SubCategory;
import com.softmed.rucodia.Dom.entities.TransactionType;
import com.softmed.rucodia.Dom.entities.Transactions;
import com.softmed.rucodia.Dom.entities.Unit;
import com.softmed.rucodia.Dom.entities.UsersInfo;


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
        version = 2)

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
