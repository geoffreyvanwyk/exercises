package de.vogella.android.sqlite.first;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import java.util.List;
import java.util.Random;

public class TestDatabaseActivity extends ListActivity {

    private CommentsDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        dataSource = new CommentsDataSource(this);
        dataSource.open();
        List<Comment> values = dataSource.getAllComments();

        ArrayAdapter<Comment> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_list_item_1,
            values
        );

        setListAdapter(adapter);
    }

    @Override
    protected void onResume() {
        dataSource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        dataSource.close();
        super.onPause();
    }

    @SuppressWarnings("unchecked")
    public void onClick(View view) {
        ArrayAdapter<Comment> adapter = (ArrayAdapter<Comment>) getListAdapter();
        Comment comment;

        switch (view.getId()) {
            case R.id.add:
                String[] comments = new String[] {"Cool", "Very nice", "Hate it", "Hooray!"};
                int nextInt = new Random().nextInt(comments.length);
                comment = dataSource.createComment(comments[nextInt]);
                adapter.add(comment);
                break;
            case R.id.delete:
                if (getListAdapter().getCount() > 0) {
                    comment = (Comment) getListAdapter().getItem(0);
                    dataSource.deleteComment(comment);
                    adapter.remove(comment);
                }

                break;
        }

        adapter.notifyDataSetChanged();
    }
}
