package nl.wildlands.wildlandseducation.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
<<<<<<< HEAD
import android.widget.RelativeLayout;
=======
import android.widget.ImageButton;
>>>>>>> ce3ed80b59578c2eb4c9153a894c90c3facd21b5
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import nl.wildlands.wildlandseducation.GlobalSettings.DefaultApplication;
import nl.wildlands.wildlandseducation.R;


public class QuizStart extends Activity implements View.OnClickListener {

    TextView code;
    Socket mSocket;
    int duration;
    Button startQuiz;
<<<<<<< HEAD
    int topmargin;

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
                    String content = naam ;
                    addTextView(content);


                }
            });
        }
    };


=======
    ImageButton quitBtn;
>>>>>>> ce3ed80b59578c2eb4c9153a894c90c3facd21b5
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_13);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

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

        topmargin = 30;
    }

    public void addTextView(String content) {
        TextView tvnew = new TextView(this.getApplicationContext());
        tvnew.setText(content);
        tvnew.setTextColor(Color.parseColor("#FFE102"));
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
        screen.addView(tvnew);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.startQuiz:

                JSONObject quizData = new JSONObject();
                int quizID = ((DefaultApplication)this.getApplication()).getSocketcode();
                try {
                    quizData.put("quizID", quizID);
                    quizData.put("duration", duration);
                    quizData.put("level", ((DefaultApplication)this.getApplication()).getLevel());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mSocket.emit("startQuiz", quizData);
                Intent scoreScreen = new Intent(this, TrackScores.class);
                startActivity(scoreScreen);
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
