package com.softmed.stockapp_staging.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.softmed.stockapp_staging.dom.dao.BalanceModelDao;
import com.softmed.stockapp_staging.dom.dao.CategorieModelDao;
import com.softmed.stockapp_staging.dom.dao.LocationModelDao;
import com.softmed.stockapp_staging.dom.dao.MessageRecipientsModelDao;
import com.softmed.stockapp_staging.dom.dao.MessagesModelDao;
import com.softmed.stockapp_staging.dom.dao.PostingFrequencyModelDao;
import com.softmed.stockapp_staging.dom.dao.ProductReportingScheduleModelDao;
import com.softmed.stockapp_staging.dom.dao.ProductsModelDao;
import com.softmed.stockapp_staging.dom.dao.TransactionModelDao;
import com.softmed.stockapp_staging.dom.dao.UnitsModelDao;
import com.softmed.stockapp_staging.dom.dao.UserInfoModelDao;
import com.softmed.stockapp_staging.dom.dao.UsersModelDao;
import com.softmed.stockapp_staging.dom.entities.Balances;
import com.softmed.stockapp_staging.dom.entities.Category;
import com.softmed.stockapp_staging.dom.entities.Location;
import com.softmed.stockapp_staging.dom.entities.Message;
import com.softmed.stockapp_staging.dom.entities.MessageRecipients;
import com.softmed.stockapp_staging.dom.entities.OtherUsers;
import com.softmed.stockapp_staging.dom.entities.PostingFrequencies;
import com.softmed.stockapp_staging.dom.entities.Product;
import com.softmed.stockapp_staging.dom.entities.ProductReportingSchedule;
import com.softmed.stockapp_staging.dom.entities.TransactionType;
import com.softmed.stockapp_staging.dom.entities.Transactions;
import com.softmed.stockapp_staging.dom.entities.Unit;
import com.softmed.stockapp_staging.dom.entities.UsersInfo;


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
                PostingFrequencies.class,
                MessageRecipients.class
        },
        version = 6, exportSchema = false)
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

    public abstract PostingFrequencyModelDao postingFrequencyModelDao();

    public abstract UsersModelDao usersModelDao();

    public abstract MessagesModelDao messagesModelDao();

    public abstract MessageRecipientsModelDao messageRecipientsModelDao();

}
