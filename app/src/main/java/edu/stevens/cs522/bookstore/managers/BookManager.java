package edu.stevens.cs522.bookstore.managers;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.Set;

import edu.stevens.cs522.bookstore.async.AsyncContentResolver;
import edu.stevens.cs522.bookstore.async.IContinue;
import edu.stevens.cs522.bookstore.async.IEntityCreator;
import edu.stevens.cs522.bookstore.async.QueryBuilder;
import edu.stevens.cs522.bookstore.async.QueryBuilder.IQueryListener;
import edu.stevens.cs522.bookstore.async.SimpleQueryBuilder;
import edu.stevens.cs522.bookstore.contracts.AuthorContract;
import edu.stevens.cs522.bookstore.contracts.BookContract;
import edu.stevens.cs522.bookstore.entities.Book;
import edu.stevens.cs522.bookstore.providers.BookProvider;

/**
 * Created by dduggan.
 */

public class BookManager extends Manager<Book> {

    private static final int LOADER_ID = 1;

    private static final IEntityCreator<Book> creator = new IEntityCreator<Book>() {
        @Override
        public Book create(Cursor cursor) {
            return new Book(cursor);
        }
    };

    private AsyncContentResolver contentResolver;

    private Context myContext;

    public BookManager(Context context) {
        super(context, creator, LOADER_ID);
        contentResolver = new AsyncContentResolver(context.getContentResolver());
        this.myContext = context;
    }

    public void getAllBooksAsync(IQueryListener<Book> listener) {
        // TODO use QueryBuilder to complete this
        QueryBuilder.executeQuery("main", (Activity) myContext, BookContract.CONTENT_URI, LOADER_ID, creator, listener);
    }

    public void getBookAsync(Long id, SimpleQueryBuilder.ISimpleQueryListener<Book> listener) {
        // TODO
        //Long[] ids = new Long[toBeDeleted.size()];
        //toBeDeleted.toArray(ids);
        String[] args = new String[] {Long.toString(id)};

        StringBuilder sb = new StringBuilder();
        sb.append(BookContract.ID);
        sb.append("=?");
        String select = sb.toString();

        contentResolver.queryAsync(BookContract.CONTENT_URI, null, select, args, null, listener);

    }

    public void persistAsync(final Book book) {
        IContinue<Uri> callback = new IContinue<Uri>() {
            public void kontinue(Uri uri) {
                book.id = (int) BookContract.getId(uri);
            }
        };
        ContentValues values = new ContentValues();
        book.writeToProvider(values);
        contentResolver.insertAsync(BookContract.CONTENT_URI, values, callback);
    }

    public void deleteBooksAsync(Set<Long> toBeDeleted) {
        Long[] ids = new Long[toBeDeleted.size()];
        toBeDeleted.toArray(ids);
        String[] args = new String[ids.length];

        StringBuilder sb = new StringBuilder();
        if (ids.length > 0) {
            sb.append(AuthorContract.ID);
            sb.append("=?");
            args[0] = ids[0].toString();
            for (int ix=1; ix<ids.length; ix++) {
                sb.append(" or ");
                sb.append(AuthorContract.ID);
                sb.append("=?");
                args[ix] = ids[ix].toString();
            }
        }
        String select = sb.toString();

        contentResolver.deleteAsync(BookContract.CONTENT_URI, select, args);
    }

}
