package nl.wildlands.wildlandseducation.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_13);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        code = (TextView)findViewById(R.id.code);
        startQuiz = (Button)findViewById(R.id.startQuiz);
        startQuiz.setOnClickListener(this);
        int quizID = ((DefaultApplication)this.getApplication()).getSocketcode();
         duration = ((DefaultApplication)this.getApplication()).getDuration();
        code.setText(String.valueOf(quizID));
        mSocket = ((DefaultApplication)this.getApplicationContext()).getSocket();
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mSocket.emit("startQuiz", quizData);
                Intent scoreScreen = new Intent(this, TrackScores.class);
                startActivity(scoreScreen);
                this.finish();
        }
    }
}
