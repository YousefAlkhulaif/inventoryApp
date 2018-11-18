package com.example.yousef.inventoryapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.yousef.inventoryapp.data.StoreContract;

public class ItemActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int ACTIVITY_MODE_ADD = 0;
    public static final int ACTIVITY_MODE_SHOW = 1;
    public static final int ACTIVITY_MODE_EDIT = 2;
    private static final int REQUEST_PHONE_CALL = 1;
    private static final int EXISTING_Product_LOADER = 0;
    public static int activityMode;
    Uri currentUri;
    EditText edTxt_productName;
    EditText edTxt_price;
    EditText edTxt_quantity;
    EditText edTxt_supplierName;
    EditText edTxt_supplierPhoneNumber;
    Button btn_upQuantity, btn_downQuantity, btn_callSeller, btn_save, btn_edit, btn_delete, btn_saveChanges;
    String currProductName, currSupplierName, currSupplierPhoneNumber;
    int currPrice, currQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        findViews();
        Intent intent = getIntent();
        currentUri = intent.getData();
        changeActivityMode();
        btn_upQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int counterForAdding = Integer.parseInt(edTxt_quantity.getText() + "");
                    counterForAdding++;
                    edTxt_quantity.setText(counterForAdding + "");
                } catch (Exception e) {
                    Toast.makeText(ItemActivity.this, "Invalid Data",
                            Toast.LENGTH_SHORT).show();
                    edTxt_quantity.setText(0 + "");
                }
            }
        });
        btn_downQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int conterForSelling = Integer.parseInt(edTxt_quantity.getText() + "");
                    if (conterForSelling > 0) {
                        conterForSelling--;
                    }
                    edTxt_quantity.setText(conterForSelling + "");
                } catch (Exception e) {
                    Toast.makeText(ItemActivity.this, "Invalid Data",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog diaBox = AskOption();
                diaBox.show();
            }
        });
        if (currentUri != null)
            getLoaderManager().initLoader(EXISTING_Product_LOADER, null, this);
        btn_callSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + edTxt_supplierPhoneNumber.getText() + ""));
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(ItemActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                    } else {
                        startActivity(intent);
                    }
                } else
                    startActivity(intent);
            }
        });
    }

    private void changeActivityMode() {
        switch (activityMode) {
            case ACTIVITY_MODE_ADD:
                addActivity();
                break;
            case ACTIVITY_MODE_SHOW:
                showActivity();
                break;
            case ACTIVITY_MODE_EDIT:
                editActivity();
                break;
        }
    }

    private void editActivity() {
        btn_upQuantity.setVisibility(View.VISIBLE);
        btn_downQuantity.setVisibility(View.VISIBLE);
        btn_save.setVisibility(View.VISIBLE);
        btn_delete.setVisibility(View.GONE);
        btn_callSeller.setVisibility(View.GONE);
        btn_saveChanges.setVisibility(View.VISIBLE);
        btn_edit.setVisibility(View.GONE);
        edTxt_productName.setEnabled(true);
        edTxt_price.setEnabled(true);
        edTxt_quantity.setEnabled(true);
        edTxt_supplierName.setEnabled(true);
        edTxt_supplierPhoneNumber.setEnabled(true);

        btn_saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                try {
                    takingInputs();
                } catch (Exception ignored) {
                    Toast.makeText(ItemActivity.this, "make sure all fields is not empty.", Toast.LENGTH_SHORT).show();
                }
                values.put(StoreContract.ProductEntry.COLUMN_PRODUCT_NAME, currProductName);
                values.put(StoreContract.ProductEntry.COLUMN_PRICE, currPrice);
                values.put(StoreContract.ProductEntry.COLUMN_QUANTITY, currQuantity);
                values.put(StoreContract.ProductEntry.COLUMN_SUPPLIER_NAME, currSupplierName);
                values.put(StoreContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER, currSupplierPhoneNumber);

                int rowsAffected = getContentResolver().update(currentUri, values, null, null);

                if (rowsAffected == 0) {
                    Toast.makeText(ItemActivity.this, "Couldn't Update",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ItemActivity.this, "Update is Done",
                            Toast.LENGTH_SHORT).show();
                    activityMode = ACTIVITY_MODE_SHOW;
                    changeActivityMode();
                }
            }
        });
    }

    private void showActivity() {
        btn_save.setVisibility(View.GONE);
        btn_downQuantity.setVisibility(View.GONE);
        btn_upQuantity.setVisibility(View.GONE);
        btn_callSeller.setVisibility(View.VISIBLE);
        btn_edit.setVisibility(View.VISIBLE);
        btn_delete.setVisibility(View.VISIBLE);
        btn_saveChanges.setVisibility(View.GONE);
        edTxt_productName.setEnabled(false);
        edTxt_price.setEnabled(false);
        edTxt_quantity.setEnabled(false);
        edTxt_supplierName.setEnabled(false);
        edTxt_supplierPhoneNumber.setEnabled(false);
        getLoaderManager().initLoader(EXISTING_Product_LOADER, null, this);

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityMode = ACTIVITY_MODE_EDIT;
                changeActivityMode();
            }
        });
    }

    private void addActivity() {
        btn_edit.setVisibility(View.GONE);
        btn_edit.setVisibility(View.GONE);
        btn_save.setVisibility(View.VISIBLE);
        btn_callSeller.setVisibility(View.GONE);
        btn_delete.setVisibility(View.GONE);
        btn_saveChanges.setVisibility(View.GONE);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addProduct()) {
                    Intent intent = new Intent(ItemActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(ItemActivity.this, "Invalid Data, try filling all fields with correct data",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void findViews() {
        edTxt_productName = findViewById(R.id.edTxt_productName_show);
        edTxt_price = findViewById(R.id.edTxt_price_show);
        edTxt_quantity = findViewById(R.id.edTxt_quantity_show);
        edTxt_supplierName = findViewById(R.id.edTxt_supplier_name_show);
        edTxt_supplierPhoneNumber = findViewById(R.id.edTxt_supplier_phone_number_show);
        btn_upQuantity = findViewById(R.id.btn_up_quantity_show);
        btn_downQuantity = findViewById(R.id.btn_down_quantity_show);
        btn_callSeller = findViewById(R.id.btn_call_phone_show);
        btn_edit = findViewById(R.id.btn_edit_product);
        btn_save = findViewById(R.id.btn_save);
        btn_delete = findViewById(R.id.btn_delete);
        btn_saveChanges = findViewById(R.id.btn_saveChanges);
    }


    private boolean addProduct() {
        try {
            takingInputs();
            if ((TextUtils.isEmpty(currProductName)) || (TextUtils.isEmpty(currPrice + ""))
                    || (TextUtils.isEmpty(currProductName)) || (TextUtils.isEmpty(currQuantity + ""))
                    || (TextUtils.isEmpty(currSupplierName)) || (TextUtils.isEmpty(currSupplierPhoneNumber + ""))) {
                return false;
            }

            ContentValues values = new ContentValues();
            values.put(StoreContract.ProductEntry.COLUMN_PRODUCT_NAME, currProductName);
            values.put(StoreContract.ProductEntry.COLUMN_PRICE, currPrice);
            values.put(StoreContract.ProductEntry.COLUMN_QUANTITY, currQuantity);
            values.put(StoreContract.ProductEntry.COLUMN_SUPPLIER_NAME, currSupplierName);
            values.put(StoreContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER, currSupplierPhoneNumber);
            if (currentUri == null) {
                Uri newUri = getContentResolver().insert(StoreContract.ProductEntry.CONTENT_URI, values);
                if (newUri == null) {
                    Toast.makeText(this, "Couldn't Add",
                            Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    Toast.makeText(this, "Product Added",
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }

    }

    private AlertDialog AskOption() {
        return new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you want to Delete")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        getContentResolver().delete(currentUri, null, null);
                        Toast.makeText(ItemActivity.this, "Product has been deleted.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ItemActivity.this, MainActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }

                })


                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();

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
        return new CursorLoader(this, currentUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1) {
            return;
        }
        if (data.moveToFirst()) {
            String currProductName = data.getString(data.getColumnIndex(StoreContract.ProductEntry.COLUMN_PRODUCT_NAME));
            int currPrice = data.getInt(data.getColumnIndex(StoreContract.ProductEntry.COLUMN_PRICE));
            int currQuantity = data.getInt(data.getColumnIndex(StoreContract.ProductEntry.COLUMN_QUANTITY));
            String currSupplierName = data.getString(data.getColumnIndex(StoreContract.ProductEntry.COLUMN_SUPPLIER_NAME));
            String currSupplierPhoneNumber = data.getString(data.getColumnIndex(StoreContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER));
            edTxt_productName.setText(currProductName);
            edTxt_price.setText(currPrice + "");
            edTxt_quantity.setText(currQuantity + "");
            edTxt_supplierName.setText(currSupplierName);
            edTxt_supplierPhoneNumber.setText(currSupplierPhoneNumber);
        }
    }

    void  takingInputs() throws Exception {
        try {
            currProductName = edTxt_productName.getText().toString().trim();
            currPrice = Integer.parseInt(edTxt_price.getText().toString().trim());
            currQuantity = Integer.parseInt(edTxt_quantity.getText().toString().trim());
            currSupplierName = edTxt_supplierName.getText().toString().trim();
            currSupplierPhoneNumber = edTxt_supplierPhoneNumber.getText().toString().trim();
        } catch (Exception e) {
            throw new Exception("error Inputs");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        String empty = "";
        edTxt_productName.setText(empty);
        edTxt_price.setText(empty);
        edTxt_quantity.setText(empty);
        edTxt_supplierName.setText(empty);
        edTxt_supplierPhoneNumber.setText(empty);
    }
}