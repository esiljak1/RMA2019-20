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

    public static final String TRANSACTION_TABLE = "transactions";
    public static final String TRANSACTION_INTERNAL_ID = "internalId";
    public static final String TRANSACTION_ID = "id";
    public static final String TRANSACTION_DATE = "date";
    public static final String TRANSACTION_AMOUNT = "amount";
    public static final String TRANSACTION_TITLE = "title";
    public static final String TRANSACTION_TYPE = "type";
    public static final String TRANSACTION_ITEM_DESCRIPTION = "itemDescription";
    public static final String TRANSACTION_INTERVAL = "transactionInterval";
    public static final String TRANSACTION_END_DATE = "transactionEndDate";
    private static final String TRANSACTION_TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TRANSACTION_TABLE + " (" + TRANSACTION_INTERNAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TRANSACTION_ID + " INTEGER UNIQUE, "
            + TRANSACTION_DATE + " DATE NOT NULL, "
            + TRANSACTION_AMOUNT + " REAL NOT NULL, "
            + TRANSACTION_TITLE + " TEXT NOT NULL, "
            + TRANSACTION_TYPE + " INTEGER NOT NULL, "
            + TRANSACTION_ITEM_DESCRIPTION + " TEXT, "
            + TRANSACTION_INTERVAL + " INTEGER, "
            + TRANSACTION_END_DATE + " DATE);";
    private static final String TRANSACTION_DROP = "DROP TABLE IF EXISTS " + TRANSACTION_TABLE;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TRANSACTION_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(TRANSACTION_DROP);
        onCreate(db);
    }
}
