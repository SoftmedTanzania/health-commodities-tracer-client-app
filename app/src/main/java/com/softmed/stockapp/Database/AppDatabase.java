package com.softmed.stockapp.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.softmed.stockapp.Dom.dao.BalanceModelDao;
import com.softmed.stockapp.Dom.dao.CategorieModelDao;
import com.softmed.stockapp.Dom.dao.LocationsModelDao;
import com.softmed.stockapp.Dom.dao.OrderModelDao;
import com.softmed.stockapp.Dom.dao.ProductReportingScheduleModelDao;
import com.softmed.stockapp.Dom.dao.ProductsModelDao;
import com.softmed.stockapp.Dom.dao.TransactionModelDao;
import com.softmed.stockapp.Dom.dao.TransactionTypeModelDao;
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
        version = 2,exportSchema = false)
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

    public abstract ProductsModelDao productsModelDao();

    public abstract UnitsModelDao unitsDao();

    public abstract TransactionModelDao transactionsDao();

    public abstract TransactionTypeModelDao transactionTypeModelDao();

    public abstract BalanceModelDao balanceModelDao();

    public abstract UserInfoModelDao userInfoDao();

    public abstract LocationsModelDao locationsModelDao();

    public abstract OrderModelDao orderModelDao();

    public abstract ProductReportingScheduleModelDao productReportingScheduleModelDao();

}
