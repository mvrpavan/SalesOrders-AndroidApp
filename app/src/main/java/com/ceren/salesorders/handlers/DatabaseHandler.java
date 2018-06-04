package com.ceren.salesorders.handlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ceren.salesorders.models.TransactionDetails;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "TransactionHistory",
            KEY_ID = "id",
            COL_CREATE_TIME = "CreateTime", COL_UPDATE_TIME = "UpdateTime", COL_BILL_NUMBER = "BillNumber",
            COL_SELLER_NAME = "SellerName", COL_SALE_AMOUNT = "SaleAmount", COL_CANCEL_AMOUNT = "CancelAmount",
            COL_RETURN_AMOUNT = "ReturnAmount", COL_DISCOUNT_AMOUNT = "DiscountAmount", COL_TOTAL_TAX_AMOUNT = "TotalTaxAmount",
            COL_NET_SALE_AMOUNT = "NetSaleAmount", COL_OLD_BALANCE_AMOUNT = "OBAmount", COL_CASH_PAID_AMOUNT = "CashPaidAmount",
            COL_BALANCE_AMOUNT = "BalanceAmount";
    private String TABLE_RECENT;

    public DatabaseHandler(Context context, String UID) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        TABLE_RECENT = String.format("Recent%s", UID);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_RECENT + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_CREATE_TIME + " TEXT, " + COL_UPDATE_TIME + " TEXT, " + COL_BILL_NUMBER + " TEXT, "
                + COL_SELLER_NAME + " TEXT, " + COL_SALE_AMOUNT + " FLOAT, " + COL_CANCEL_AMOUNT + " FLOAT, "
                + COL_RETURN_AMOUNT + " FLOAT, " + COL_DISCOUNT_AMOUNT + " FLOAT, " + COL_TOTAL_TAX_AMOUNT + " FLOAT, "
                + COL_NET_SALE_AMOUNT + " FLOAT, " + COL_OLD_BALANCE_AMOUNT + " FLOAT, " + COL_CASH_PAID_AMOUNT + " FLOAT, "
                + COL_BALANCE_AMOUNT + " FLOAT"
                + ")");
        Log.d("DatabaseHandler", String.format("CreateTable: %s Table Created", TABLE_RECENT));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECENT);
        onCreate(db);
        Log.d("DatabaseHandler", String.format("CreateTable: %s Table Recreated", TABLE_RECENT));
    }

    public void addTransactionDetailsToRecent(TransactionDetails transactionDetails) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COL_CREATE_TIME, transactionDetails.getCreateTime().toString());
        values.put(COL_UPDATE_TIME, transactionDetails.getUpdateTime().toString());
        values.put(COL_BILL_NUMBER, transactionDetails.getBillNumber());
        values.put(COL_SELLER_NAME, transactionDetails.getSellerName());
        values.put(COL_SALE_AMOUNT, transactionDetails.getSaleAmount());
        values.put(COL_CANCEL_AMOUNT, transactionDetails.getCancelAmount());
        values.put(COL_RETURN_AMOUNT, transactionDetails.getReturnAmount());
        values.put(COL_DISCOUNT_AMOUNT, transactionDetails.getDiscountAmount());
        values.put(COL_TOTAL_TAX_AMOUNT, transactionDetails.getTotalTaxAmount());
        values.put(COL_NET_SALE_AMOUNT, transactionDetails.getNetSaleAmount());
        values.put(COL_OLD_BALANCE_AMOUNT, transactionDetails.getOBAmount());
        values.put(COL_CASH_PAID_AMOUNT, transactionDetails.getCashPaidAmount());
        values.put(COL_BALANCE_AMOUNT, transactionDetails.getBalanceAmount());

        db.insert(TABLE_RECENT, null, values);
        db.close();
    }

    public TransactionDetails getTransactionDetails(int id) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_RECENT,
                new String[] { KEY_ID, COL_CREATE_TIME, COL_UPDATE_TIME, COL_BILL_NUMBER, COL_SELLER_NAME, COL_SALE_AMOUNT,
                        COL_CANCEL_AMOUNT, COL_RETURN_AMOUNT, COL_DISCOUNT_AMOUNT, COL_TOTAL_TAX_AMOUNT, COL_NET_SALE_AMOUNT,
                        COL_OLD_BALANCE_AMOUNT, COL_CASH_PAID_AMOUNT, COL_BALANCE_AMOUNT },
               KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null );

        if (cursor != null) {
            cursor.moveToFirst();

            TransactionDetails transactionDetails = new TransactionDetails();
            transactionDetails.setID(cursor.getInt(0));
            transactionDetails.setCreateTime(cursor.getString(1));
            transactionDetails.setUpdateTime(cursor.getString(2));
            transactionDetails.setBillNumber(cursor.getString(3));
            transactionDetails.setSellerName(cursor.getString(4));
            transactionDetails.setSaleAmount(cursor.getDouble(5));
            transactionDetails.setCancelAmount(cursor.getDouble(6));
            transactionDetails.setReturnAmount(cursor.getDouble(7));
            transactionDetails.setDiscountAmount(cursor.getDouble(8));
            transactionDetails.setTotalTaxAmount(cursor.getDouble(9));
            transactionDetails.setNetSaleAmount(cursor.getDouble(10));
            transactionDetails.setOBAmount(cursor.getDouble(11));
            transactionDetails.setCashPaidAmount(cursor.getDouble(12));
            transactionDetails.setBalanceAmount(cursor.getDouble(13));

            db.close();
            cursor.close();

            return transactionDetails;
        }

        return null;
    }

    public List<TransactionDetails> getAllTransactions() {
        List<TransactionDetails> ListTransactionDetails = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RECENT, null);

        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                TransactionDetails transactionDetails = new TransactionDetails();
                transactionDetails.setID(cursor.getInt(0));
                transactionDetails.setCreateTime(cursor.getString(1));
                transactionDetails.setUpdateTime(cursor.getString(2));
                transactionDetails.setBillNumber(cursor.getString(3));
                transactionDetails.setSellerName(cursor.getString(4));
                transactionDetails.setSaleAmount(cursor.getDouble(5));
                transactionDetails.setCancelAmount(cursor.getDouble(6));
                transactionDetails.setReturnAmount(cursor.getDouble(7));
                transactionDetails.setDiscountAmount(cursor.getDouble(8));
                transactionDetails.setTotalTaxAmount(cursor.getDouble(9));
                transactionDetails.setNetSaleAmount(cursor.getDouble(10));
                transactionDetails.setOBAmount(cursor.getDouble(11));
                transactionDetails.setCashPaidAmount(cursor.getDouble(12));
                transactionDetails.setBalanceAmount(cursor.getDouble(13));

                ListTransactionDetails.add(transactionDetails);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return ListTransactionDetails;
    }

    public void deleteTransactionDetails(TransactionDetails transactionDetails) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_RECENT, KEY_ID + "=?", new String[]{String.valueOf(transactionDetails.getID())});
        db.close();
    }

    public void deleteAllTransactionDetails() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='" + TABLE_RECENT + "';", null);
        if (cursor.getCount() > 0) {
            db.execSQL("DELETE FROM " + TABLE_RECENT);
        }
        db.close();
    }

    public int getTransactionDetailsCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RECENT, null);
        int count = cursor.getCount();
        db.close();
        cursor.close();

        return count;
    }
}
