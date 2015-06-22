package nl.wildlands.wildlandseducation.Activities;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import nl.wildlands.wildlandseducation.GlobalSettings.DefaultApplication;
import nl.wildlands.wildlandseducation.JSONParser;
import nl.wildlands.wildlandseducation.R;
import nl.wildlands.wildlandseducation.quiz.Answer;
import nl.wildlands.wildlandseducation.quiz.Question;
import nl.wildlands.wildlandseducation.SQLite.QuestionsDataSource;
import nl.wildlands.wildlandseducation.quiz.QuestionImage;


public class Quiz extends Activity implements OnClickListener {

    private ArrayList<Question> questions, questionAll;                              // ArrayList to store all questions
    private ArrayList<Answer> answers, answerAll;

    private Answer actualAnswer;
    public static final String MyPREFERENCES = "MyPrefs" ;              // String to get sharedprefs
    private Socket socket;


    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Quiz.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    abortQuiz();
                }
            });
        }
    };
    // Url to get JSON
    private static final String GET_QUESTION_URL = "http://wildlands.doornbosagrait.tk/api/api.php?c=GetAllQuestions";

    /*
    Tags om variabelen uit JSON te halen
     */
    private static final String TAG_ID = "id";
    private static final String TAG_PINPOINTID = "pinpointId";
    private static final String TAG_TEXT = "text";
    private static final String TAG_IMAGE = "image";
    private static final String TAG_ANSWERS = "answers";
    private static final String TAG_RIGHTWRONG = "rightWrong";
    private static final String TAG_CHECKSUM = "checksum";

    /*
    Grafische elementen voor layout
     */
    private TextView question;
    private Button answer1, answer2, answer3, answer4, answer5, answer6, answer7, answer8;
    private ImageView img1;

    // String voor Thema, String voor Onvoldoende/voldoende/goed etc.
    private HashMap<String, String> themaAntwoorden;

    //
    private HashMap<Question, Boolean> beantwoordevragen;

    /*
    Datasource voor SQLite
     */
    private QuestionsDataSource datasource;

    /*
    Sharedpreferences voor versie
     */
    SharedPreferences sharedpreferences;

    /*
    Quizvariabelen om goede vragen en vraagnummer bij te houden
     */
    private int questionNumber;
    private int questionsCorrect;

    // JSONParser voor ophalen van data
    JSONParser jsonParser = new JSONParser();

    /*
    JSONObject van de data
     */
    private JSONObject questionObj = null;
    private JSONArray questionArray;
    private JSONArray jsonArray;

    ArrayList<HashMap<String, String>> mQuestionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        socket = ((DefaultApplication)this.getApplication()).getSocket();
        // To use the full width and high of the screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        question = (TextView)findViewById(R.id.textView); // Textview to display the question(s)

        img1 = (ImageView)findViewById(R.id.imageView); // Image above the question

        /*
            Buttons toewijzen
         */
        answer1 = (Button)findViewById(R.id.button);
        answer2 = (Button)findViewById(R.id.button2);
        answer3 = (Button)findViewById(R.id.button3);
        answer4 = (Button)findViewById(R.id.button4);
        answer5 = (Button)findViewById(R.id.button5);
        answer6 = (Button)findViewById(R.id.button6);
        answer7 = (Button)findViewById(R.id.button7);
        answer8 = (Button)findViewById(R.id.button8);



        /*
            Mogelijkheid tot klikken
         */
        answer1.setOnClickListener(this);
        answer2.setOnClickListener(this);
        answer3.setOnClickListener(this);
        answer4.setOnClickListener(this);
        answer5.setOnClickListener(this);
        answer6.setOnClickListener(this);
        answer7.setOnClickListener(this);
        answer8.setOnClickListener(this);

        questionArray = new JSONArray();
        jsonArray = new JSONArray();

       // questions = ((DefaultApplication)this.getApplication()).getQuestions();

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        datasource = new QuestionsDataSource(this);
        datasource.open();

        questions = new ArrayList<Question>();
        questionAll = datasource.getAllQuestions();
        int level = ((DefaultApplication)this.getApplication()).getQuizLevel();

        // Als ze van het goede niveau zijn, voeg ze aan lijst toe
        for(Question question1: questionAll)
        {
            if(question1.getLevel() == level)
            {
                questions.add(question1);
            }
        }

        // Zet de antwoorden in de lijst
        answers = new ArrayList<Answer>();
        answerAll = datasource.getAllAnswers();
        for(Answer answer: answerAll)
        {
            long vraagId = answer.getVraagId();
            for(Question question1: questions)
            {
                if(vraagId == question1.getId())
                {
                    answers.add(answer);
                }
            }
        }

        ArrayList<Question> values = datasource.getAllQuestions();

        themaAntwoorden = new HashMap<String, String>();
        beantwoordevragen = new HashMap<Question, Boolean>();


        display(questionNumber);        // Display de vraag

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(0);

        socket.on("quizAborted", onNewMessage);
    }

    /**
     * Abort de quiz
     */

    public void abortQuiz()
    {
        Intent wait = new Intent(this, WaitForQuizStart.class);
        startActivity(wait);
        this.finish();
    }

    /**
     * Display de vraag adhv vraagnummer
     * @param i
     */
    public void display(int i){
        int searchId = i + 1;

        if(questions.size() <= i){
            // Tel alle scores op
            int energieCorrect = 0, energieTotaal = 0;
            int waterCorrect = 0, waterTotaal = 0;
            int materialenCorrect = 0, materialenTotaal = 0;
            int bioCorrect = 0, bioTotaal = 0;
            int dierenCorrect = 0, dierenTotaal = 0;
            calcResults(questionsCorrect, questionNumber, "Totaal");
            Set<Question> questions = beantwoordevragen.keySet();
            for(Question q: questions)
            {
                boolean correct = beantwoordevragen.get(q);
                String type = q.getType();
                // Kijk welk type het is en tel dan op
                if(type.equals("Water"))
                {
                    waterTotaal += 1;
                    if(correct)
                    {
                        waterCorrect+=1;
                    }
                }
                else if(type.equals("Materiaal"))
                {
                    materialenTotaal += 1;
                    if(correct)
                    {
                        materialenCorrect += 1;
                    }
                }
                else if(type.equals("Bio Mimicry"))
                {
                    bioTotaal += 1;
                    if(correct)
                    {
                        bioCorrect += 1;
                    }
                }
                else if(type.equals("Energie"))
                {
                    energieTotaal += 1;
                    if(correct)
                    {
                        energieCorrect += 1;
                    }
                }
                else if(type.equals("Dierenwelzijn"))
                {
                    dierenTotaal += 1;
                    if(correct)
                    {
                        dierenCorrect += 1;
                    }
                }
            }
            // Bereken de resultaten
            calcResults(waterCorrect,waterTotaal, "Water");
            calcResults(energieCorrect,energieTotaal, "Energie");
            calcResults(materialenCorrect, materialenTotaal, "Materiaal");
            calcResults(bioCorrect, bioTotaal, "Bio Mimicry");
            calcResults(dierenCorrect, dierenTotaal, "Dierenwelzijn");

            // Start de quizend activity
            Intent quizEnd = new Intent(this, uitslag_venster.class);
            startActivity(quizEnd);
            this.finish();
            socket.disconnect();
            // Sluit de connectie
        }
        else {
            // Geef vraag weer
            Question qActual = questions.get(i);
            String path = qActual.getImagePath();
            ArrayList<QuestionImage> images = datasource.getAllImages();
            for(QuestionImage questionImage: images)
            {
                if(questionImage.getQuestionid() == qActual.getId())
                {
                    Log.d("pathBijLaden", questionImage.getImagePath());
                    loadImageFromStorage(questionImage.getImagePath(),questionImage.getImageName());
                }
            }


            long id = qActual.getId();
            ArrayList<Answer> answers1 = new ArrayList<Answer>();
            for(Answer answer: answers)
            {
                if(answer.getVraagId() == id)
                {
                    answers1.add(answer);
                    if(answer.isGood())
                    {
                        actualAnswer = answer;
                    }
                }
            }

            // Zet de layouts afhankelijk van de inhoud
            ImageView bush = (ImageView)findViewById(R.id.bush);
            RelativeLayout rl = (RelativeLayout)findViewById(R.id.content);

            String type = questions.get(i).getType();
            if(type.equals("Water"))
            {
                bush.setImageResource(R.drawable.bush_blue);
                rl.setBackgroundResource(R.drawable.quiz_gradient_blauw);
            }
            else if(type.equals("Bio Mimicry"))
            {
                bush.setImageResource(R.drawable.bush_purple);
                rl.setBackgroundResource(R.drawable.quiz_gradient_magenta);
            }
            else if(type.equals("Materiaal"))
            {
                bush.setImageResource(R.drawable.bush_brown);
                rl.setBackgroundResource(R.drawable.quiz_gradient_bruin);
            }
            else if(type.equals("Dierenwelzijn"))
            {
                bush.setImageResource(R.drawable.element_dierenwelzijn);
                rl.setBackgroundResource(R.drawable.quiz_gradient_red);
            }
            else{
                bush.setImageResource(R.drawable.bush_orange);
                rl.setBackgroundResource(R.drawable.quiz_gradient_oranje);
            }
            // Zet alle antwoorden zichtbaar
            answer1.setVisibility(View.VISIBLE);
            answer2.setVisibility(View.VISIBLE);
            answer3.setVisibility(View.VISIBLE);
            answer4.setVisibility(View.VISIBLE);
            answer5.setVisibility(View.VISIBLE);
            answer6.setVisibility(View.VISIBLE);
            answer7.setVisibility(View.VISIBLE);
            answer8.setVisibility(View.VISIBLE);

                question.setText(questions.get(i).getQuestion());

            if(questions.get(i).getImagePath() != "") {


                loadImageFromStorage(questions.get(i).getImagePath(), String.valueOf(questions.get(i).getId()));
            }
            // Krijg de antwoorden
            if(answers1.size() > 0) {
                if (answers1.size() == 3) {
                    answer1.setText(answers1.get(0).getAnswer());
                    answer2.setText(answers1.get(1).getAnswer());
                    answer3.setText(answers1.get(2).getAnswer());
                } else {
                    answer1.setText(answers1.get(0).getAnswer());
                    answer2.setText(answers1.get(1).getAnswer());
                    answer3.setText(answers1.get(2).getAnswer());
                    answer4.setText(answers1.get(3).getAnswer());
                }
            }

            // Verberg lege antwoorden
            if(answers1.size() <= 7){answer8.setVisibility(View.GONE);}
            if(answers1.size() <= 6){answer7.setVisibility(View.GONE);}
            if(answers1.size() <= 5){answer6.setVisibility(View.GONE);}
            if(answers1.size() <= 4){answer5.setVisibility(View.GONE);}
            if(answers1.size() <= 3){answer4.setVisibility(View.GONE);}
            if(answers1.size() <= 2){answer3.setVisibility(View.GONE);}


        }
    }

    /**
     * Als de focus veranderd, verander
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(hasFocus == false)
        {
           // socket.disconnect();
        }
    }

    /**
     * Method to store the images internally
     * @param bitmapImage
     * @param filename
     * @return path to file
     */

    private String saveToInternalStorage(Bitmap bitmapImage, String filename){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        Log.d("dir", directory.toString());
        // Create imageDir
        File mypath=new File(directory,filename);

        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(mypath);

            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath();
    }

    /**
     * Bereken resultaten aan de hand van waardes
     * @param aantalGoed
     * @param totaal
     * @param thema
     */
    public void calcResults(int aantalGoed, int totaal, String thema)
    {
        Log.d("aantalgoed", String.valueOf(aantalGoed));
        Log.d("totaal", String.valueOf(totaal));

        double percentage = (double)aantalGoed / (double)totaal * 100;
        int geheelPercentage =(int)percentage;
        Log.d("geheelpercentage", String.valueOf(geheelPercentage));

        if(geheelPercentage < 30)
        {
            Log.d("pr", String.valueOf(geheelPercentage));
        }
        else if(geheelPercentage >= 30 && geheelPercentage < 55)
        {
            Log.d("pr", String.valueOf(geheelPercentage));
        }
        else if(geheelPercentage >= 55 && geheelPercentage < 80)
        {
            Log.d("pr", String.valueOf(geheelPercentage));
        }
        else if(geheelPercentage >= 80)
        {
            Log.d("pr", String.valueOf(geheelPercentage));
        }

            HashMap<Integer, Integer> score = new HashMap<Integer, Integer>();
            score.put(aantalGoed,totaal);

            ((DefaultApplication)this.getApplication()).addThemaScores(thema, score);


    }
    /**
     * Load image from internal path
     * @param path
     */
    private void loadImageFromStorage(String path, String name)
    {
        try {
            File f=new File(path, name);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            ImageView img=(ImageView)findViewById(R.id.headerImage);
            img.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }

    /**
     * Check if the correct answer is pressed
     * if so, socre is incremented
     * @param answer
     */
    public void checkAnswer(String answer)
    {
        int code = ((DefaultApplication)this.getApplication()).getSocketcode();
        String naam = ((DefaultApplication)this.getApplication()).getSocketnaam();

        boolean correct = false;
        if(answer == actualAnswer.getAnswer())
        {

            questionsCorrect += 1;
            correct = true;
        }
        else{
            Vibrator v = (Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            v.vibrate(500);
        }
        beantwoordevragen.put(questions.get(questionNumber), correct);

        // Maak een nieuw object aan
        JSONObject message = new JSONObject();

        try {
            message.put("naam", naam);
            message.put("vraag", questionNumber);
            message.put("goed", correct);
            message.put("quizID",code );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit("sendAnswer", message); // Stuur object
    }


    /**
     * Switch naar de volgende vraag als er een knop in wordt gedrukt
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                checkAnswer(answer1.getText().toString());
                questionNumber += 1;
                display(questionNumber);
                break;
            case R.id.button2:
                checkAnswer(answer2.getText().toString());
                questionNumber += 1;
                display(questionNumber);
                break;
            case R.id.button3:
                checkAnswer(answer3.getText().toString());
                questionNumber += 1;
                display(questionNumber);
                break;
            case R.id.button4:
                checkAnswer(answer4.getText().toString());
                questionNumber += 1;
                display(questionNumber);
                break;
        }
    }






}
