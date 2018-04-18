package com.timotiusoktorio.inventoryapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.timotiusoktorio.inventoryapp.model.CategorySubCategory;
import com.timotiusoktorio.inventoryapp.model.Model;
import com.timotiusoktorio.inventoryapp.model.Product;
import com.timotiusoktorio.inventoryapp.model.SubCategoryModel;
import com.timotiusoktorio.inventoryapp.model.Type;

import java.util.ArrayList;
import java.util.List;

import static android.provider.BaseColumns._ID;
import static com.timotiusoktorio.inventoryapp.database.ProductContract.ProductEntry.COLUMN_CATEGORY_ID;
import static com.timotiusoktorio.inventoryapp.database.ProductContract.ProductEntry.COLUMN_CATEGORY_SUB_CATEGORY_ID;
import static com.timotiusoktorio.inventoryapp.database.ProductContract.ProductEntry.COLUMN_DESCRIPTION;
import static com.timotiusoktorio.inventoryapp.database.ProductContract.ProductEntry.COLUMN_IS_VALID;
import static com.timotiusoktorio.inventoryapp.database.ProductContract.ProductEntry.COLUMN_NAME;
import static com.timotiusoktorio.inventoryapp.database.ProductContract.ProductEntry.COLUMN_PHOTO_PATH;
import static com.timotiusoktorio.inventoryapp.database.ProductContract.ProductEntry.COLUMN_PRICE;
import static com.timotiusoktorio.inventoryapp.database.ProductContract.ProductEntry.COLUMN_QUANTITY;
import static com.timotiusoktorio.inventoryapp.database.ProductContract.ProductEntry.COLUMN_SUB_CATEGORY_ID;
import static com.timotiusoktorio.inventoryapp.database.ProductContract.ProductEntry.COLUMN_SUPPLIER;
import static com.timotiusoktorio.inventoryapp.database.ProductContract.ProductEntry.COLUMN_SUPPLIER_EMAIL;
import static com.timotiusoktorio.inventoryapp.database.ProductContract.ProductEntry.COLUMN_TYPE_ID;
import static com.timotiusoktorio.inventoryapp.database.ProductContract.ProductEntry.COLUMN_UNITS_OF_MEASURE_ID;
import static com.timotiusoktorio.inventoryapp.database.ProductContract.ProductEntry.SQL_CREATE_CATEGORY;
import static com.timotiusoktorio.inventoryapp.database.ProductContract.ProductEntry.SQL_CREATE_CATEGORY_SUB_CATEGORY;
import static com.timotiusoktorio.inventoryapp.database.ProductContract.ProductEntry.SQL_CREATE_ENTRY;
import static com.timotiusoktorio.inventoryapp.database.ProductContract.ProductEntry.SQL_CREATE_SUB_CATEGORY;
import static com.timotiusoktorio.inventoryapp.database.ProductContract.ProductEntry.SQL_CREATE_TYPE;
import static com.timotiusoktorio.inventoryapp.database.ProductContract.ProductEntry.SQL_CREATE_UNITS_OF_MEASURE;
import static com.timotiusoktorio.inventoryapp.database.ProductContract.ProductEntry.SQL_DELETE_CATEGORY;
import static com.timotiusoktorio.inventoryapp.database.ProductContract.ProductEntry.SQL_DELETE_CATEGORY_SUB_CATEGORY;
import static com.timotiusoktorio.inventoryapp.database.ProductContract.ProductEntry.SQL_DELETE_ENTRY;
import static com.timotiusoktorio.inventoryapp.database.ProductContract.ProductEntry.SQL_DELETE_SUB_CATEGORY;
import static com.timotiusoktorio.inventoryapp.database.ProductContract.ProductEntry.SQL_DELETE_TYPE;
import static com.timotiusoktorio.inventoryapp.database.ProductContract.ProductEntry.SQL_DELETE_UNITS_OF_MEASURE;
import static com.timotiusoktorio.inventoryapp.database.ProductContract.ProductEntry.TABLE_CATEGORY;
import static com.timotiusoktorio.inventoryapp.database.ProductContract.ProductEntry.TABLE_CATEGORY_SUB_CATEGORY;
import static com.timotiusoktorio.inventoryapp.database.ProductContract.ProductEntry.TABLE_PRODUCT;
import static com.timotiusoktorio.inventoryapp.database.ProductContract.ProductEntry.TABLE_SUB_CATEGORY;
import static com.timotiusoktorio.inventoryapp.database.ProductContract.ProductEntry.TABLE_TYPE;

