package edu.stevens.cs522.bookstore.managers;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import edu.stevens.cs522.bookstore.async.AsyncContentResolver;
import edu.stevens.cs522.bookstore.async.IEntityCreator;
import edu.stevens.cs522.bookstore.async.QueryBuilder;
import edu.stevens.cs522.bookstore.async.SimpleQueryBuilder;

/**
 * Created by dduggan.
 */

public abstract class Manager<T> {

    private final Context context;

    private final IEntityCreator<T> creator;

    private final int loaderID;

    private final String tag;

    protected Manager(Context context,
                      IEntityCreator<T> creator,
                      int loaderID) {
        this.context = context;
        this.creator = creator;
        this.loaderID = loaderID;
        this.tag = this.getClass().getCanonicalName();
    }

    private ContentResolver syncResolver;

    private AsyncContentResolver asyncResolver;

    protected ContentResolver getSyncResolver() {
        if (syncResolver == null)
            syncResolver = context.getContentResolver();
        return syncResolver;
    }

    protected AsyncContentResolver getAsyncResolver() {
        if (asyncResolver == null)
            asyncResolver = new AsyncContentResolver(context.getContentResolver());
        return asyncResolver;
    }

    // TODO Provide operations for executing queries (see lectures)
    protected void executeSimpleQuery(Uri uri, SimpleQueryBuilder.ISimpleQueryListener<T> listener)
    {
        SimpleQueryBuilder.executeQuery((Activity) context, uri, creator, listener);
    }

    protected void executeSimpleQuery(Uri uri, String[] projection, String select, String[] selectArgs, SimpleQueryBuilder.ISimpleQueryListener<T> listener)
    {
        SimpleQueryBuilder.executeQuery((Activity) context, uri, projection, select, selectArgs, creator, listener);
    }

    protected void executeQuery(Uri uri, QueryBuilder.IQueryListener<T> listener)
    {
        QueryBuilder.executeQuery(tag, (Activity) context, uri, loaderID, creator, listener);
    }

    protected void executeQuery(Uri uri, String[] projection, String select, String[] selectArgs, QueryBuilder.IQueryListener<T> listener)
    {
        QueryBuilder.executeQuery(tag, (Activity) context, uri, loaderID, projection, select, selectArgs, creator, listener);
    }


}
