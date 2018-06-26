package com.ciberdictionary.ciberdictionary.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by inmobitec on 6/24/18.
 */

public class DbConnection extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ciberDictionary.db";

    //Table Name
    public static final String TABLE_WORD = "word_table";

    //Table Columns
    public static final String COLUMN_ID="id";
    public static final String COLUMN_WORD="word";
    public static final String COLUMN_TYPE="type";
    public static final String COLUMN_DEFINITION="definition";
    public static final String COLUMN_EXAMPLE="example";

    public static final String CREATE_TABLE_WORD = "CREATE TABLE "
            + TABLE_WORD + "(" + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_WORD
            + " TEXT, " + COLUMN_TYPE + " TEXT, " + COLUMN_DEFINITION + " TEXT, " + COLUMN_EXAMPLE + " TEXT" + ")";


    public DbConnection(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_WORD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORD);
        onCreate(db);
    }
}
