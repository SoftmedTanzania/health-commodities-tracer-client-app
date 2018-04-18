package com.timotiusoktorio.inventoryapp.database;

import android.provider.BaseColumns;

/**
 * Created by Coze on 2016-08-07.
 */

public class ProductContract {

    private static final String TEXT_TYPE = " TEXT";
    private static final String REAL_TYPE = " REAL";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String NOT_NULL = " NOT NULL";
    private static final String COMMA_SEP = ", ";

    public ProductContract() {}

    public static abstract class ProductEntry implements BaseColumns {

        public static final String TABLE_PRODUCT = "product";

        public static final String TABLE_CATEGORY = "category";
        public static final String TABLE_SUB_CATEGORY = "sub_category";
        public static final String TABLE_TYPE = "type";
        public static final String TABLE_CATEGORY_TYPE = "category_type";
        public static final String TABLE_SUB_CATEGORY_TYPE = "sub_category_type";
        public static final String TABLE_CATEGORY_SUB_CATEGORY = "category_sub_category";
        public static final String TABLE_UNITS_OF_MEASURE = "unity_of_measure";


        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_IS_VALID = "is_valid";
        public static final String COLUMN_CATEGORY_ID = "category_id";
        public static final String COLUMN_SUB_CATEGORY_ID = "sub_category_id";
        public static final String COLUMN_TYPE_ID = "type_id";
        public static final String COLUMN_CATEGORY_SUB_CATEGORY_ID = "category_sub_category_id";
        public static final String COLUMN_UNITS_OF_MEASURE_ID = "unit_of_measure_id";


        public static final String COLUMN_SUPPLIER = "supplier";
        public static final String COLUMN_SUPPLIER_EMAIL = "supplier_email";
        public static final String COLUMN_PHOTO_PATH = "photo_path";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QUANTITY = "quantity";



        public static final String SQL_CREATE_CATEGORY =
                "CREATE TABLE " + TABLE_CATEGORY + " (" +
                        _ID + INTEGER_TYPE + " PRIMARY KEY autoincrement" + COMMA_SEP +
                        COLUMN_NAME + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                        COLUMN_IS_VALID + INTEGER_TYPE + NOT_NULL  + ")";

        public static final String SQL_CREATE_UNITS_OF_MEASURE=
                "CREATE TABLE " + TABLE_UNITS_OF_MEASURE + " (" +
                        _ID + INTEGER_TYPE + " PRIMARY KEY autoincrement" + COMMA_SEP +
                        COLUMN_NAME + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                        COLUMN_IS_VALID + INTEGER_TYPE + NOT_NULL  + ")";

        public static final String SQL_CREATE_SUB_CATEGORY =
                "CREATE TABLE " + TABLE_SUB_CATEGORY + " (" +
                        _ID + INTEGER_TYPE + " PRIMARY KEY autoincrement" + COMMA_SEP +
                        COLUMN_NAME + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                        COLUMN_IS_VALID + INTEGER_TYPE + NOT_NULL  + ")";


        public static final String SQL_CREATE_CATEGORY_SUB_CATEGORY =
                "CREATE TABLE " + TABLE_CATEGORY_SUB_CATEGORY + " (" +
                        _ID + INTEGER_TYPE + " PRIMARY KEY autoincrement" + COMMA_SEP +
                        COLUMN_CATEGORY_ID + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                        COLUMN_SUB_CATEGORY_ID + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                        "  FOREIGN KEY ("+COLUMN_CATEGORY_ID+") REFERENCES "+TABLE_CATEGORY+"("+_ID+")" + COMMA_SEP +
                        "  FOREIGN KEY ("+COLUMN_SUB_CATEGORY_ID+") REFERENCES "+TABLE_SUB_CATEGORY+"("+_ID+")" +
                        ")";

        public static final String SQL_CREATE_TYPE=
                "CREATE TABLE " + TABLE_TYPE + " (" +
                        _ID + INTEGER_TYPE + " PRIMARY KEY autoincrement" + COMMA_SEP +
                        COLUMN_NAME + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                        COLUMN_DESCRIPTION + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                        COLUMN_CATEGORY_SUB_CATEGORY_ID + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                        COLUMN_IS_VALID + INTEGER_TYPE + NOT_NULL  + ")";



//
//        public static final String SQL_CREATE_SUB_CATEGORY_TYPE=
//                "CREATE TABLE " + TABLE_SUB_CATEGORY_TYPE + " (" +
//                        _ID + INTEGER_TYPE + " PRIMARY KEY autoincrement" + COMMA_SEP +
//                        COLUMN_SUB_CATEGORY_ID + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
//                        COLUMN_TYPE_ID + INTEGER_TYPE + NOT_NULL  + COMMA_SEP +
//                        "  FOREIGN KEY ("+COLUMN_SUB_CATEGORY_ID+") REFERENCES "+TABLE_SUB_CATEGORY+"("+_ID+")" + COMMA_SEP +
//                        "  FOREIGN KEY ("+COLUMN_TYPE_ID+") REFERENCES "+TABLE_TYPE+"("+_ID+")" +
//                        ")";


        public static final String SQL_CREATE_ENTRY =
                "CREATE TABLE " + TABLE_PRODUCT + " (" +
                        _ID + INTEGER_TYPE + " PRIMARY KEY autoincrement" + COMMA_SEP +
                        COLUMN_NAME + TEXT_TYPE  + COMMA_SEP +
                        COLUMN_TYPE_ID + INTEGER_TYPE  + COMMA_SEP +
                        COLUMN_UNITS_OF_MEASURE_ID + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                        COLUMN_SUPPLIER + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                        COLUMN_PHOTO_PATH + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                        COLUMN_PRICE + REAL_TYPE + NOT_NULL + COMMA_SEP +
                        COLUMN_QUANTITY + INTEGER_TYPE + NOT_NULL + ")";


        public static final String SQL_DELETE_ENTRY =
                "DROP TABLE IF EXISTS " + TABLE_PRODUCT;

        public static final String SQL_DELETE_CATEGORY=
                "DROP TABLE IF EXISTS " + TABLE_CATEGORY;

        public static final String SQL_DELETE_SUB_CATEGORY =
                "DROP TABLE IF EXISTS " + TABLE_SUB_CATEGORY;

        public static final String SQL_DELETE_TYPE =
                "DROP TABLE IF EXISTS " + TABLE_TYPE;
//
//        public static final String SQL_DELETE_CATEGORY_TYPE =
//                "DROP TABLE IF EXISTS " + TABLE_CATEGORY_TYPE;
//
//        public static final String SQL_DELETE_SUB_CATEGORY_TYPE =
//                "DROP TABLE IF EXISTS " + TABLE_SUB_CATEGORY_TYPE;

        public static final String SQL_DELETE_CATEGORY_SUB_CATEGORY =
                "DROP TABLE IF EXISTS " + TABLE_CATEGORY_SUB_CATEGORY;

        public static final String SQL_DELETE_UNITS_OF_MEASURE =
                "DROP TABLE IF EXISTS " + TABLE_UNITS_OF_MEASURE;


    }

}