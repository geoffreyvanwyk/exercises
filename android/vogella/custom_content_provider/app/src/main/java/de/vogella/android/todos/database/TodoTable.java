package de.vogella.android.todos.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Creates and drops the "todos" database table.
 */

public class TodoTable {
    private static final String TAG = "TodoTable";

    public static final String TABLE_TODO = "todo";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_SUMMARY = "summary";
    public static final String COLUMN_DESCRIPTION = "description";

    private static final String CREATE_TABLE = ""  +
        "CREATE TABLE " + TABLE_TODO + " ( " +
        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
        COLUMN_CATEGORY + " TEXT NOT NULL, " +
        COLUMN_SUMMARY + " TEXT NOT NULL, " +
        COLUMN_DESCRIPTION + " TEXT NOT NULL " +
        " ); ";

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_TODO;

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(
            TAG,
            "Upgrading database from version " + oldVersion + " to " + newVersion
            + ", which will destroy all old data."
        );

        db.execSQL(DROP_TABLE);
        onCreate(db);
    }
}
