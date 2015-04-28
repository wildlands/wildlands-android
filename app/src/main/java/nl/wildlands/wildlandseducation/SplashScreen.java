package nl.wildlands.wildlandseducation;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class SplashScreen extends Activity {
    private static final int DISPLAY_DATA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mHandler.sendEmptyMessageDelayed(DISPLAY_DATA, 1500);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // Do task here
            if (msg.what == DISPLAY_DATA) startKaart();
        }
    };

    public void startKaart()
    {
        Intent h = new Intent(this, Kaart.class);
        startActivity(h);
        this.finish();
    }


}
