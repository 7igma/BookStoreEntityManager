package edu.stevens.cs522.bookstore.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

/**
 * Created by dduggan.
 */

public class AuthorContract implements BaseColumns {

    public static final String ID = _ID;

    public static final String NAME = "name";

    public static final String FIRST_NAME = "first";

    public static final String MIDDLE_INITIAL = "initial";

    public static final String LAST_NAME = "last";

    /*
     * NAME column
     */

    private static int nameColumn = -1;

    private static int firstNameColumn = -1;

    public static String getFirstName(Cursor cursor) {
        if (firstNameColumn < 0) {
            firstNameColumn =  cursor.getColumnIndexOrThrow(FIRST_NAME);
        }
        return cursor.getString(firstNameColumn);
    }

    public static void putFirstName(ContentValues values, String firstName) {
        values.put(FIRST_NAME, firstName);
    }

    // TODO complete the definitions of the operations for Parcelable, cursors and contentvalues
    private static int lastNameColumn = -1;

    public static String getLastName(Cursor cursor) {
        if (lastNameColumn < 0) {
            lastNameColumn =  cursor.getColumnIndexOrThrow(LAST_NAME);;
        }
        return cursor.getString(lastNameColumn);
    }

    public static void putLastName(ContentValues values, String lastName) {
        values.put(LAST_NAME, lastName);
    }


    private static int middleInitialColumn = -1;

    public static String getMiddleInitial(Cursor cursor) {
        if (middleInitialColumn < 0) {
            middleInitialColumn =  cursor.getColumnIndexOrThrow(MIDDLE_INITIAL);
        }
        return cursor.getString(middleInitialColumn);
    }

    public static void putMiddleInitial(ContentValues values, String middleInitial) {
        values.put(MIDDLE_INITIAL, middleInitial);
    }
}