/**
 * Created by Coze on 2016-08-07.
 */

public class ProductDbHelper extends SQLiteOpenHelper {
    private static final String TAG = ProductDbHelper.class.getSimpleName();
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
        sqLiteDatabase.execSQL(SQL_CREATE_CATEGORY);
        sqLiteDatabase.execSQL(SQL_CREATE_UNITS_OF_MEASURE);
        sqLiteDatabase.execSQL(SQL_CREATE_SUB_CATEGORY);
        sqLiteDatabase.execSQL(SQL_CREATE_CATEGORY_SUB_CATEGORY);
        sqLiteDatabase.execSQL(SQL_CREATE_TYPE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRY);
        sqLiteDatabase.execSQL(SQL_DELETE_CATEGORY);
        sqLiteDatabase.execSQL(SQL_DELETE_SUB_CATEGORY);
        sqLiteDatabase.execSQL(SQL_DELETE_TYPE);
        sqLiteDatabase.execSQL(SQL_DELETE_CATEGORY_SUB_CATEGORY);
        sqLiteDatabase.execSQL(SQL_DELETE_UNITS_OF_MEASURE);
        onCreate(sqLiteDatabase);
    }

    public void insertProduct(Product product) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues cv = generateContentValues(product);
            long insertId = db.insertOrThrow(TABLE_PRODUCT, null, cv);
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
        String[] columns = { _ID, COLUMN_NAME,COLUMN_TYPE_ID,COLUMN_UNITS_OF_MEASURE_ID,COLUMN_SUPPLIER, COLUMN_PHOTO_PATH, COLUMN_PRICE, COLUMN_QUANTITY };
        String orderBy = COLUMN_NAME + " ASC";
        Cursor cursor = db.query(TABLE_PRODUCT, columns, null, null, null, null, orderBy);
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
        String[] columns = { COLUMN_SUPPLIER, COLUMN_NAME };
        String selection = _ID + " = ?";
        String[] selectionArgs = { String.valueOf(product.getmId()) };
        Cursor cursor = db.query(TABLE_PRODUCT, columns, selection, selectionArgs, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                product.setmSupplier(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUPPLIER)));
                //TODO fix
