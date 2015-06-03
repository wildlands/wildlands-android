package nl.wildlands.wildlandseducation.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import nl.wildlands.wildlandseducation.GlobalSettings.DefaultApplication;
import nl.wildlands.wildlandseducation.R;


public class WaitForQuizStart extends Activity {

    private Socket mSocket = null;

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
    public void startQuiz(int level, int duration)
    {
        /*
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Quiz gestart")
                        .setContentText("Begin nu");
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, Quiz.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(Home.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        int mId = 0;
        mNotificationManager.notify(mId, mBuilder.build());
*/

        ((DefaultApplication)this.getApplication()).setQuizLevel(level);
        ((DefaultApplication)this.getApplication()).setDuration(duration);
      Intent h = new Intent(this, Quiz.class);
      startActivity(h);
        this.finish();
    }

}
