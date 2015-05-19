package nl.wildlands.wildlandseducation;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;


public class GenerateQuiz extends Activity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    Button generateQuiz, startQuiz;
    TextView tv;
    Socket mSocket;
    //seekbar object variable
    private SeekBar bar;
    //textlabel object
    private TextView textProgress;

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

                    Log.d("Zou nu zijn, wolla", "wolla");
                    startNewActivity(success, quizID);

                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_12);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        tv = (TextView)findViewById(R.id.result);
        generateQuiz = (Button)findViewById(R.id.generateQuiz);
        generateQuiz.setOnClickListener(this);

        //startQuiz = (Button)findViewById(R.id.startQuiz);
       // startQuiz.setOnClickListener(this);
        bar =(SeekBar)findViewById(R.id.seekBar1);
        //set seekbar listener
        bar.setOnSeekBarChangeListener(this);
        //textlabel for selected time
        textProgress = (TextView)findViewById(R.id.textView3);

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


        }
    }

    public void startListening()
    {
        mSocket.on("quizCreated", onNewMessage);
    }

    public void startNewActivity(String success, int quizID)
    {

        ((DefaultApplication)this.getApplication()).setSocketcode(quizID);
        ((DefaultApplication)this.getApplication()).setDuration(bar.getProgress());
        Intent codeScreen = new Intent(this, view_13.class);
        startActivity(codeScreen);
        this.finish();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        textProgress.setText(progress+ " MIN");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
