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

import java.util.HashMap;
import java.util.Set;

import nl.wildlands.wildlandseducation.GlobalSettings.DefaultApplication;
import nl.wildlands.wildlandseducation.R;


public class TrackScores extends Activity implements View.OnClickListener{

    Socket mSocket;
    ImageButton quitButton;
    Button skipbutton;
    TextView tv;
    int topmargin;
    private int aantalCorrect, totaal;
    private int aantalVragen;
    HashMap<String, TextView> leerlingen;
    ProgressBar pb;
    int i = 0;
    CountDownTimer mCountDownTimer;
    int mProgressStatus = 600;
    Handler mHandler = new Handler();
    final Context context = this;

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
                        if(correct == true)
                        {
                            aantalCorrect += 1;
                        }
                        totaal+= 1;
                        aantalVragen = vraagnummer +1;
                    } catch (JSONException e) {
                        return;
                    }
                    String content = naam + "     " + aantalCorrect + "/" + aantalVragen;
                    addTextView(naam, content);


                }
            });
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_14);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        leerlingen = new HashMap<String, TextView>();

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

    public void addTextView(String naam, String content)
    {
        if(leerlingen.get(naam) != null) {
            TextView tvLeerling = leerlingen.get(naam);
            tvLeerling.setText(content);
        }
        else {
            TextView tvnew = new TextView(this.getApplicationContext());
            tvnew.setText(content);
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

            leerlingen.put(naam, tvnew);
            RelativeLayout screen = (RelativeLayout) findViewById(R.id.scrollOverzicht);
            screen.addView(tvnew);
        }
    }

    public void addScoresToMainFrame()
    {
        Set<String> keys = leerlingen.keySet();
        for(String name: keys)
        {
            String score = leerlingen.get(name).getText().toString();
            ((DefaultApplication)this.getApplication()).addScore(score,name);
        }
    }

    public void startAlertdialog(int optie)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = this.getLayoutInflater();
        // set title
        View resultsView = LayoutInflater.from(getBaseContext()).inflate(R.layout.custom_option_alert,
                null);
        TextView title = (TextView)resultsView.findViewById(R.id.titleAlert);
        resultsView.setBackgroundResource(R.drawable.alert_red);

        if(optie == 1) {
            title.setText("QUIZ ANNULEREN");
        }
        else{
            title.setText("QUIZ AFRONDEN");
        }

        title.setTypeface(DefaultApplication.tf);
        alertDialogBuilder
                .setView(resultsView)
                .setCancelable(false)
        ;

        final AlertDialog alertDialog = alertDialogBuilder.create();
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

            tv.setText("Weet u zeker dat u de quiz wilt annuleren?");
        }
        else{
            ja.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    skipQuiz();
                }
            });
            tv.setText("Hiermee wordt de quiz afgerond en de scores berekend.");
        }
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
        TextView alertImage = (TextView)resultsView.findViewById(R.id.alertImage);
        if(optie == 1) {
            alertImage.setText(getString(R.string.question));
        }
        else{
            alertImage.setText(getString(R.string.skip));
        }
        alertImage.setTypeface(font);
        alertDialog.show();

    }

    public void skipQuiz()
    {
        addScoresToMainFrame();
        JSONObject bericht = new JSONObject();
        try {
            bericht.put("quizID", ((DefaultApplication)this.getApplication()).getSocketcode());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("skipQuiz", bericht);
        Intent finish = new Intent(this, view_15.class);
        startActivity(finish);
        this.finish();
    }

    public void abortQuiz()
    {
        JSONObject bericht = new JSONObject();
        try {
            bericht.put("quizID", ((DefaultApplication)this.getApplication()).getSocketcode());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("abortQuiz", bericht);
        Intent start = new Intent(this, QuizStart.class);
        startActivity(start);
        this.finish();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.quitbutton:
                startAlertdialog(1);
                break;
            case R.id.skipButton:
                startAlertdialog(2);
                break;

        }
    }
}
