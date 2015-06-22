package nl.wildlands.wildlandseducation.SQLite;

import java.util.ArrayList;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import nl.wildlands.wildlandseducation.Pinpoint.Page;
import nl.wildlands.wildlandseducation.Pinpoint.PageImage;
import nl.wildlands.wildlandseducation.Pinpoint.Pinpoint;

/**
 * Class die SQLite handeling voor pinpoints verzorgt
 */
public class PinpointsDataSource {

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] pinpointColumns = { MySQLiteHelper.COLUMN_PINPOINT_ID, MySQLiteHelper.COLUMN_PINPOINT_PINPOINT_ID,
            MySQLiteHelper.COLUMN_PINPOINT_NAME, MySQLiteHelper.COLUMN_PINPOINT_DESCRIPTION, MySQLiteHelper.COLUMN_PINPOINT_TYPE, MySQLiteHelper.COLUMN_PINPOINT_XPOS, MySQLiteHelper.COLUMN_PINPOINT_YPOS };
    private String[] pagesColumns = {MySQLiteHelper.COLUMN_PAGE_ID, MySQLiteHelper.COLUMN_PINPOINT, MySQLiteHelper.COLUMN_PAGE_LEVEL, MySQLiteHelper.COLUMN_PAGE_TITLE, MySQLiteHelper.COLUMN_PAGE_IMAGE, MySQLiteHelper.COLUMN_PAGE_TEXT};
    private String[] pageImageColumns = {MySQLiteHelper.COLUMN_PAGE_IMAGE_ID, MySQLiteHelper.COLUMN_PAGE_IMAGE_PAGE_ID, MySQLiteHelper.COLUMN_PAGE_IMAGE_PATH, MySQLiteHelper.COLUMN_PAGE_IMAGE_NAME};

    public PinpointsDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * Voeg een pinpoint toe
     * @param id
     * @param name
     * @param description
     * @param type
     * @param xpos
     * @param ypos
     * @return pinpoint
     */
    public Pinpoint createPinpoint(int id, String name, String description, String type, int xpos, int ypos)
    {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_PINPOINT_PINPOINT_ID, id);
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

    /**
     * Voeg nieuwe pageimage toe
     * @param pageId
     * @param path
     * @param name
     * @return pageimage
     */
    public PageImage createPageImage(long pageId, String path, String name)
    {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_PAGE_IMAGE_PAGE_ID, pageId);
        values.put(MySQLiteHelper.COLUMN_PAGE_IMAGE_PATH, path);
        values.put(MySQLiteHelper.COLUMN_PAGE_IMAGE_NAME, name);
        long insertId = database.insert(MySQLiteHelper.TABLE_PAGE_IMAGES, null, values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_PAGE_IMAGES,pageImageColumns,MySQLiteHelper.COLUMN_PAGE_IMAGE_ID + " = " + insertId,null,null,null,null);
        cursor.moveToFirst();
        PageImage newPageImage = cursorToPageImage(cursor);
        cursor.close();
        return newPageImage;
    }

    /**
     * Voeg nieuwe pagina toe
     * @param pinpointid
     * @param level
     * @param title
     * @param image
     * @param text
     * @return page
     */
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

    /**
     *
     * @return alle pinpoints
     */
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

    /**
     *
     * @return alle pages
     */
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

    /**
     *
     * @return alle pageimages
     */
    public ArrayList<PageImage> getAllPageImages() {
        ArrayList<PageImage> pageImages = new ArrayList<PageImage>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_PAGE_IMAGES,
                pageImageColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            PageImage pageImage = cursorToPageImage(cursor);
            pageImages.add(pageImage);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return pageImages;
    }


    /**
     * Cursor voor pinpoint
     * @param cursor
     * @return pinpoint
     */
       private Pinpoint cursorToPinpoint(Cursor cursor)
    {
        Pinpoint pinpoint = new Pinpoint(cursor.getLong(0),cursor.getInt(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getInt(5), cursor.getInt(6));
        return pinpoint;
    }

    /**
     * Cursor voor page
     * @param cursor
     * @return page
     */
    private Page cursorToPage(Cursor cursor)
    {
        Page page = new Page(cursor.getLong(0), cursor.getLong(1), cursor.getInt(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
        return page;
    }

    /**
     * Cursor voor pageimage
     * @param cursor
     * @return pageimage
     */
    private PageImage cursorToPageImage(Cursor cursor)
    {
        PageImage pageImage = new PageImage(cursor.getLong(0), cursor.getLong(1), cursor.getString(2), cursor.getString(3));
        return pageImage;
    }
}

