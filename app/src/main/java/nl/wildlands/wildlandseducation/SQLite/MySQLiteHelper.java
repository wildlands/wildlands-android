package nl.wildlands.wildlandseducation.SQLite;

/**
 * Created by stefan on 3/25/2015.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper{
    public static final String TABLE_QUESTIONS = "questions";
    public static final String TABLE_ANSWERS = "answers";
    public static final String TABLE_PINPOINTS = "pinpoints";
    public static final String TABLE_PAGES = "pages";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_LEVEL = "level";
    public static final String COLUMN_TYPE = "type";

    public static final String COLUMN_ANSWER_ID = "id";
    public static final String COLUMN_QUESTION_ID = "questionid";
    public static final String COLUMN_ANSWER_TEXT = "text";
    public static final String COLUMN_GOOD = "good";

    public static final String COLUMN_PINPOINT_ID = "id";
    public static final String COLUMN_PINPOINT_NAME = "name";
    public static final String COLUMN_PINPOINT_DESCRIPTION = "description";
    public static final String COLUMN_PINPOINT_TYPE = "type";
    public static final String COLUMN_PINPOINT_XPOS = "xpos";
    public static final String COLUMN_PINPOINT_YPOS = "ypos";

    public static final String COLUMN_PAGE_ID = "id";
    public static final String COLUMN_PINPOINT = "pinpointId";
    public static final String COLUMN_PAGE_LEVEL = "level";
    public static final String COLUMN_PAGE_TITLE = "title";
    public static final String COLUMN_PAGE_IMAGE = "image";
    public static final String COLUMN_PAGE_TEXT = "text";



    private static final String DATABASE_NAME = "questions.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_QUESTIONS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_TEXT
            + " text not null, " + COLUMN_IMAGE + " text, " + COLUMN_LEVEL + " integer, " + COLUMN_TYPE + " text);";

    private static final String DATABASE_CREATE_ANSWER = "create table "
            + TABLE_ANSWERS + "(" + COLUMN_ANSWER_ID
            + " integer primary key autoincrement, " + COLUMN_QUESTION_ID + " integer, " + COLUMN_ANSWER_TEXT
            + " text not null, " + COLUMN_GOOD + " integer );";
    private static final String DATABASE_CREATE_PINPOINT = "create table "
            + TABLE_PINPOINTS + "(" + COLUMN_PINPOINT_ID + " integer primary key autoincrement, " + COLUMN_PINPOINT_NAME
            + " text not null, " + COLUMN_PINPOINT_DESCRIPTION + " text, " + COLUMN_PINPOINT_TYPE + " text, " + COLUMN_PINPOINT_XPOS
            + " integer, " + COLUMN_PINPOINT_YPOS + " integer);";

    private static final String DATABASE_CREATE_PAGES = "create table "
            + TABLE_PAGES + "(" + COLUMN_PAGE_ID
            + " integer primary key autoincrement, " + COLUMN_PINPOINT + " integer, " + COLUMN_PAGE_LEVEL + " integer, "
            + COLUMN_PAGE_TITLE + " text, " + COLUMN_PAGE_IMAGE + " text, " + COLUMN_PAGE_TEXT + " text);";


    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
        db.execSQL(DATABASE_CREATE_ANSWER);
        db.execSQL(DATABASE_CREATE_PINPOINT);
        db.execSQL(DATABASE_CREATE_PAGES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ANSWERS);
        onCreate(db);
    }
}