//                product.setSupplierEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUPPLIER_EMAIL)));
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
            String[] whereArgs = { String.valueOf(product.getmId()) };
            int noOfRowsAffected = db.update(TABLE_PRODUCT, cv, whereClause, whereArgs);
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
            int noOfRowsAffected = db.update(TABLE_PRODUCT, cv, whereClause, whereArgs);
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
            db.delete(TABLE_PRODUCT, whereClause, whereArgs);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void deleteAllProducts() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_PRODUCT, null, null);
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
        cv.put(COLUMN_NAME, product.getmName());
        cv.put(COLUMN_TYPE_ID, product.getTypeId());
        cv.put(COLUMN_UNITS_OF_MEASURE_ID, product.getUnitOfMeasureId());
        cv.put(COLUMN_SUPPLIER, product.getmSupplier());
        cv.put(COLUMN_PHOTO_PATH, product.getmPhotoPath());
        cv.put(COLUMN_PRICE, product.getmPrice());
        cv.put(COLUMN_QUANTITY, product.getmQuantity());
        return cv;
    }

    private Product createProductFromCursor(Cursor cursor) throws IllegalArgumentException {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(_ID));
        long typeId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TYPE_ID));
        long unitOfMeasureId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_UNITS_OF_MEASURE_ID));
        String supplier = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUPPLIER));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
        String photoPath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHOTO_PATH));
        double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE));
        int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY));
        return new Product(id,typeId,unitOfMeasureId,name,photoPath,supplier,price,quantity);
    }

    public List<Model> getCategories(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM "+TABLE_CATEGORY,null);
        List<Model> categories = new ArrayList<>();
        for (int i=0;i<c.getCount();i++){
            c.moveToPosition(i);
            Model model = new Model();
            model.setmId(c.getLong(c.getColumnIndex(_ID)));
            model.setmName(c.getString(c.getColumnIndex(COLUMN_NAME)));
            model.setIsValid(c.getInt(c.getColumnIndex(COLUMN_IS_VALID))==1?true:false);

            categories.add(model);
        }

        return categories;
    }

    public List<SubCategoryModel> getSubCategories(int categoryId){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT "+TABLE_SUB_CATEGORY+"."+_ID+" as ID, "+TABLE_CATEGORY_SUB_CATEGORY+"."+_ID+" as ID2,"+COLUMN_NAME+"   FROM "+TABLE_SUB_CATEGORY+
                " INNER JOIN "+TABLE_CATEGORY_SUB_CATEGORY+" ON "+TABLE_SUB_CATEGORY+"."+_ID+" = "+TABLE_CATEGORY_SUB_CATEGORY+"."+COLUMN_SUB_CATEGORY_ID+
                " WHERE "+COLUMN_CATEGORY_ID+" = "+categoryId,null);
        List<SubCategoryModel> subCategories = new ArrayList<>();
        for (int i=0;i<c.getCount();i++){
            c.moveToPosition(i);
            SubCategoryModel subCategoryModel = new SubCategoryModel();
            subCategoryModel.setmId(c.getLong(c.getColumnIndex("ID")));
            subCategoryModel.setmCategorySubCatogoryId(c.getLong(c.getColumnIndex("ID2")));
            subCategoryModel.setmName(c.getString(c.getColumnIndex(COLUMN_NAME)));

            subCategories.add(subCategoryModel);
        }

        return subCategories;
    }

    public List<Type> getTypes(long categorySubcategoryId){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM "+TABLE_TYPE+
                " WHERE "+COLUMN_CATEGORY_SUB_CATEGORY_ID+" = "+categorySubcategoryId,null);
        List<Type> types = new ArrayList<>();
        for (int i=0;i<c.getCount();i++){
            c.moveToPosition(i);
            Type type = new Type();
            type.setmId(c.getLong(c.getColumnIndex(_ID)));
            type.setmName(c.getString(c.getColumnIndex(COLUMN_NAME)));
            type.setmDescritption(c.getString(c.getColumnIndex(COLUMN_DESCRIPTION)));
            type.setCategorySubCategoryId(c.getLong(c.getColumnIndex(COLUMN_CATEGORY_SUB_CATEGORY_ID)));
            type.setIsValid(c.getInt(c.getColumnIndex(COLUMN_IS_VALID))==1?true:false);

            types.add(type);
        }

        return types;
    }



    public void insertCategories(List<Model> models){
        Log.d(TAG,"Inserting categories");
        SQLiteDatabase db = getWritableDatabase();
        List<Model> categories = new ArrayList<>();
        for (Model model:models){
            ContentValues values = new ContentValues();
            values.put(_ID,model.getmId());
            values.put(COLUMN_NAME,model.getmName());
            values.put(COLUMN_IS_VALID,model.getIsValid());
            db.insert(TABLE_CATEGORY,null,values);
        }

    }


    public void insertSubCategories(List<Model> models){
        SQLiteDatabase db = getWritableDatabase();
        List<Model> categories = new ArrayList<>();
        for (Model model:models){
            ContentValues values = new ContentValues();
            values.put(_ID,model.getmId());
            values.put(COLUMN_NAME,model.getmName());
            values.put(COLUMN_IS_VALID,model.getIsValid());
            db.insert(TABLE_SUB_CATEGORY,null,values);
        }

    }


    public void insertCategoriesSubCategories(List<CategorySubCategory> categorySubCategories){
        SQLiteDatabase db = getWritableDatabase();
        for (CategorySubCategory categorySubCategory:categorySubCategories){
            ContentValues values = new ContentValues();
            values.put(_ID,categorySubCategory.getmId());
            values.put(COLUMN_CATEGORY_ID,categorySubCategory.getCategoryId());
            values.put(COLUMN_SUB_CATEGORY_ID,categorySubCategory.getSubCategoryId());
            db.insert(TABLE_CATEGORY_SUB_CATEGORY,null,values);
        }

    }

    public void insertTypes(List<Type> typeList){
        SQLiteDatabase db = getWritableDatabase();
        for (Type type:typeList){
            ContentValues values = new ContentValues();
            values.put(_ID,type.getmId());
            values.put(COLUMN_NAME,type.getmName());
            values.put(COLUMN_DESCRIPTION,type.getmDescritption());
            values.put(COLUMN_CATEGORY_SUB_CATEGORY_ID,type.getCategorySubCategoryId());
            values.put(COLUMN_IS_VALID,1);
            db.insert(TABLE_TYPE,null,values);
        }

    }


}