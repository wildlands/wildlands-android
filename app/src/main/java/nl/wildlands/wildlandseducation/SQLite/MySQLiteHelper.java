package nl.wildlands.wildlandseducation.SQLite;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Class voor SQLite handelingen
 */
public class MySQLiteHelper extends SQLiteOpenHelper{

    // Alle tabellen
    public static final String TABLE_QUESTIONS = "questions";
    public static final String TABLE_ANSWERS = "answers";
    public static final String TABLE_PINPOINTS = "pinpoints";
    public static final String TABLE_PAGES = "pages";
    public static final String TABLE_IMAGES = "images";
    public static final String TABLE_LAYERS = "layers";
    public static final String TABLE_LAYER_IMAGES = "layerimages";
    public static final String TABLE_PAGE_IMAGES = "pageimages";
    public static final String TABLE_LEVELS = "levels";

    // Alle kolommen
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
    public static final String COLUMN_PINPOINT_PINPOINT_ID = "pinpointd";
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

    public static final String COLUMN_IMAGE_ID = "id";
    public static final String COLUMN_IMAGE_PATH = "path";
    public static final String COLUMN_IMAGE_NAME = "name";
    public static final String COLUMN_IMAGE_QUESTION = "questionid";

    public static final String COLUMN_LAYER_ID = "id";
    public static final String COLUMN_THEMA_ID = "themaid";
    public static final String COLUMN_LAYER_IMAGE = "image";

    public static final String COLUMN_LAYER_IMAGE_ID = "id";
    public static final String COLUMN_LAYER_THEMA_ID = "themaid";
    public static final String COLUMN_LAYER_IMAGE_PATH = "path";
    public static final String COLUMN_LAYER_IMAGE_NAME = "name";

    public static final String COLUMN_PAGE_IMAGE_ID = "id";
    public static final String COLUMN_PAGE_IMAGE_PAGE_ID = "pageid";
    public static final String COLUMN_PAGE_IMAGE_PATH = "path";
    public static final String COLUMN_PAGE_IMAGE_NAME = "name";

    public static final String COLUMN_LEVEL_ID = "id";
    public static final String COLUMN_LEVEL_LEVELID = "levelid";
    public static final String COLUMN_LEVEL_LEVELNAAM = "levelname";


    private static final String DATABASE_NAME = "questions.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statements
    private static final String DATABASE_CREATE_LAYER_IMAGES = "create table "
            + TABLE_LAYER_IMAGES + "(" + COLUMN_LAYER_IMAGE_ID + " integer primary key autoincrement, "
            + COLUMN_LAYER_THEMA_ID + " integer, " + COLUMN_LAYER_IMAGE_PATH + " text, " + COLUMN_LAYER_IMAGE_NAME +
            " text);";

    private static final String DATABASE_CREATE_LEVELS = "create table "
            + TABLE_LEVELS + "(" + COLUMN_LEVEL_ID + " integer primary key autoincrement, "
            + COLUMN_LEVEL_LEVELID + " integer, " + COLUMN_LEVEL_LEVELNAAM + " text);";

    private static final String DATABASE_CREATE_PAGE_IMAGES = "create table "
            + TABLE_PAGE_IMAGES + "(" + COLUMN_PAGE_IMAGE_ID + " integer primary key autoincrement, "
            + COLUMN_PAGE_IMAGE_PAGE_ID + " integer, " + COLUMN_PAGE_IMAGE_PATH + " text, " + COLUMN_PAGE_IMAGE_NAME +
            " text);";

    private static final String DATABASE_CREATE_LAYER = "create table "
            + TABLE_LAYERS + "(" + COLUMN_LAYER_ID
            + " integer primary key autoincrement, " + COLUMN_THEMA_ID + " integer, " + COLUMN_LAYER_IMAGE + " text);";

    private static final String DATABASE_CREATE = "create table "
            + TABLE_QUESTIONS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_TEXT
            + " text not null, " + COLUMN_IMAGE + " text, " + COLUMN_LEVEL + " integer, " + COLUMN_TYPE + " text);";

    private static final String DATABASE_CREATE_ANSWER = "create table "
            + TABLE_ANSWERS + "(" + COLUMN_ANSWER_ID
            + " integer primary key autoincrement, " + COLUMN_QUESTION_ID + " integer, " + COLUMN_ANSWER_TEXT
            + " text not null, " + COLUMN_GOOD + " integer );";
    private static final String DATABASE_CREATE_PINPOINT = "create table "
            + TABLE_PINPOINTS + "(" + COLUMN_PINPOINT_ID + " integer primary key autoincrement, " + COLUMN_PINPOINT_PINPOINT_ID +
            " integer, " + COLUMN_PINPOINT_NAME
            + " text not null, " + COLUMN_PINPOINT_DESCRIPTION + " text, " + COLUMN_PINPOINT_TYPE + " text, " + COLUMN_PINPOINT_XPOS
            + " integer, " + COLUMN_PINPOINT_YPOS + " integer);";

    private static final String DATABASE_CREATE_PAGES = "create table "
            + TABLE_PAGES + "(" + COLUMN_PAGE_ID
            + " integer primary key autoincrement, " + COLUMN_PINPOINT + " integer, " + COLUMN_PAGE_LEVEL + " integer, "
            + COLUMN_PAGE_TITLE + " text, " + COLUMN_PAGE_IMAGE + " text, " + COLUMN_PAGE_TEXT + " text);";

    private static final String DATABASE_CREATE_IMAGES = "create table "
            + TABLE_IMAGES + "(" + COLUMN_IMAGE_ID
            + " integer primary key autoincrement, " + COLUMN_IMAGE_PATH + " text, " + COLUMN_IMAGE_NAME + " text, " + COLUMN_IMAGE_QUESTION + " integer);";


    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Maak alle tabellen aan
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
        db.execSQL(DATABASE_CREATE_ANSWER);
        db.execSQL(DATABASE_CREATE_PINPOINT);
        db.execSQL(DATABASE_CREATE_PAGES);
        db.execSQL(DATABASE_CREATE_IMAGES);
        db.execSQL(DATABASE_CREATE_LAYER);
        db.execSQL(DATABASE_CREATE_LAYER_IMAGES);
        db.execSQL(DATABASE_CREATE_PAGE_IMAGES);
        db.execSQL(DATABASE_CREATE_LEVELS);
    }

    /**
     * Drop alle oude tabellen en creeer opnieuw
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ANSWERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PINPOINTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LAYERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LAYER_IMAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAGE_IMAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LEVELS);
        onCreate(db);
    }
}
