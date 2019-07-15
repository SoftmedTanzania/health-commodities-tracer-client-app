package com.softmed.stockapp.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.softmed.stockapp.dom.dao.BalanceModelDao;
import com.softmed.stockapp.dom.dao.CategorieModelDao;
import com.softmed.stockapp.dom.dao.LocationModelDao;
import com.softmed.stockapp.dom.dao.MessagesModelDao;
import com.softmed.stockapp.dom.dao.ProductReportingScheduleModelDao;
import com.softmed.stockapp.dom.dao.ProductsModelDao;
import com.softmed.stockapp.dom.dao.TransactionModelDao;
import com.softmed.stockapp.dom.dao.UnitsModelDao;
import com.softmed.stockapp.dom.dao.UserInfoModelDao;
import com.softmed.stockapp.dom.dao.UsersModelDao;
import com.softmed.stockapp.dom.entities.Balances;
import com.softmed.stockapp.dom.entities.Category;
import com.softmed.stockapp.dom.entities.Location;
import com.softmed.stockapp.dom.entities.Message;
import com.softmed.stockapp.dom.entities.MessageRecipients;
import com.softmed.stockapp.dom.entities.OtherUsers;
import com.softmed.stockapp.dom.entities.Product;
import com.softmed.stockapp.dom.entities.ProductReportingSchedule;
import com.softmed.stockapp.dom.entities.TransactionType;
import com.softmed.stockapp.dom.entities.Transactions;
import com.softmed.stockapp.dom.entities.Unit;
import com.softmed.stockapp.dom.entities.UsersInfo;


@Database(
        entities = {
                Category.class,
                Product.class,
                UsersInfo.class,
                OtherUsers.class,
                Location.class,
                Unit.class,
                Transactions.class,
                TransactionType.class,
                Balances.class,
                ProductReportingSchedule.class,
                Message.class,
                MessageRecipients.class
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

    public abstract UsersModelDao usersModelDao();

    public abstract MessagesModelDao messagesModelDao();

}
