package nl.wildlands.wildlandseducation;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;
import android.app.Activity;
import android.os.Bundle;
import android.widget.SeekBar.OnSeekBarChangeListener;


public class view_12 extends ActionBarActivity implements SeekBar.OnSeekBarChangeListener {

    //seekbar object variable
    SeekBar bar;
    //textlabel object
    TextView textProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_12);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_12, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
