package nl.wildlands.wildlandseducation;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.app.Activity;
import android.os.Bundle;
import android.widget.SeekBar.OnSeekBarChangeListener;


public class view_12 extends Activity implements SeekBar.OnSeekBarChangeListener {

    //seekbar object variable
    SeekBar bar;
    //textlabel object
    TextView textProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_12);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //make seekbar object
        bar =(SeekBar)findViewById(R.id.seekBar1);
        //set seekbar listener
        bar.setProgress(0);

        //textlabel for selected time
        textProgress = (TextView)findViewById(R.id.textView3);
        //initControls();

        bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
              public void onStopTrackingTouch(SeekBar seekBar) {
                //add here your implementation
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //add here your implementation
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {

                textProgress.setText(Integer.toString(progress)+ " MIN ");
            }
        });
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
