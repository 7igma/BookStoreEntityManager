package edu.stevens.cs522.bookstore.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.stevens.cs522.bookstore.R;
import edu.stevens.cs522.bookstore.async.QueryBuilder.IQueryListener;
import edu.stevens.cs522.bookstore.async.SimpleQueryBuilder;
import edu.stevens.cs522.bookstore.contracts.BookContract;
import edu.stevens.cs522.bookstore.entities.Book;
import edu.stevens.cs522.bookstore.managers.BookManager;
import edu.stevens.cs522.bookstore.managers.TypedCursor;
import edu.stevens.cs522.bookstore.util.BookAdapter;

public class MainActivity extends Activity implements OnItemClickListener, AdapterView.OnItemLongClickListener, AbsListView.MultiChoiceModeListener, SimpleQueryBuilder.ISimpleQueryListener, IQueryListener {
	
	// Use this when logging errors and warnings.
	@SuppressWarnings("unused")
	private static final String TAG = MainActivity.class.getCanonicalName();
	
	// These are request codes for subactivity request calls
	static final private int ADD_REQUEST = 1;
	
	@SuppressWarnings("unused")
	static final private int CHECKOUT_REQUEST = ADD_REQUEST + 1;

    private BookManager bookManager;

    private BookAdapter bookAdapter;
    ListView lv;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set the layout (use cart.xml layout)
        setContentView(R.layout.cart);

        // Use a custom cursor adapter to display an empty (null) cursor.
        bookAdapter = new BookAdapter(this, null);
        lv = (ListView) findViewById(android.R.id.list);
        lv.setAdapter(bookAdapter);
        lv.setSelection(0);

        // Set listeners for item selection and multi-choice CAB
        lv.setOnItemClickListener(this);
        lv.setMultiChoiceModeListener(this);
        lv.setOnItemLongClickListener(this);

        // Initialize the book manager and query for all books
        bookManager = new BookManager(this);
        bookManager.getAllBooksAsync(this);

        //bookManager.getBookAsync(0, this);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// inflate a menu with ADD and CHECKOUT options
        getMenuInflater().inflate(R.menu.bookstore_menu, menu);

        return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
        switch(item.getItemId()) {

            // ADD provide the UI for adding a book
            case R.id.add:
                Intent addIntent = new Intent(this, AddBookActivity.class);
                startActivityForResult(addIntent, ADD_REQUEST);
                break;

            // CHECKOUT provide the UI for checking out
            case R.id.checkout:
                Intent checkoutIntent = new Intent(this, CheckoutActivity.class);
                startActivityForResult(checkoutIntent, CHECKOUT_REQUEST);
                break;

            default:
        }
        return false;
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        // TODO Handle results from the Search and Checkout activities.

        // Use ADD_REQUEST and CHECKOUT_REQUEST codes to distinguish the cases.
        switch(requestCode) {
            case ADD_REQUEST:
                // ADD: add the book that is returned to the shopping cart.
                if (resultCode == RESULT_OK){
                    Bundle data = intent.getExtras();
                    Book book = (Book) data.getParcelable("book");
                    //TODO add async
                    bookManager.persistAsync(book);
                }
                break;
            case CHECKOUT_REQUEST:
                // CHECKOUT: empty the shopping cart.
                if (resultCode == RESULT_OK)
                {
                    //TODO delete books async
                    HashSet<Long> ids = new HashSet<Long>();
                    for (int i = 0; i < lv.getAdapter().getCount(); i++)
                    {
                        Log.d("deletion", "ids: "+lv.getItemIdAtPosition(i));
                        ids.add(lv.getItemIdAtPosition(i));
                        bookManager.deleteBooksAsync(ids);
                    }
                }
                break;
        }
    }
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		
	}

    /*
     * TODO Query listener callbacks
     */

    @Override
    public void handleResults(TypedCursor results) {
        // TODO update the adapter
        if (results != null)
        {
            this.bookAdapter.swapCursor(results.getCursor());
        }
        else
        {
            this.bookAdapter.swapCursor(null);
        }
    }

    @Override
    public void closeResults() {
        // TODO update the adapter
        this.bookAdapter.swapCursor(null);
    }

    @Override
    public void handleResults(List results)
    {
        if (results != null)
        {
            Intent intent = new Intent(MainActivity.this, ViewBookActivity.class);
            Cursor c = (Cursor) results.get(0);
            Book book = new Book(c);
            c.close();
            intent.putExtra("book", book);
            startActivity(intent);
        }
    }

    /*
     * Selection of a book from the list view
     */

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO query via async
        // ok to do on main thread for BookStoreWithContentProvider
        //Intent intent = new Intent(MainActivity.this, ViewBookActivity.class);
        //intent.putExtra("book", dba.fetchBook(l));
        //String[] args = new String[] {Long.toString(id)};
        bookManager.getBookAsync(id, this);
        //Cursor cursor = getContentResolver().query(BookContract.CONTENT_URI(id), null, null, args, null);
        //Book book = new Book(cursor);
        //cursor.close();
        //intent.putExtra("book", book);
        //startActivity(intent);
    }


    /*
  * Long click listener
  */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int i, long l) {
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        lv.setItemChecked(i, true);
        return true;
    }

    /*
     * Handle multi-choice action mode for deletion of several books at once
     */

    Set<Long> selected;

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        // inflate the menu for the CAB
        mode.getMenuInflater().inflate(R.menu.books_cab, menu);
        selected = new HashSet<Long>();
        return true;
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        if (checked) {
            selected.add(id);
        } else {
            selected.remove(id);
        }
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch(item.getItemId()) {
            case R.id.delete:
                // TODO delete the selected books async
                long[] itemIds = lv.getCheckedItemIds();
                HashSet<Long> ids = new HashSet<Long>();
                for (int i = 0; i < itemIds.length; i++)
                {
                    ids.add(itemIds[i]);
                }
                bookManager.deleteBooksAsync(ids);
                Toast toast=Toast.makeText(getApplicationContext(),"deleting", Toast.LENGTH_LONG);
                toast.show();
                selected.clear();
                bookAdapter.notifyDataSetChanged();
                mode.finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
    }

}