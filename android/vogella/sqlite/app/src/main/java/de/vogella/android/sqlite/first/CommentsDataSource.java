package de.vogella.android.sqlite.first;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by systemovich on 2/15/17.
 */

public class CommentsDataSource {
    private SQLiteDatabase db;
    private MySQLiteHelper dbHelper;

    private String[] allColumns = {
            MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_COMMENT
    };

    private Comment cursorToComment(Cursor cursor) {
        Comment comment = new Comment();
        comment.setId(cursor.getLong(0));
        comment.setComment(cursor.getString(1));

        return comment;
    }

    public CommentsDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Comment createComment(String comment) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_COMMENT, comment);
        long insertId = db.insert(MySQLiteHelper.TABLE_COMMENTS, null, values);

        Cursor cursor = db.query(
            MySQLiteHelper.TABLE_COMMENTS,
            allColumns,
            MySQLiteHelper.COLUMN_ID +  " = " + insertId,
            null,
            null,
            null,
            null
        );

        cursor.moveToFirst();
        Comment newComment = cursorToComment(cursor);
        cursor.close();

        return newComment;
    }

    public List<Comment> getAllComments() {
        List<Comment> comments = new ArrayList<>();
        Cursor cursor = db.query(MySQLiteHelper.TABLE_COMMENTS, allColumns, null, null, null, null, null);
        cursor.moveToFirst();

        while (! cursor.isAfterLast()) {
            Comment comment = cursorToComment(cursor);
            comments.add(comment);
            cursor.moveToNext();
        }

        cursor.close();

        return comments;
    }

    public void deleteComment(Comment comment) {
        long id = comment.getId();
        System.out.println(String.format("Comment with id %d deleted.", id));

        db.delete(
            MySQLiteHelper.TABLE_COMMENTS,
            MySQLiteHelper.COLUMN_ID + " = ? ",
            new String[] {String.valueOf(id)}
        );
    }
}
