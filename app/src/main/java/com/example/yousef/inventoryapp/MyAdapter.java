package com.example.yousef.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yousef.inventoryapp.data.StoreContract;


public class MyAdapter extends CursorAdapter {


     MyAdapter(Context context, Cursor c) {

        super(context, c, 0);
    }

    @NonNull


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item, parent, false);
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        final int currID = cursor.getInt(cursor.getColumnIndex(StoreContract.ProductEntry._ID));
        final String currProductName = cursor.getString(cursor.getColumnIndex(StoreContract.ProductEntry.COLUMN_PRODUCT_NAME));
        final int currPrice = cursor.getInt(cursor.getColumnIndex(StoreContract.ProductEntry.COLUMN_PRICE));
        final int currQuantity = cursor.getInt(cursor.getColumnIndex(StoreContract.ProductEntry.COLUMN_QUANTITY));
        final String currSupplierName = cursor.getString(cursor.getColumnIndex(StoreContract.ProductEntry.COLUMN_SUPPLIER_NAME));
        final String currSupplierPhoneNumber = cursor.getString(cursor.getColumnIndex(StoreContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER));
        TextView txt_productName = view.findViewById(R.id.txt_product_name);
        TextView txt_price = view.findViewById(R.id.txt_price);
        TextView txt_quantity = view.findViewById(R.id.txt_quantity);
        Button btn_sale = view.findViewById(R.id.btn_sale);

        btn_sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currQuantity <= 1){
                    Toast.makeText(context,context.getString(R.string.txt_sold_out) , Toast.LENGTH_SHORT).show();
                    view.setVisibility(View.GONE);
                    return;
                }

                ContentValues values = new ContentValues();
                values.put(StoreContract.ProductEntry.COLUMN_PRODUCT_NAME, currProductName);
                values.put(StoreContract.ProductEntry.COLUMN_PRICE, currPrice);
                values.put(StoreContract.ProductEntry.COLUMN_QUANTITY, currQuantity - 1);
                values.put(StoreContract.ProductEntry.COLUMN_SUPPLIER_NAME, currSupplierName);
                values.put(StoreContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER, currSupplierPhoneNumber);
                Uri currentProductUri = ContentUris.withAppendedId(StoreContract.ProductEntry.CONTENT_URI, currID);
                context.getContentResolver().update(currentProductUri, values, null, null);
                bindView(view, context, cursor);
            }
        });

        txt_productName.setText(currProductName);
        txt_price.setText(currPrice + "");
        txt_quantity.setText(currQuantity + "");
    }

}
