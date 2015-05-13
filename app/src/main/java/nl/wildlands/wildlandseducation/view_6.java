package nl.wildlands.wildlandseducation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;


public class view_6 extends Activity {

    private Socket mSocket = null;

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            view_6.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startQuiz();
                }
            });
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSocket = ((DefaultApplication)this.getApplication()).getSocket();
        setContentView(R.layout.activity_view_6);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        TextView tv = (TextView)findViewById(R.id.afsluiten);
        TextView tv2 = (TextView)findViewById(R.id.geduld);
        TextView tv3 = (TextView)findViewById(R.id.quizStart);
        TextView tv4 = (TextView)findViewById(R.id.tijdensQuiz);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/thematext.ttf");
        tv.setTypeface(tf);
        tv2.setTypeface(tf);
        tv3.setTypeface(tf);
        tv4.setTypeface(tf);
        mSocket.on("startTheQuiz", onNewMessage);

    }




    /**
     * Functie die de quiz start
     */
    public void startQuiz()
    {
        Intent h = new Intent(this, MainActivity.class);
        startActivity(h);
        this.finish();
    }

}
