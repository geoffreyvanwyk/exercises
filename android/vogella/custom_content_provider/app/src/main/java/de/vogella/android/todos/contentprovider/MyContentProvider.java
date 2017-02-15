package de.vogella.android.todos.contentprovider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.HashSet;

import de.vogella.android.todos.database.TodoOpenHelper;
import de.vogella.android.todos.database.TodoTable;

public class MyContentProvider extends ContentProvider {

    private TodoOpenHelper dbHelper;
    private static final int TODOS = 10;
    private static final int TODO_ID = 20;
    private static final String AUTHORITY = "de.vogella.android.todos.contentprovider";
    private static final String BASE_PATH = "todos";
    private static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/todos";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/todo";
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, TODOS);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", TODO_ID);
    }

    private void checkColumns(String[] projection) {
        String[] available = {
                TodoTable.COLUMN_ID,
                TodoTable.COLUMN_CATEGORY,
                TodoTable.COLUMN_SUMMARY,
                TodoTable.COLUMN_DESCRIPTION
        };

        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<>(Arrays.asList(available));

            if (! availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection.");
            }
        }
    }

    public MyContentProvider() {
    }

    @Override
    public boolean onCreate() {
        dbHelper = new TodoOpenHelper(getContext());
        return false;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = 0;

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case TODOS:
                id = db.insert(TodoTable.TABLE_TODO, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        checkColumns(projection);
        queryBuilder.setTables(TodoTable.TABLE_TODO);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case TODOS:
                break;
            case TODO_ID:
                queryBuilder.appendWhere(TodoTable.COLUMN_ID + " = " + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsUpdated = 0;
        int uriType = sURIMatcher.match(uri);

        switch (uriType) {
            case TODOS:
                rowsUpdated = db.update(TodoTable.TABLE_TODO, values, selection, selectionArgs);
                break;
            case TODO_ID:
                String id = uri.getLastPathSegment();

                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(
                        TodoTable.TABLE_TODO,
                        values,
                        TodoTable.COLUMN_ID + " = " + id,
                        null
                    );
                }  else {
                    rowsUpdated = db.update(
                            TodoTable.TABLE_TODO,
                            values,
                            TodoTable.COLUMN_ID + " = " + id + " and " + selection,
                            selectionArgs
                    );
                }

                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted = 0;

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case TODOS:
                rowsDeleted = db.delete(TodoTable.TABLE_TODO, selection, selectionArgs);
                break;
            case TODO_ID:
                String id = uri.getLastPathSegment();

                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(TodoTable.TABLE_TODO, TodoTable.COLUMN_ID + " = " + id, null);
                } else {
                    rowsDeleted = db.delete(
                        TodoTable.TABLE_TODO,
                        TodoTable.COLUMN_ID + " = " + id + " and " + selection, selectionArgs
                    );
                }

                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }
}
