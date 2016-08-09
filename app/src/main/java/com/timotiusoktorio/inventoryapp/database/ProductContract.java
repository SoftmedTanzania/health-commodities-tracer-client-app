package com.timotiusoktorio.inventoryapp.database;

import android.provider.BaseColumns;

/**
 * Created by Timotius on 2016-08-07.
 */

public class ProductContract {

    private static final String TEXT_TYPE = " TEXT";
    private static final String REAL_TYPE = " REAL";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String NOT_NULL = " NOT NULL";
    private static final String COMMA_SEP = ", ";

    public ProductContract() {}

    public static abstract class ProductEntry implements BaseColumns {

        public static final String TABLE_NAME = "product";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_CODE = "code";
        public static final String COLUMN_SUPPLIER = "supplier";
        public static final String COLUMN_SUPPLIER_EMAIL = "supplier_email";
        public static final String COLUMN_PHOTO_PATH = "photo_path";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QUANTITY = "quantity";

        public static final String SQL_CREATE_ENTRY =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + INTEGER_TYPE + " PRIMARY KEY" + COMMA_SEP +
                        COLUMN_NAME + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                        COLUMN_CODE + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                        COLUMN_SUPPLIER + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                        COLUMN_SUPPLIER_EMAIL + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                        COLUMN_PHOTO_PATH + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                        COLUMN_PRICE + REAL_TYPE + NOT_NULL + COMMA_SEP +
                        COLUMN_QUANTITY + INTEGER_TYPE + NOT_NULL + ")";

        public static final String SQL_DELETE_ENTRY =
                "DROP TABLE IF EXISTS " + TABLE_NAME;

    }

}