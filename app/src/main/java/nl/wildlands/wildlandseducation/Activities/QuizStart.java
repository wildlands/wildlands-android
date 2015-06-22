package nl.wildlands.wildlandseducation.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import nl.wildlands.wildlandseducation.GlobalSettings.DefaultApplication;
import nl.wildlands.wildlandseducation.R;


public class QuizStart extends Activity implements View.OnClickListener {

    // Private fields
    private TextView code;
    private Socket mSocket;
    private ArrayList<String> studenten;
    private int duration;
    private Button startQuiz;
    private int topmargin;
    private Typeface tf, tf2;
    private ImageButton quitBtn;

    // Emitter voor joinen van quiz
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            QuizStart.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String naam;

                    try {
                        naam = data.getString("naam");

                    } catch (JSONException e) {
                        return;
                    }

                    addTextView(naam);               // Voeg nieuwe textview met naam toe
                }
            });
        }
    };

    // Emitter voor leaven van quiz
    private Emitter.Listener leftMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            QuizStart.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String naam;
                    try {
                        naam = data.getString("naam");

                    } catch (JSONException e) {
                        return;
                    }
                    String content = naam ;
                    removeStudent(content);         // Verwijder textview van deze naam
                }
            });
        }
    };





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_start);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        studenten = new ArrayList<String>();

        // Initialiseer alle waardes
        code = (TextView)findViewById(R.id.code);
        startQuiz = (Button)findViewById(R.id.startQuiz);
        startQuiz.setOnClickListener(this);
        quitBtn = (ImageButton)findViewById(R.id.quitbutton);
        quitBtn.setOnClickListener(this);
        int quizID = ((DefaultApplication)this.getApplication()).getSocketcode();
         duration = ((DefaultApplication)this.getApplication()).getDuration();
        code.setText(String.valueOf(quizID));
        mSocket = ((DefaultApplication)this.getApplicationContext()).getSocket();
        mSocket.on("somebodyJoined", onNewMessage);
        mSocket.on("somebodyLeaved", leftMessage);

        topmargin = 30;     // Startmargin voor textviews

        tf = DefaultApplication.tf2;
        tf2 = DefaultApplication.tf;

        // Zet lettertypes
        code.setTypeface(tf2);
        startQuiz.setTypeface(tf2);

    }

    /**
     * Verwijder de textview
     * @param name
     */
    public void removeStudent(String name)
    {
        studenten.remove(name);
        RelativeLayout screen = (RelativeLayout) findViewById(R.id.scrollOverzicht);
        screen.removeAllViews();
        for(String student: studenten)
        {
            addTextView(student);
        }
    }

    /**
     * Voeg een nieuwe textview toe
     * @param content
     */
    public void addTextView(String content) {
        TextView tvnew = new TextView(this.getApplicationContext());
        tvnew.setText(content);
        studenten.add(content);         // Voeg toe aan studentenarray

        // Verander de waardes van de textview
        tvnew.setTextColor(Color.parseColor("#FFE102"));
        tvnew.setTypeface(tf);
        tvnew.setTextSize(25);
        RelativeLayout.LayoutParams lp =
                new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.FILL_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, topmargin, 0, 0);
        topmargin += 70;
        tvnew.setLayoutParams(lp);
        tvnew.setGravity(Gravity.CENTER_HORIZONTAL);

        RelativeLayout screen = (RelativeLayout) findViewById(R.id.scrollOverzicht);
        screen.addView(tvnew);      // Voeg toe aan views

    }

    /**
     * Acties voor starten en stoppen
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.startQuiz:

                JSONObject quizData = new JSONObject();         // nieuw quiz object
                int quizID = ((DefaultApplication)this.getApplication()).getSocketcode();
                try
                {
                    quizData.put("quizID", quizID);
                    quizData.put("duration", duration);
                    quizData.put("level", ((DefaultApplication)this.getApplication()).getLevel());
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                mSocket.emit("startQuiz", quizData);            // start de quiz
                Intent scoreScreen = new Intent(this, TrackScores.class);
                startActivity(scoreScreen);                     // start de activity
                this.finish();
                break;
            case R.id.quitbutton:
                Intent i = new Intent(this, GenerateQuiz.class);        // Backbutton gaat naar home activity
                startActivity(i);
                this.finish();                                          // Beeindig deze activity
                break;
        }
    }
}
