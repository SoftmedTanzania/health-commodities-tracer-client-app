package com.timotiusoktorio.inventoryapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.timotiusoktorio.inventoryapp.model.Product;

import java.util.ArrayList;
import java.util.List;

import static android.provider.BaseColumns._ID;
import static com.timotiusoktorio.inventoryapp.database.ProductContract.ProductEntry.COLUMN_CODE;
import static com.timotiusoktorio.inventoryapp.database.ProductContract.ProductEntry.COLUMN_NAME;
import static com.timotiusoktorio.inventoryapp.database.ProductContract.ProductEntry.COLUMN_PHOTO_PATH;
import static com.timotiusoktorio.inventoryapp.database.ProductContract.ProductEntry.COLUMN_PRICE;
import static com.timotiusoktorio.inventoryapp.database.ProductContract.ProductEntry.COLUMN_QUANTITY;
import static com.timotiusoktorio.inventoryapp.database.ProductContract.ProductEntry.COLUMN_SUPPLIER;
import static com.timotiusoktorio.inventoryapp.database.ProductContract.ProductEntry.COLUMN_SUPPLIER_EMAIL;
import static com.timotiusoktorio.inventoryapp.database.ProductContract.ProductEntry.SQL_CREATE_ENTRY;
import static com.timotiusoktorio.inventoryapp.database.ProductContract.ProductEntry.SQL_DELETE_ENTRY;
import static com.timotiusoktorio.inventoryapp.database.ProductContract.ProductEntry.TABLE_NAME;

/**
 * Created by Timotius on 2016-08-07.
 */

public class ProductDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "InventoryApp.db";
    public static final int DATABASE_VERSION = 1;

    private static final String LOG_TAG = ProductDbHelper.class.getSimpleName();
    private static ProductDbHelper sDbHelperInstance;

    public static synchronized ProductDbHelper getInstance(Context context) {
        if (sDbHelperInstance == null) sDbHelperInstance = new ProductDbHelper(context);
        return sDbHelperInstance;
    }

    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRY);
        onCreate(sqLiteDatabase);
    }

    public void insertProduct(Product product) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues cv = generateContentValues(product);
            long insertId = db.insertOrThrow(TABLE_NAME, null, cv);
            Log.i(LOG_TAG, "Inserted new Product with ID: " + insertId);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        } finally {
            db.endTransaction();
        }
    }

    public List<Product> queryProducts() {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = { _ID, COLUMN_NAME, COLUMN_PHOTO_PATH, COLUMN_PRICE, COLUMN_QUANTITY };
        String orderBy = COLUMN_NAME + " ASC";
        Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null, orderBy);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Product product = createProductFromCursor(cursor);
                    products.add(product);
                } while (cursor.moveToNext());
                Log.i(LOG_TAG, "Queried Products with size: " + products.size());
            }
        } catch (IllegalArgumentException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        } finally {
            if (cursor != null && !cursor.isClosed()) cursor.close();
        }
        return products;
    }

    public Product queryProductDetails(Product product) {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = { COLUMN_CODE, COLUMN_SUPPLIER, COLUMN_SUPPLIER_EMAIL };
        String selection = _ID + " = ?";
        String[] selectionArgs = { String.valueOf(product.getId()) };
        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                product.setCode(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CODE)));
                product.setSupplier(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUPPLIER)));
                product.setSupplierEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUPPLIER_EMAIL)));
            }
        } catch (IllegalArgumentException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        } finally {
            if (cursor != null && !cursor.isClosed()) cursor.close();
        }
        return product;
    }

    public void updateProduct(Product product) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues cv = generateContentValues(product);
            String whereClause = _ID + " = ?";
            String[] whereArgs = { String.valueOf(product.getId()) };
            int noOfRowsAffected = db.update(TABLE_NAME, cv, whereClause, whereArgs);
            Log.i(LOG_TAG, "Number of rows affected: " + noOfRowsAffected);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void updateProductQuantity(long id, int newQuantity) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_QUANTITY, newQuantity);
            String whereClause = _ID + " = ?";
            String[] whereArgs = { String.valueOf(id) };
            int noOfRowsAffected = db.update(TABLE_NAME, cv, whereClause, whereArgs);
            Log.i(LOG_TAG, "Number of rows affected: " + noOfRowsAffected);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void deleteProduct(long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            String whereClause = _ID + " = ?";
            String[] whereArgs = { String.valueOf(id) };
            db.delete(TABLE_NAME, whereClause, whereArgs);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void deleteAllProducts() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_NAME, null, null);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void deleteDatabase(Context context) {
        context.deleteDatabase(getDatabaseName());
    }

    private ContentValues generateContentValues(Product product) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, product.getName());
        cv.put(COLUMN_CODE, product.getCode());
        cv.put(COLUMN_SUPPLIER, product.getSupplier());
        cv.put(COLUMN_SUPPLIER_EMAIL, product.getSupplierEmail());
        cv.put(COLUMN_PHOTO_PATH, product.getPhotoPath());
        cv.put(COLUMN_PRICE, product.getPrice());
        cv.put(COLUMN_QUANTITY, product.getQuantity());
        return cv;
    }

    private Product createProductFromCursor(Cursor cursor) throws IllegalArgumentException {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
        String photoPath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHOTO_PATH));
        double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE));
        int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY));
        return new Product(id, name, photoPath, price, quantity);
    }

}