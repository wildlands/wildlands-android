package nl.wildlands.wildlandseducation;

import android.app.Activity;
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


public class TrackScores extends Activity implements View.OnClickListener{

    Socket mSocket;
    Button closeSocket;
    TextView tv;
    private int aantalCorrect, totaal;

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
                    } catch (JSONException e) {
                        return;
                    }
                    tv.setText(naam + "     " + aantalCorrect + "/" + totaal);


                }
            });
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_scores);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        tv = (TextView)findViewById(R.id.checkScore);
        closeSocket = (Button)findViewById(R.id.socket);
        closeSocket.setOnClickListener(this);
        mSocket = ((DefaultApplication)this.getApplication()).getSocket();
        mSocket.on("receiveAnswer", onNewMessage);

    }


    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.socket:
                mSocket.disconnect();
                break;
        }
    }
}
