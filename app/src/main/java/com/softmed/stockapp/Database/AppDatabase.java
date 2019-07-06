package com.softmed.stockapp.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.softmed.stockapp.Dom.dao.BalanceModelDao;
import com.softmed.stockapp.Dom.dao.CategorieModelDao;
import com.softmed.stockapp.Dom.dao.LocationModelDao;
import com.softmed.stockapp.Dom.dao.ProductReportingScheduleModelDao;
import com.softmed.stockapp.Dom.dao.ProductsModelDao;
import com.softmed.stockapp.Dom.dao.TransactionModelDao;
import com.softmed.stockapp.Dom.dao.UnitsModelDao;
import com.softmed.stockapp.Dom.dao.UserInfoModelDao;
import com.softmed.stockapp.Dom.entities.Balances;
import com.softmed.stockapp.Dom.entities.Category;
import com.softmed.stockapp.Dom.entities.Location;
import com.softmed.stockapp.Dom.entities.Orders;
import com.softmed.stockapp.Dom.entities.Product;
import com.softmed.stockapp.Dom.entities.ProductReportingSchedule;
import com.softmed.stockapp.Dom.entities.TransactionType;
import com.softmed.stockapp.Dom.entities.Transactions;
import com.softmed.stockapp.Dom.entities.Unit;
import com.softmed.stockapp.Dom.entities.UsersInfo;


@Database(
        entities = {
                Category.class,
                Product.class,
                UsersInfo.class,
                Location.class,
                Unit.class,
                Transactions.class,
                TransactionType.class,
                Balances.class,
                ProductReportingSchedule.class,
                Orders.class
        },
        version = 4, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "InventoryAppDb").fallbackToDestructiveMigration().build();
        }
        return INSTANCE;
    }

    public abstract CategorieModelDao categoriesModel();

    public abstract ProductsModelDao productsModelDao();

    public abstract UnitsModelDao unitsDao();

    public abstract TransactionModelDao transactionsDao();

    public abstract LocationModelDao locationModelDao();

    public abstract BalanceModelDao balanceModelDao();

    public abstract UserInfoModelDao userInfoDao();

    public abstract ProductReportingScheduleModelDao productReportingScheduleModelDao();

}
