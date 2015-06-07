package nl.wildlands.wildlandseducation.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import nl.wildlands.wildlandseducation.GlobalSettings.DefaultApplication;
import nl.wildlands.wildlandseducation.R;


/**
 * JoinQuiz is het scherm voor het invoeren van naam en quizcode
 * Dit is voor een leerling om deel te nemen aan een quiz
 */

public class JoinQuiz extends Activity implements View.OnClickListener {

    ImageButton backBtn;                // ImageButton om terug te gaan
    Button startBtn;                    // Button om te verbinden
    EditText naam, quiz;                // Invoervelden voor naam en code
    String leerling;                    // Naam vd leerling
    private int code;                   // Code voor de quiz
    private Socket mSocket = null;      // De socket voor quiz verbinding

    /*
    Actie bij het krijgen van joinSuccess event.
    startNewActivity wordt aangeroepn
     */
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            JoinQuiz.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startNewActivity();
                }
            });
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSocket = ((DefaultApplication)this.getApplicationContext()).getSocket(); // Krijg global socket
        mSocket.connect();                                                        // Verbindit socket

        setContentView(R.layout.activity_join_quiz);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,          // Gebruik fullscreen
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Invoervelden
        naam = (EditText)findViewById(R.id.editText);
        quiz = (EditText)findViewById(R.id.editText2);

        // Textviews
        TextView tv = (TextView)findViewById(R.id.txtJoin);
        TextView tv2 = (TextView)findViewById(R.id.txtName);
        TextView tv3 = (TextView)findViewById(R.id.txtQuiz);

        // Verander lettertype van de verschillende textviews
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/thematext.ttf");
        tv.setTypeface(tf);
        tv2.setTypeface(tf);
        tv3.setTypeface(tf);




        // Zet de invoer van de naam op alleen hoofdletters om het lettertype mooi te laten lijken

        InputFilter[] filters = new InputFilter[2];
        filters[0] = new InputFilter.LengthFilter(25);
        filters[1] = new InputFilter.AllCaps();
        naam.setFilters(filters);

        startBtn = (Button)findViewById(R.id.btnStart);
        startBtn.setOnClickListener(this);
        backBtn = (ImageButton)findViewById(R.id.quitbutton);
        backBtn.setOnClickListener(this);

        Typeface tf2 = Typeface.createFromAsset(getAssets(), "fonts/text.ttf");
        naam.setTypeface(tf2);
        quiz.setTypeface(tf2);
        startBtn.setTypeface(tf2);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnStart:
                if(naam.getText().length() > 0 && quiz.getText().length() > 0)  // Als er bij beide velden invoer is
                {
                    // Haal de data uit de velden en zet deze om in een JSONObject
                    leerling = naam.getText().toString();
                    code = Integer.parseInt(quiz.getText().toString());
                    String quizCode = String.valueOf(code);
                    JSONObject bericht = new JSONObject();

                    try {
                        bericht.put("quizID", quizCode);
                        bericht.put("naam", leerling);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // Stuur het joinQuiz event naar de socket
                    mSocket.emit("joinQuiz", bericht);

                    // Wacht op joinSuccess event
                    mSocket.on("joinSuccess", onNewMessage);

                    makeToast("ZOEK NAAR QUIZ");
                }
                else if(naam.getText().length() > 0 && quiz.getText().length() == 0) // Als quizcode leeg is
                {
                    makeToast("GEEN CODE INGEVULD");                                 // Maak een toast met deze message
                }
                else if(naam.getText().length() == 0 && quiz.getText().length() >0)  // Als naam leeg is
                {
                    makeToast("GEEN NAAM INGEVULD");                                 // Maak een toast met deze message
                }
                else                                                                 // Als beide leeg zijn
                {
                    makeToast("GEEN GEGEVENS INGEVULD");                             // Maak een toast met deze message
                }
                break;
            case R.id.quitbutton:
                Intent i = new Intent(this, ChooseQuizGroup.class);     // Backbutton gaat naar Choose quiz group activity
                startActivity(i);
                this.finish();                                          // Beeindig deze activity
                break;
        }
    }

    /**
     * Maakt een nieuwe toast aan met een customlayout
     * En laat deze zien
     * @param message
     */
    public void makeToast(String message)
    {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) findViewById(R.id.toast_layout_root));                  // Gebruik de custom_toast layout

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);                                                      // Zet de tekst aan de hand van de parameter
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);                                      // Zet korte duratie
        toast.setView(layout);
        toast.show();                                                               // Laat de toast zien
    }

    /**
     * Zet de socketnaam en code in de global variabelen
     * start het wachtscherm van de quiz
     */
    public void startNewActivity()
    {
        ((DefaultApplication)this.getApplication()).setSocketcode(code);            // Zet quizcode in global
        ((DefaultApplication)this.getApplication()).setSocketnaam(leerling);        // Zet leerlingnaam in global


        Intent h = new Intent(this, WaitForQuizStart.class);                        // Start wachtscherm
        startActivity(h);
        this.finish();

        System.gc();                                                                // Verwijder onnodige data
    }

}
