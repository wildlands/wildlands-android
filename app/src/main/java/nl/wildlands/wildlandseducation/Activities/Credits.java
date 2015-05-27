package nl.wildlands.wildlandseducation.Activities;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import nl.wildlands.wildlandseducation.R;

/**
 * Activity om creditscherm te weergeven
 *
 */
public class Credits extends Activity implements View.OnClickListener {

    MediaPlayer mp;                                                                             // Mediaplayer voor geluid
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);                                                     // Zet layout
        setContentView(R.layout.activity_credits);                                              // Fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        ImageButton backbutton = (ImageButton)findViewById(R.id.backbutton);                   // Backbutton uit layout
        backbutton.setOnClickListener(this);                                                   // Activeer clickactie

        mp = MediaPlayer.create(this, R.raw.wildlandsfunkyshit);                               // Maak een mediaplayer met het muziekje
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();

            }

        });
        mp.start();                                                                            // Start de muziek
        mp.setLooping(true);                                                                   // Loop tot scherm wordt verlaten
    }



    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(hasFocus == false)
        {
            mp.pause();
        }
        else{
            mp.start();
        }
    }




    @Override
    public void onClick(View v) {
        System.gc();                                                    // Onnodige data verwijderen, alvorens door te gaan
        switch(v.getId()) {
            case R.id.backbutton:
                mp.stop();
                Intent i = new Intent(this, Home.class);                // Backbutton gaat naar home activity
                startActivity(i);
                this.finish();                                          // Beeindig deze activity
                break;
        }

    }
}
