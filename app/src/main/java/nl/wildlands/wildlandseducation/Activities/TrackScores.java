package nl.wildlands.wildlandseducation.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import nl.wildlands.wildlandseducation.GlobalSettings.DefaultApplication;
import nl.wildlands.wildlandseducation.R;
import nl.wildlands.wildlandseducation.SQLite.QuestionsDataSource;
import nl.wildlands.wildlandseducation.quiz.Question;


public class TrackScores extends Activity implements View.OnClickListener{

    private Socket mSocket;
    private ImageButton quitButton;
    private Button skipbutton;
    private TextView tv;
    private int topmargin;
    private int aantalCorrect, totaal;
    private HashMap<String, HashMap<String, HashMap<Integer,Integer>>> scores;
    private int aantalVragen;
    private HashMap<String, TextView> leerlingen;
    private HashMap<String, HashMap<Integer,Integer>> score;
    private ProgressBar pb;
    private int i = 0;
    private CountDownTimer mCountDownTimer;
    private int mProgressStatus = 600;
    private Handler mHandler = new Handler();
    private ArrayList<Question> questions, questionAll;
    private QuestionsDataSource questionsDataSource;
    private final Context context = this;

    // Emitter voor nieuwe textview
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            TrackScores.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String naam;
                    int vraagnummer;
                    boolean correct;
                    int quizID;
                    try {
                        naam = data.getString("naam");
                        vraagnummer = data.getInt("vraag");
                        correct = data.getBoolean("goed");
                        quizID = data.getInt("quizID");

                    } catch (JSONException e) {
                        return;
                    }


