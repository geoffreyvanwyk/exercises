package de.vogella.android.sqlite.first;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Data access object (DAO) for executing INSERT, SELECT, and DELETE SQL queries against the
 * "comments" database table.
 *
 * When SELECTing or INSERTing, the relevant methods return Comment objects. It manages the
 * connection to the database via indirectly via the CommentsOpenHelper class.
 */
public class CommentsDataSource {
    private SQLiteDatabase db;
    private CommentsOpenHelper dbHelper;

    private String[] allColumns = {
            CommentsOpenHelper.COLUMN_ID,
            CommentsOpenHelper.COLUMN_COMMENT
    };

    private Comment cursorToComment(Cursor cursor) {
        Comment comment = new Comment();
        comment.setId(cursor.getLong(0));
        comment.setComment(cursor.getString(1));

        return comment;
    }

    public CommentsDataSource(Context context) {
        dbHelper = new CommentsOpenHelper(context);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Comment createComment(String comment) {
        ContentValues values = new ContentValues();
        values.put(CommentsOpenHelper.COLUMN_COMMENT, comment);
        long insertId = db.insert(CommentsOpenHelper.TABLE_COMMENTS, null, values);

        Cursor cursor = db.query(
            CommentsOpenHelper.TABLE_COMMENTS,
            allColumns,
            CommentsOpenHelper.COLUMN_ID +  " = " + insertId,
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
        Cursor cursor = db.query(CommentsOpenHelper.TABLE_COMMENTS, allColumns, null, null, null, null, null);
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
            CommentsOpenHelper.TABLE_COMMENTS,
            CommentsOpenHelper.COLUMN_ID + " = ? ",
            new String[] {String.valueOf(id)}
        );
    }
}
