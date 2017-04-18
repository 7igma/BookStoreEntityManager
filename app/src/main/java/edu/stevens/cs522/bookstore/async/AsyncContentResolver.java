package edu.stevens.cs522.bookstore.async;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;

import edu.stevens.cs522.bookstore.entities.Book;

/**
 * Created by dduggan.
 */

public class AsyncContentResolver extends AsyncQueryHandler {

    public AsyncContentResolver(ContentResolver cr) {
        super(cr);
    }

    public void insertAsync(Uri uri,
                            ContentValues values,
                            IContinue<Uri> callback) {
        this.startInsert(0, callback, uri, values);
    }

    @Override
    public void onInsertComplete(int token, Object cookie, Uri uri) {
        if (cookie != null) {
            @SuppressWarnings("unchecked")
            IContinue<Uri> callback = (IContinue<Uri>) cookie;
            callback.kontinue(uri);
        }
    }

    public void queryAsync(Uri uri, String[] columns, String select, String[] selectArgs, String order, SimpleQueryBuilder.ISimpleQueryListener<Book> listener) {
        this.startQuery(0, listener, uri, columns, select, selectArgs, order);
    }

    public void queryAsync(Uri uri, String[] columns, String select, String[] selectArgs, String order, IContinue<Cursor> callback) {
        this.startQuery(0, callback, uri, columns, select, selectArgs, order);
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        super.onQueryComplete(token, cookie, cursor);
        // TODO check
        if (cookie != null) {

            if (cookie instanceof SimpleQueryBuilder)
            {
                SimpleQueryBuilder<Cursor> callback = (SimpleQueryBuilder<Cursor>) cookie;
                callback.kontinue(cursor);
            }
            else if (cookie instanceof SimpleQueryBuilder.ISimpleQueryListener)
            {
                SimpleQueryBuilder.ISimpleQueryListener<Cursor> callback = (SimpleQueryBuilder.ISimpleQueryListener) cookie;
                ArrayList<Cursor> results = new ArrayList<>();
                results.add(cursor);
                callback.handleResults(results);
            }
        }
    }

    public void deleteAsync(Uri uri, String select, String[] selectArgs) {
        this.startDelete(0, null, uri, select, selectArgs);
    }

    @Override
    protected void onDeleteComplete(int token, Object cookie, int result) {
        super.onDeleteComplete(token, cookie, result);
    }

    public void updateAsync(Uri uri, ContentValues values, String select, String[] selectArgs) {
        this.startUpdate(0,null, uri, values, select, selectArgs);
    }

    @Override
    protected void onUpdateComplete(int token, Object cookie, int result) {
        super.onUpdateComplete(token, cookie, result);
    }

}