                    addTextView(naam, vraagnummer, correct);


                }
            });
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);                                     // Zet layout
        setContentView(R.layout.activity_track_scores_view);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,        // Fullscreen
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        leerlingen = new HashMap<String, TextView>();
        scores = new HashMap<String, HashMap<String,HashMap<Integer, Integer>>>();
        score = new HashMap<String,HashMap<Integer, Integer>>();

        questions = new ArrayList<Question>();

        questionsDataSource = new QuestionsDataSource(this.getApplicationContext());
        questionsDataSource.open();
        questionAll = questionsDataSource.getAllQuestions();
        int level = ((DefaultApplication)this.getApplication()).getQuizLevel();
        level += 1;

        for(Question question1: questionAll)
        {
            if(question1.getLevel() == level)
            {
                questions.add(question1);
            }
        }

        quitButton = (ImageButton)findViewById(R.id.quitbutton);
        quitButton.setOnClickListener(this);

        skipbutton = (Button)findViewById(R.id.skipButton);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");

        skipbutton.setTypeface(font);
        skipbutton.setOnClickListener(this);

        TextView quizOverzicht = (TextView)findViewById(R.id.quizoverzicht);
        quizOverzicht.setTypeface(DefaultApplication.tf2);

        mSocket = ((DefaultApplication)this.getApplication()).getSocket();
        mSocket.on("receiveAnswer", onNewMessage);

        pb = (ProgressBar)findViewById(R.id.pb);

        // Countdown van de tijd
        int duration = ((DefaultApplication)this.getApplication()).getDuration();
        i = duration * 60;
        int countdown = i * 60 * 1000;
        pb.setMax(i);
        pb.setProgress(i);


        // Start lengthy operation in a background thread
        pb.setProgress(i);

        mCountDownTimer=new CountDownTimer(countdown,1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                Log.v("Log_tag", "Tick of Progress" + i + millisUntilFinished);
                i--;
                pb.setProgress(i);

            }

            @Override
            public void onFinish() {
                //Do what you want
                i--;
                pb.setProgress(i);
            }
        };
        mCountDownTimer.start();

        topmargin = 30;
    }

    /**
     * Voeg textview toe aan layout
     * @param naam
     * @param vraagNummer
     * @param correct
     */
    public void addTextView(String naam, int vraagNummer, boolean correct)
    {
        if(leerlingen.get(naam) != null)            // Als naam er al is
        {
            TextView tvLeerling = leerlingen.get(naam);
            HashMap<Integer,Integer> totaalScore = score.get(naam);
            Set<Integer> keys = totaalScore.keySet();
            String content ="";
            for(Integer aantalgoed: keys)
            {
                content = naam + "  " + String.valueOf(aantalgoed) + "/" + String.valueOf(totaalScore.get(aantalgoed));
            }
            tvLeerling.setText(content);            // Verander de score
        }
        else {
            TextView tvnew = new TextView(this.getApplicationContext());        // Nieuwe textview
            String goedStr = "0";
            if(correct)
            {
                goedStr = "1";
            }
            tvnew.setText(naam + " " + goedStr + "/" + "1" );                      // Verander de score

            // Zet layouts
            tvnew.setTextColor(Color.parseColor("#FFE102"));
            tvnew.setTypeface(DefaultApplication.tf2);
            tvnew.setTextSize(25);
            RelativeLayout.LayoutParams lp =
            new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0,topmargin, 0, 0);
            topmargin += 70;
            tvnew.setLayoutParams(lp);
            tvnew.setGravity(Gravity.CENTER_HORIZONTAL);
            Log.d("vraagnummer",String.valueOf(vraagNummer));
            String thema = questions.get(vraagNummer).getType();
            Log.d("thema", "bij score");

            // Voeg naam en textview toe aan HashMap
            leerlingen.put(naam, tvnew);
            HashMap<String, HashMap<Integer, Integer>> themas = new HashMap<String, HashMap<Integer, Integer>>();
            HashMap<Integer,Integer> vragen = new HashMap<Integer,Integer>();

            int goed = 0;
            if(correct)
            {
                goed = 1;
            }
            vragen.put(goed,1);
            themas.put(thema, vragen);
            scores.put(naam, themas);
            score.put(naam, vragen);
            RelativeLayout screen = (RelativeLayout) findViewById(R.id.scrollOverzicht);

            // Voeg view toe
            screen.addView(tvnew);
        }
    }

    /**
     * Voeg scores toe aan defaultapplication
     */
    public void addScoresToMainFrame()
    {
        Set<String> keys = leerlingen.keySet();     // Haal namen op
        for(String name: keys)
        {
            String score = leerlingen.get(name).getText().toString();       // Haal scores op en voeg toe
            ((DefaultApplication)this.getApplication()).addScore(score,name);
        }
    }

    /**
     * Bouw alert 1 of 2
     * @param optie
     */

    public void startAlertdialog(int optie)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        View resultsView = LayoutInflater.from(getBaseContext()).inflate(R.layout.custom_option_alert,
                null);
        TextView title = (TextView)resultsView.findViewById(R.id.titleAlert);
        resultsView.setBackgroundResource(R.drawable.alert_red);

        if(optie == 1) {
            title.setText("QUIZ ANNULEREN");                // Title vd dialog
        }
        else{
            title.setText("QUIZ AFRONDEN");                 // Title vd dialog
        }

        title.setTypeface(DefaultApplication.tf);
        alertDialogBuilder
                .setView(resultsView)
                .setCancelable(false)
        ;

        final AlertDialog alertDialog = alertDialogBuilder.create();                // Maak nieuwe dialog


        TextView tv = (TextView)resultsView.findViewById(R.id.alertTextDialog);
        Button nee = (Button)resultsView.findViewById(R.id.alertBtnNo);
        nee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        Button ja = (Button)resultsView.findViewById(R.id.alertBtnYes);
        if(optie == 1) {
            ja.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    abortQuiz();
                }
            });

            tv.setText("Weet u zeker dat u de quiz wilt annuleren?");               // Verander tekst
        }
        else{
            ja.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    skipQuiz();
                }
            });
            tv.setText("Hiermee wordt de quiz afgerond en de scores berekend.");   // Verander tekst
        }
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
        TextView alertImage = (TextView)resultsView.findViewById(R.id.alertImage);
        if(optie == 1) {
            alertImage.setText(getString(R.string.question));                       // Verander logootje
        }
        else{
            alertImage.setText(getString(R.string.skip));                           // Verander logootje
        }
        alertImage.setTypeface(font);
        alertDialog.show();                                                         // Start dialog

    }

    /**
     * Skip de quiz
     *
     */
    public void skipQuiz()
    {
        addScoresToMainFrame();         // Voeg scores toe aan DefaultApplication
        JSONObject bericht = new JSONObject();      // Maak nieuw object
        try {
            bericht.put("quizID", ((DefaultApplication)this.getApplication()).getSocketcode());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("skipQuiz", bericht);          // Skip the quiz
        Intent finish = new Intent(this, SendQuiz.class); // Ga door naar venster
        startActivity(finish);
        this.finish();
    }

    /**
     * Abort de quiz
     */
    public void abortQuiz()
    {
        JSONObject bericht = new JSONObject();              // nieuw object
        try {
            bericht.put("quizID", ((DefaultApplication)this.getApplication()).getSocketcode());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("abortQuiz", bericht);                 // Emit bericht met object
        Intent start = new Intent(this, QuizStart.class);   // Ga naar start quiz venster
        startActivity(start);
        this.finish();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.quitbutton:
                startAlertdialog(1);                // Start alertdialog
                break;
            case R.id.skipButton:
                startAlertdialog(2);                // Start andere dialog
                break;

        }
    }
}
