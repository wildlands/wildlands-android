package nl.wildlands.wildlandseducation;

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

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_IMAGE = "image";

    public static final String COLUMN_ANSWER_ID = "id";
    public static final String COLUMN_QUESTION_ID = "questionid";
    public static final String COLUMN_ANSWER_TEXT = "text";
    public static final String COLUMN_GOOD = "good";

    private static final String DATABASE_NAME = "questions.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_QUESTIONS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_TEXT
            + " text not null, " + COLUMN_IMAGE + " text );";

    private static final String DATABASE_CREATE_ANSWER = "create table "
            + TABLE_ANSWERS + "(" + COLUMN_ANSWER_ID
            + " integer primary key autoincrement, " + COLUMN_QUESTION_ID + " integer, " + COLUMN_ANSWER_TEXT
            + " text not null, " + COLUMN_GOOD + " integer );";


    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE); db.execSQL(DATABASE_CREATE_ANSWER);
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
