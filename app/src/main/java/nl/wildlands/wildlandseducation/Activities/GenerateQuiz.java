package nl.wildlands.wildlandseducation.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import nl.wildlands.wildlandseducation.GlobalSettings.DefaultApplication;
import nl.wildlands.wildlandseducation.R;


/**
 * Activity waarbij de docent de gegevens van de quiz kan invoeren
 * en deze vervolgens d.m.v. socekts wordt aangemaakt
 */
public class GenerateQuiz extends Activity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    ImageButton backBtn;                                        // ImageButton om terug te gaan
    Button generateQuiz;                                         // Button om quiz te genereren

    Socket mSocket;                                             // Socket voor de quizverbinding

    private SeekBar bar;                                        // Seekbar om tijd dynamisch in te stellen
    private TextView textProgress, genereerQuiz,tijd;          // TextView voor weergave huidige tijdsinput

    /**
     * Actie die voltrokken wordt, als de socket een bepaald bericht krijgt
     */
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            GenerateQuiz.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];             // Zet de data om in een JSONObject
                    String success;                                     // Succes van aanmaken
                    int quizID;                                         // ID van de gegenereerde quiz
                    try {
                        success = data.getString("success");            // Success message
                        quizID = data.getInt("quizid");                 // Quizid
                    } catch (JSONException e) {
                        return;
                    }

                    startNewActivity(success, quizID);                  // Roep startNewActivity aan met de waardes

                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);                                             // Zet layout
        setContentView(R.layout.activity_generate_quiz);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,                // Fullscreen
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        generateQuiz = (Button)findViewById(R.id.generateQuiz);                         // Button voor genereren quiz
        generateQuiz.setOnClickListener(this);                                          // Activeer knopactie

        backBtn = (ImageButton)findViewById(R.id.quitbutton);                           // ImageButton om terug te gaan
        backBtn.setOnClickListener(this);                                               // Activeer knopactie

        bar =(SeekBar)findViewById(R.id.seekBar1);                                      // Seekbar om tijd in te stellen

        bar.setOnSeekBarChangeListener(this);                                           // Actie bij het veranderen van de seekbar

        textProgress = (TextView)findViewById(R.id.textView3);                          // Tekst voor de actuele tijdsinstelling
        genereerQuiz = (TextView)findViewById(R.id.textView1);
        tijd = (TextView)findViewById(R.id.textView2);

        Typeface tf = DefaultApplication.tf2;

        // Verander de lettertypes
        genereerQuiz.setTypeface(tf);
        tijd.setTypeface(tf);

        Typeface tf2 = DefaultApplication.tf;
        // Verander de lettertypes
        generateQuiz.setTypeface(tf2);
        textProgress.setTypeface(tf2);

        mSocket = ((DefaultApplication)this.getApplicationContext()).getSocket();       // Vraag de centrale socket op
        mSocket.connect();                                                              // Maak verbinding met de server
    }


    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.generateQuiz:                                                     // Genereer quiz ingedrukt
            mSocket.emit("createQuiz","");                                              // Verzend het verzoek om een quiz te maken
                startListening();                                                       // Wacht op bevestiging
                break;
            case R.id.quitbutton:
                Intent i = new Intent(this, ChooseQuizGroup.class);                     // Backbutton gaat naar choose quiz group activity
                startActivity(i);
                this.finish();                                                          // Beeindig deze activity
                break;
        }
    }

    public void startListening()
    {
        mSocket.on("quizCreated", onNewMessage);                                        // Start de actie als de socket bevestiging krijgt
    }

    /**
     * Zet het quizid en de lengte van de quiz in de global variables
     * en start een nieuwe activity
     * @param success
     * @param quizID
     */
    public void startNewActivity(String success, int quizID)
    {
        ((DefaultApplication)this.getApplication()).setSocketcode(quizID);              // Zet de socketcode
        ((DefaultApplication)this.getApplication()).setDuration(bar.getProgress());     // Zet de quizduur in minuten
        Intent codeScreen = new Intent(this, QuizStart.class);                          // Intent met het wachtscherm
        startActivity(codeScreen);                                                      // Start het wachtscherm
        this.finish();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        textProgress.setText(progress+ " MIN");                                         // Bij verandering van de balk, zet de actuele tijdinstelling
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
