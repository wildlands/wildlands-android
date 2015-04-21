package nl.wildlands.wildlandseducation;

/**
 * Created by stefan on 3/25/2015.
 */
import java.util.ArrayList;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
public class QuestionsDataSource {

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_TEXT };

    public QuestionsDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Question createQuestion(String question)
    {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_TEXT, question);
        long insertId = database.insert(MySQLiteHelper.TABLE_QUESTIONS, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_QUESTIONS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Question newQuestion = cursorToQuestion(cursor);
        cursor.close();
        return newQuestion;
    }

    /*
    public void deleteComment(Question question) {
        //long id = question.getId();
        System.out.println("Comment deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_QUESTIONS, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }*/

    public ArrayList<Question> getAllQuestions() {
        ArrayList<Question> questions = new ArrayList<Question>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_QUESTIONS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Question question = cursorToQuestion(cursor);
            questions.add(question);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return questions;
    }

    private Question cursorToQuestion(Cursor cursor) {
        Question question = new Question(cursor.getString(0),null);


        return question;
    }
}

