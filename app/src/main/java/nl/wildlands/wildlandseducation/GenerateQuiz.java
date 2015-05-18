package nl.wildlands.wildlandseducation;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;


public class GenerateQuiz extends Activity implements View.OnClickListener {

    Button generateQuiz, startQuiz;
    TextView tv;
    Socket mSocket;

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            GenerateQuiz.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String success;
                    int quizID;
                    try {
                        success = data.getString("success");
                        quizID = data.getInt("quizid");
                    } catch (JSONException e) {
                        return;
                    }

                    startNewActivity(success, quizID);

                }
            });
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_quiz);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        tv = (TextView)findViewById(R.id.result);
        generateQuiz = (Button)findViewById(R.id.generateQuiz);
        generateQuiz.setOnClickListener(this);

        startQuiz = (Button)findViewById(R.id.startQuiz);
        startQuiz.setOnClickListener(this);

        mSocket = ((DefaultApplication)this.getApplicationContext()).getSocket();
        mSocket.connect();
    }


    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.generateQuiz:
            mSocket.emit("createQuiz","");
                startListening();
                break;
            case R.id.startQuiz:
                JSONObject quizData = new JSONObject();
                int quizID = ((DefaultApplication)this.getApplication()).getSocketcode();
                try {
                    quizData.put("quizID", quizID);
                    quizData.put("duration", 3);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mSocket.emit("startQuiz", quizData);
                Intent scoreScreen = new Intent(this, TrackScores.class);
                startActivity(scoreScreen);
                this.finish();

        }
    }

    public void startListening()
    {
        mSocket.on("quizCreated", onNewMessage);
    }

    public void startNewActivity(String success, int quizID)
    {
        tv.setText(String.valueOf(quizID)
        );
        ((DefaultApplication)this.getApplication()).setSocketcode(quizID);
    }
}
