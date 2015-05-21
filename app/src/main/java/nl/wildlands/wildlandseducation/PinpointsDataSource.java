package nl.wildlands.wildlandseducation;

import java.util.ArrayList;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class PinpointsDataSource {

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] pinpointColumns = { MySQLiteHelper.COLUMN_PINPOINT_ID,
            MySQLiteHelper.COLUMN_PINPOINT_NAME, MySQLiteHelper.COLUMN_PINPOINT_DESCRIPTION, MySQLiteHelper.COLUMN_PINPOINT_TYPE, MySQLiteHelper.COLUMN_PINPOINT_XPOS, MySQLiteHelper.COLUMN_PINPOINT_YPOS };
    private String[] pagesColumns = {MySQLiteHelper.COLUMN_PAGE_ID, MySQLiteHelper.COLUMN_PINPOINT, MySQLiteHelper.COLUMN_PAGE_LEVEL, MySQLiteHelper.COLUMN_PAGE_TITLE, MySQLiteHelper.COLUMN_PAGE_IMAGE, MySQLiteHelper.COLUMN_PAGE_TEXT};

    public PinpointsDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Pinpoint createPinpoint(String name, String description, String type, int xpos, int ypos)
    {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_PINPOINT_NAME, name);
        values.put(MySQLiteHelper.COLUMN_PINPOINT_DESCRIPTION, description);
        values.put(MySQLiteHelper.COLUMN_PINPOINT_TYPE, type);
        values.put(MySQLiteHelper.COLUMN_PINPOINT_XPOS, xpos);
        values.put(MySQLiteHelper.COLUMN_PINPOINT_YPOS, ypos);
        long insertId = database.insert(MySQLiteHelper.TABLE_PINPOINTS, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_PINPOINTS,
                pinpointColumns, MySQLiteHelper.COLUMN_PINPOINT_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Pinpoint newPinpoint = cursorToPinpoint(cursor);
        cursor.close();

        return newPinpoint;
    }

    public Page createPage(long pinpointid, int level, String title, String image, String text)
    {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_PINPOINT, pinpointid);
        values.put(MySQLiteHelper.COLUMN_PAGE_LEVEL, level);
        values.put(MySQLiteHelper.COLUMN_PAGE_TITLE, title);
        values.put(MySQLiteHelper.COLUMN_PAGE_IMAGE, image);
        values.put(MySQLiteHelper.COLUMN_PAGE_TEXT, text);


        long insertId = database.insert(MySQLiteHelper.TABLE_PAGES, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_PAGES,
                pagesColumns, MySQLiteHelper.COLUMN_PAGE_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Page newPage = cursorToPage(cursor);
        cursor.close();

        Log.d("page", newPage.getText());
        return newPage;
    }

    public ArrayList<Pinpoint> getAllPinpoints() {
        ArrayList<Pinpoint> pinpoints = new ArrayList<Pinpoint>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_PINPOINTS,
                pinpointColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Pinpoint pinpoint = cursorToPinpoint(cursor);
            pinpoints.add(pinpoint);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return pinpoints;
    }

    public ArrayList<Page> getAllPages() {
        ArrayList<Page> pages = new ArrayList<Page>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_PAGES,
                pagesColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Page page = cursorToPage(cursor);
            pages.add(page);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return pages;
    }



       private Pinpoint cursorToPinpoint(Cursor cursor)
    {
        Pinpoint pinpoint = new Pinpoint(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4), cursor.getInt(5));
        return pinpoint;
    }

    private Page cursorToPage(Cursor cursor)
    {
        Page page = new Page(cursor.getLong(0), cursor.getLong(1), cursor.getInt(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
        return page;
    }

}

