package nl.wildlands.wildlandseducation.SQLite;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import nl.wildlands.wildlandseducation.Level;

/**
 * Class die SQLite voor level verzorgt
 */
public class LevelDataSource {

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] levelColumns = { MySQLiteHelper.COLUMN_LEVEL_ID, MySQLiteHelper.COLUMN_LEVEL_LEVELID, MySQLiteHelper.COLUMN_LEVEL_LEVELNAAM};


    public LevelDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * Voeg nieuw niveau toe
     * @param levelId
     * @param name
     * @return
     */
    public Level createLevel(int levelId, String name)
    {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_LEVEL_LEVELID, levelId);
        values.put(MySQLiteHelper.COLUMN_LEVEL_LEVELNAAM, name);
        long insertId = database.insert(MySQLiteHelper.TABLE_LEVELS, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_LEVELS,
                levelColumns, MySQLiteHelper.COLUMN_LEVEL_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Level newLevel = cursorToLevel(cursor);
        cursor.close();
        return newLevel;
    }

    /**
     *
     * @return alle niveaus
     */
    public ArrayList<Level> getAllLevels()
    {
        ArrayList<Level> levels = new ArrayList<Level>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_LEVELS, levelColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Level level = cursorToLevel(cursor);
            levels.add(level);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return levels;
    }

    /**
     * Cursor voor level
     * @param cursor
     * @return level
     */
    private Level cursorToLevel(Cursor cursor)
    {
        Level level = new Level(cursor.getLong(0), cursor.getInt(1), cursor.getString(2));
        return level;
    }








}