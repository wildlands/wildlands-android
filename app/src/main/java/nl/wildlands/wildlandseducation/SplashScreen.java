package nl.wildlands.wildlandseducation;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.WindowManager;


/*
Splashscreen voor het begin van de app
Weergave van een x aantal seconden
 */

public class SplashScreen extends Activity {
    private static final int MSDELAY = 1500;            // Aantal ms voor het doorschakelen naar contentweergave
    private static final int DISPLAY_DATA = 1;          // Checkwaarde voor Handler

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mHandler.sendEmptyMessageDelayed(DISPLAY_DATA, MSDELAY);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == DISPLAY_DATA)
            {
                startKaart();                       // Roep functie aan na splashtijd
            }
        }
    };

    /**
     * Functie die de kaart activiteit start
     */
    public void startKaart()
    {
        //Intent h = new Intent(this, Home.class);
        //startActivity(h);
        Intent level = new Intent(this, LevelChoice.class);
        startActivity(level);

        this.finish();
    }


}
