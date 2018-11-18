package com.example.yousef.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class StoreDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "our_products.db";
    private static final int DATABASE_VERSION = 1;

     StoreDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_ENTRIES = "CREATE TABLE " + StoreContract.ProductEntry.TABLE_NAME + " (" +
                StoreContract.ProductEntry._ID + " INTEGER PRIMARY KEY," +
                StoreContract.ProductEntry.COLUMN_PRODUCT_NAME + " TEXT," +
                StoreContract.ProductEntry.COLUMN_PRICE + " INTEGER," +
                StoreContract.ProductEntry.COLUMN_QUANTITY + " INTEGER," +
                StoreContract.ProductEntry.COLUMN_SUPPLIER_NAME + " TEXT," +
                StoreContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " TEXT)";
        Log.d("debug", SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + StoreContract.ProductEntry.TABLE_NAME);
        onCreate(db);
    }
}
