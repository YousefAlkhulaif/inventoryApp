package com.example.yousef.inventoryapp;


import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.yousef.inventoryapp.data.StoreContract;


public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final int PRODUCT_LOADER = 0;
    MyAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn_addProduct = findViewById(R.id.btn_addProduct);
        TextView txt_emptyList = findViewById(R.id.txt_empty_list);
        btn_addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ItemActivity.class);
                ItemActivity.activityMode = ItemActivity.ACTIVITY_MODE_ADD;
                startActivity(i);
            }
        });
        final ListView lst_items = findViewById(R.id.lst_items);
        lst_items.setEmptyView(txt_emptyList);
        cursorAdapter = new MyAdapter(this, null);
        lst_items.setAdapter(cursorAdapter);
        lst_items.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemActivity.activityMode = ItemActivity.ACTIVITY_MODE_SHOW;
                Intent intent = new Intent(MainActivity.this, ItemActivity.class);
                Uri currentProductUri = ContentUris.withAppendedId(StoreContract.ProductEntry.CONTENT_URI, id);

                intent.setData(currentProductUri);
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                StoreContract.ProductEntry._ID,
                StoreContract.ProductEntry.COLUMN_PRODUCT_NAME,
                StoreContract.ProductEntry.COLUMN_PRICE,
                StoreContract.ProductEntry.COLUMN_QUANTITY,
                StoreContract.ProductEntry.COLUMN_SUPPLIER_NAME,
                StoreContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER
        };
        return new CursorLoader(this, StoreContract.ProductEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

}
