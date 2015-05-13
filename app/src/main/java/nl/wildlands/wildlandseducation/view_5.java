package nl.wildlands.wildlandseducation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class view_5 extends Activity implements View.OnClickListener {

    Button startBtn;
    EditText naam, quiz;
    String leerling;
    private int code;
    private Socket mSocket = null;

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            view_5.this.runOnUiThread(new Runnable() {
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
        mSocket = ((DefaultApplication)this.getApplicationContext()).getSocket();
        mSocket.connect();
        setContentView(R.layout.activity_view_5);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        naam = (EditText)findViewById(R.id.editText);
        quiz = (EditText)findViewById(R.id.editText2);
        TextView tv = (TextView)findViewById(R.id.txtJoin);
        TextView tv2 = (TextView)findViewById(R.id.txtName);
        TextView tv3 = (TextView)findViewById(R.id.txtQuiz);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/thematext.ttf");
        tv.setTypeface(tf);
        tv2.setTypeface(tf);
        tv3.setTypeface(tf);
        EditText naam = (EditText)findViewById(R.id.editText);

        naam.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        startBtn = (Button)findViewById(R.id.btnStart);
        startBtn.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnStart:
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
                mSocket.emit("joinQuiz", bericht);
                mSocket.on("joinSuccess", onNewMessage);


                break;
        }
    }

    public void startNewActivity()
    {
        ((DefaultApplication)this.getApplication()).setSocketcode(code);
        ((DefaultApplication)this.getApplication()).setSocketnaam(leerling);
        Intent h = new Intent(this, view_6.class);

        startActivity(h);
        this.finish();
        System.gc();
    }

}
