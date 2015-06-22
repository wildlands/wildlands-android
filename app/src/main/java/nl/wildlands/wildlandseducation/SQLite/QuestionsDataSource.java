package nl.wildlands.wildlandseducation.SQLite;


import java.util.ArrayList;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import nl.wildlands.wildlandseducation.quiz.Answer;
import nl.wildlands.wildlandseducation.quiz.Question;
import nl.wildlands.wildlandseducation.quiz.QuestionImage;

/**
 * Class om de SQLite handeling voor de vragen te verzorgen
 */
public class QuestionsDataSource {

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_TEXT, MySQLiteHelper.COLUMN_IMAGE, MySQLiteHelper.COLUMN_LEVEL, MySQLiteHelper.COLUMN_TYPE };
    private String[] answerColums = { MySQLiteHelper.COLUMN_ANSWER_ID, MySQLiteHelper.COLUMN_QUESTION_ID, MySQLiteHelper.COLUMN_ANSWER_TEXT, MySQLiteHelper.COLUMN_GOOD};
    private String[] imageColumns = { MySQLiteHelper.COLUMN_IMAGE_ID, MySQLiteHelper.COLUMN_IMAGE_PATH, MySQLiteHelper.COLUMN_IMAGE_NAME, MySQLiteHelper.COLUMN_IMAGE_QUESTION};

    public QuestionsDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * Maak een nieuwe vraag aan
     * @param question
     * @param image
     * @param level
     * @param type
     * @return
     */
    public Question createQuestion(String question, String image, int level, String type)
    {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_TEXT, question);
        values.put(MySQLiteHelper.COLUMN_IMAGE, image);
        values.put(MySQLiteHelper.COLUMN_LEVEL, level);
        values.put(MySQLiteHelper.COLUMN_TYPE, type);
        long insertId = database.insert(MySQLiteHelper.TABLE_QUESTIONS, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_QUESTIONS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Question newQuestion = cursorToQuestion(cursor);
        cursor.close();
        Log.d("Question naar opslag", newQuestion.getQuestion());
        return newQuestion;
    }

    /**
     * Maak een nieuwe vraagafbeelding aan
     * @param path
     * @param name
     * @param id
     * @return
     */
    public QuestionImage createImage(String path, String name, long id)
    {
        Log.d("name in opslag", name);
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_IMAGE_PATH, path);
        values.put(MySQLiteHelper.COLUMN_IMAGE_NAME, name);
        values.put(MySQLiteHelper.COLUMN_IMAGE_QUESTION, id);
        long insertId = database.insert(MySQLiteHelper.TABLE_IMAGES, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_IMAGES,
                imageColumns, MySQLiteHelper.COLUMN_IMAGE_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        QuestionImage newQuestionImage = cursorToQuestionImage(cursor);
        cursor.close();
        return newQuestionImage;
    }

    /**
     * Maak een nieuw antwoord aan
     * @param answer
     * @return
     */
    public Answer createAnswer(Answer answer)
    {
        long id = answer.getVraagId();
        int good = 0;
        if (answer.isGood())
        {
            good = 1;
        }
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_QUESTION_ID, id);
        values.put(MySQLiteHelper.COLUMN_ANSWER_TEXT, answer.getAnswer());
        values.put(MySQLiteHelper.COLUMN_GOOD, good);
        long insertId = database.insert(MySQLiteHelper.TABLE_ANSWERS, null,
                values);
        Cursor cursor2 = database.query(MySQLiteHelper.TABLE_ANSWERS,
                answerColums, MySQLiteHelper.COLUMN_ANSWER_ID + " = " + insertId, null,
                null, null, null);
        cursor2.moveToFirst();
        Answer newAnswer = cursorToAnswer(cursor2);
        cursor2.close();
        return newAnswer;
    }


    /**
     *
     * @return alle vragen
     */
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

    /**
     *
     * @return alle afbeeldingen
     */
    public ArrayList<QuestionImage> getAllImages()
    {
        ArrayList<QuestionImage> images = new ArrayList<>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_IMAGES, imageColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            QuestionImage questionImage = cursorToQuestionImage(cursor);
            images.add(questionImage);
            cursor.moveToNext();
        }
        cursor.close();
        return images;
    }

    /**
     *
     * @return alle antwoorden
     */
    public ArrayList<Answer> getAllAnswers() {
        ArrayList<Answer> answers = new ArrayList<Answer>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_ANSWERS,
                answerColums, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Answer answer = cursorToAnswer(cursor);
            answers.add(answer);
            cursor.moveToNext();
        }

        cursor.close();
        return answers;
    }

    /**
     * Cursor voor nieuwe vraag
     * @param cursor
     * @return vraag
     */
    private Question cursorToQuestion(Cursor cursor) {

        Question question = new Question(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getString(4));
        return question;
    }

    /**
     * Cursor voor vraagafbeelding
     * @param cursor
     * @return
     */
    private QuestionImage cursorToQuestionImage(Cursor cursor)
    {
        QuestionImage questionImage = new QuestionImage(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getLong(3));
        return questionImage;
    }

    /**
     * Cursor voor antwoord
     * @param cursor
     * @return
     */
    private Answer cursorToAnswer(Cursor cursor)
    {
        int good = cursor.getInt(3);
        boolean correct = false;
        if(good == 1)
        {
            correct = true;
        }
        Answer answer = new Answer(cursor.getLong(0),cursor.getLong(1), cursor.getString(2), correct);
        return answer;
    }
}

