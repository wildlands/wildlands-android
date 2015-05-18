package nl.wildlands.wildlandseducation;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;


public class view_12 extends Activity implements SeekBar.OnSeekBarChangeListener {

    //seekbar object variable
    private SeekBar bar;
    //textlabel object
    private TextView textProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_12);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //make seekbar object
        bar =(SeekBar)findViewById(R.id.seekBar);
        //set seekbar listener
        bar.setOnSeekBarChangeListener(this);

        //textlabel for selected time
        textProgress = (TextView)findViewById(R.id.textView3);
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        //change time
        textProgress.setText(progress+ " MIN");

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
