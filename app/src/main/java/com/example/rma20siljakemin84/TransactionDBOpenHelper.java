package com.example.rma20siljakemin84;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class TransactionDBOpenHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "RMASpirala.db";
    public static final int DATABASE_VERSION = 1;

    public TransactionDBOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public TransactionDBOpenHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static final String CREATED_TRANSACTIONS_TABLE = "createdTransactions";
    public static final String TRANSACTION_INTERNAL_ID = "internalId";
    public static final String TRANSACTION_ID = "id";
    public static final String TRANSACTION_DATE = "date";
    public static final String TRANSACTION_AMOUNT = "amount";
    public static final String TRANSACTION_TITLE = "title";
    public static final String TRANSACTION_TYPE = "type";
    public static final String TRANSACTION_ITEM_DESCRIPTION = "itemDescription";
    public static final String TRANSACTION_INTERVAL = "transactionInterval";
    public static final String TRANSACTION_END_DATE = "transactionEndDate";
    private static final String CREATED_TRANSACTIONS_TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + CREATED_TRANSACTIONS_TABLE + " (" + TRANSACTION_INTERNAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TRANSACTION_ID + " INTEGER UNIQUE, "
            + TRANSACTION_DATE + " DATE NOT NULL, "
            + TRANSACTION_AMOUNT + " REAL NOT NULL, "
            + TRANSACTION_TITLE + " TEXT NOT NULL, "
            + TRANSACTION_TYPE + " INTEGER NOT NULL, "
            + TRANSACTION_ITEM_DESCRIPTION + " TEXT, "
            + TRANSACTION_INTERVAL + " INTEGER, "
            + TRANSACTION_END_DATE + " DATE);";
    private static final String CREATED_TRANSACTIONS_DROP = "DROP TABLE IF EXISTS " + CREATED_TRANSACTIONS_TABLE;

    public static final String UPDATED_TRANSACTIONS_TABLE = "updatedTransactions";
    private static final String UPDATED_TRANSACTIONS_TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + UPDATED_TRANSACTIONS_TABLE + " (" + TRANSACTION_INTERNAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + TRANSACTION_ID + " INTEGER UNIQUE, "
                    + TRANSACTION_DATE + " DATE NOT NULL, "
                    + TRANSACTION_AMOUNT + " REAL NOT NULL, "
                    + TRANSACTION_TITLE + " TEXT NOT NULL, "
                    + TRANSACTION_TYPE + " INTEGER NOT NULL, "
                    + TRANSACTION_ITEM_DESCRIPTION + " TEXT, "
                    + TRANSACTION_INTERVAL + " INTEGER, "
                    + TRANSACTION_END_DATE + " DATE);";
    private static final String UPDATED_TRANSACTIONS_DROP = "DROP TABLE IF EXISTS " + UPDATED_TRANSACTIONS_TABLE;

    public static final String DELETED_TRANSACTIONS_TABLE = "deletedTransactionsTable";
    private static final String DELETED_TRANSACTIONS_TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + DELETED_TRANSACTIONS_TABLE + " (" + TRANSACTION_INTERNAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TRANSACTION_ID + " INTEGER UNIQUE, "
                    + TRANSACTION_DATE + " DATE NOT NULL, "
                    + TRANSACTION_AMOUNT + " REAL NOT NULL, "
                    + TRANSACTION_TITLE + " TEXT NOT NULL, "
                    + TRANSACTION_TYPE + " INTEGER NOT NULL, "
                    + TRANSACTION_ITEM_DESCRIPTION + " TEXT, "
                    + TRANSACTION_INTERVAL + " INTEGER, "
                    + TRANSACTION_END_DATE + " DATE);";
    private static final String DELETED_TRANSACTIONS_DROP = "DROP TABLE IF EXISTS " + DELETED_TRANSACTIONS_TABLE;

    public static final String ACCOUNT_TABLE = "account";
    public static final String ACCOUNT_INTERNAL_ID = "internalId";
    public static final String ACCOUNT_ID = "id";
    public static final String ACCOUNT_AMOUNT = "amount";
    public static final String ACCOUNT_TOTAL_LIMIT = "totalLimit";
    public static final String ACCOUNT_MONTH_LIMIT = "monthLimit";
    private static final String ACCOUNT_TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + ACCOUNT_TABLE + " (" + ACCOUNT_INTERNAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ACCOUNT_ID + " TEXT UNIQUE, "
            + ACCOUNT_AMOUNT + " REAL, "
            + ACCOUNT_TOTAL_LIMIT + " REAL, "
            + ACCOUNT_MONTH_LIMIT + " REAL);";
    private static final String ACCOUNT_DROP = "DROP TABLE IF EXISTS " + ACCOUNT_TABLE;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATED_TRANSACTIONS_TABLE_CREATE);
        db.execSQL(UPDATED_TRANSACTIONS_TABLE_CREATE);
        db.execSQL(DELETED_TRANSACTIONS_TABLE_CREATE);
        db.execSQL(ACCOUNT_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(CREATED_TRANSACTIONS_DROP);
        db.execSQL(UPDATED_TRANSACTIONS_DROP);
        db.execSQL(DELETED_TRANSACTIONS_DROP);
        db.execSQL(ACCOUNT_DROP);
        onCreate(db);
    }
}
