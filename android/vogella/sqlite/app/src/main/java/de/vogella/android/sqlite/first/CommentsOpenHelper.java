package de.vogella.android.sqlite.first;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Creates the "comments.db" database if it does not exist, upgrades or downgrades it, and creates
 * the database tables.
 */
public class CommentsOpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "comments.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_COMMENTS = "comments";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_COMMENT = "comment";

    public static final String CREATE_TABLE_COMMENTS = "" +
        "CREATE TABLE " + TABLE_COMMENTS + " ( " +
        COLUMN_ID + " INTEGER  PRIMARY KEY, " +
        COLUMN_COMMENT + " TEXT NOT NULL " +
        " ); ";

    public CommentsOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
       db.execSQL(CREATE_TABLE_COMMENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(
            CommentsOpenHelper.class.getName(),
            String.format(
                "Upgrading database from version %d to %d, which will destroy all old data.",
                oldVersion,
                newVersion
            )
        );

        db.execSQL(String.format("DROP TABLE IF EXISTS %s", TABLE_COMMENTS));
        onCreate(db);
    }
}
