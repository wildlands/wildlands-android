package nl.wildlands.wildlandseducation;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class TrackScores extends Activity implements View.OnClickListener{

    Socket mSocket;
    Button closeSocket;
    TextView tv;
    int topmargin;
    private int aantalCorrect, totaal;
    private int aantalVragen;
    HashMap<String, TextView> leerlingen;

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

        mSocket = ((DefaultApplication)this.getApplication()).getSocket();
        mSocket.on("receiveAnswer", onNewMessage);

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


    @Override
    public void onClick(View v) {
        switch(v.getId())
        {

        }
    }
}
