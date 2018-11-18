package com.example.yousef.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Objects;

/**
 * {@link ContentProvider} for Pets app.
 */
public class ProductProvider extends ContentProvider {
    public static final String LOG_TAG = ProductProvider.class.getSimpleName();
    /**
     * Tag for the log messages
     */
    private static final int PRODUCTS = 100;

    private static final int PRODUCTS_ID = 101;
    private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    static StoreDbHelper mDbHelper;

    static {

        matcher.addURI(StoreContract.CONTENT_AUTHORITY, StoreContract.PATH_PRODUCTS, PRODUCTS);
        matcher.addURI(StoreContract.CONTENT_AUTHORITY, StoreContract.PATH_PRODUCTS + "/#", PRODUCTS_ID);
    }

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
//         TODO: Create and initialize a PetDbHelper object to gain access to the pets database.
        mDbHelper = new StoreDbHelper(getContext());
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = matcher.match(uri);
        switch (match) {
            case PRODUCTS:
                cursor = database.query(StoreContract.ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case PRODUCTS_ID:
                selection = StoreContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(StoreContract.ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);

        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        final int match = matcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {
        String name = values.getAsString(StoreContract.ProductEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Product requires a name");
        }

        Integer price = values.getAsInteger(StoreContract.ProductEntry.COLUMN_PRICE);
        if (price == null || price < 0) {
            throw new IllegalArgumentException("Product requires valid price");
        }

        Integer quantity = values.getAsInteger(StoreContract.ProductEntry.COLUMN_QUANTITY);
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Product requires valid quantity");
        }

        String SUPPLIER_NAME = values.getAsString(StoreContract.ProductEntry.COLUMN_SUPPLIER_NAME);
        if (SUPPLIER_NAME == null) {
            throw new IllegalArgumentException("Product requires a SUPPLIER NAME");
        }
        String SUPPLIER_PHONE = values.getAsString(StoreContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
        if (SUPPLIER_PHONE == null) {
            throw new IllegalArgumentException("Product requires a SUPPLIER PHONE");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(StoreContract.ProductEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        final int match = matcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCTS_ID:
                selection = StoreContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }

    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(StoreContract.ProductEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(StoreContract.ProductEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }

        if (values.containsKey(StoreContract.ProductEntry.COLUMN_PRICE)) {
            Integer price = values.getAsInteger(StoreContract.ProductEntry.COLUMN_PRICE);
            if (price == null || price < 0) {
                throw new IllegalArgumentException("Product requires valid price");
            }
        }

        if (values.containsKey(StoreContract.ProductEntry.COLUMN_QUANTITY)) {
            Integer quantity = values.getAsInteger(StoreContract.ProductEntry.COLUMN_QUANTITY);
            if (quantity == null || quantity < 0) {
                throw new IllegalArgumentException("Product requires valid quantity");
            }
        }
        if (values.containsKey(StoreContract.ProductEntry.COLUMN_SUPPLIER_NAME)) {
            String SUPPLIER_NAME = values.getAsString(StoreContract.ProductEntry.COLUMN_SUPPLIER_NAME);
            if (SUPPLIER_NAME == null) {
                throw new IllegalArgumentException("Product requires a SUPPLIER NAME");
            }
        }
        if (values.containsKey(StoreContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER)) {
            String SUPPLIER_PHONE = values.getAsString(StoreContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            if (SUPPLIER_PHONE == null) {
                throw new IllegalArgumentException("Product requires a SUPPLIER NAME");
            }
        }


        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(StoreContract.ProductEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = matcher.match(uri);
        switch (match) {
            case PRODUCTS:
                rowsDeleted = database.delete(StoreContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCTS_ID:
                selection = StoreContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(StoreContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }


    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = matcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return StoreContract.ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCTS_ID:
                return StoreContract.ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}