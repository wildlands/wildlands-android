package nl.wildlands.wildlandseducation.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import nl.wildlands.wildlandseducation.GlobalSettings.DefaultApplication;
import nl.wildlands.wildlandseducation.R;


public class WaitForQuizStart extends Activity implements View.OnClickListener{

    private Socket mSocket = null;

    // Emitter als de quiz gestart wordt
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            WaitForQuizStart.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    int level;
                    int duration;

                    try {
                        level = data.getInt("level");
                        duration = data.getInt("duration");

                    } catch (JSONException e) {
                        return;
                    }

                    startQuiz(level, duration);
                }
            });
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSocket = ((DefaultApplication)this.getApplication()).getSocket();
        setContentView(R.layout.activity_wait_for_quiz_start);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        TextView tv = (TextView)findViewById(R.id.afsluiten);
        TextView tv2 = (TextView)findViewById(R.id.geduld);
        TextView tv3 = (TextView)findViewById(R.id.quizStart);
        TextView tv4 = (TextView)findViewById(R.id.tijdensQuiz);
        Typeface tf = DefaultApplication.tf2;
        tv.setTypeface(tf);
        tv2.setTypeface(tf);
        tv3.setTypeface(tf);
        tv4.setTypeface(tf);


        mSocket.on("startTheQuiz", onNewMessage);
        ImageButton quitBtn = (ImageButton)findViewById(R.id.quitbutton);
        quitBtn.setOnClickListener(this);
    }




    /**
     * Functie die de quiz start
     */
    public void startQuiz(int level, int duration)
    {

        ((DefaultApplication)this.getApplication()).setQuizLevel(level);
        ((DefaultApplication)this.getApplication()).setDuration(duration);
        mSocket.off("startTheQuiz", onNewMessage);
      Intent h = new Intent(this, Quiz.class);
      startActivity(h);
        this.finish();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.quitbutton:
                int quizCode = ((DefaultApplication) this.getApplication()).getSocketcode();
                String naam =((DefaultApplication)this.getApplication()).getSocketnaam();
                JSONObject bericht = new JSONObject();
                try {
                    bericht.put("quizID", quizCode);
                    bericht.put("naam", naam);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mSocket.emit("leaveQuiz", bericht);
                Intent i = new Intent(this, JoinQuiz.class);            // Backbutton gaat naar join quiz activity
                startActivity(i);
                this.finish();                                          // Beeindig deze activity
                break;

        }
    }
}
