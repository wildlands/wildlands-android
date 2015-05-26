package nl.wildlands.wildlandseducation.Activities;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import nl.wildlands.wildlandseducation.R;


public class QuizEnd extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_einde);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        TextView tv = (TextView)findViewById(R.id.checkjedocent);
        TextView tv2 = (TextView)findViewById(R.id.voorjouwscore);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/thematext.ttf");
        tv.setTypeface(tf);
        tv2.setTypeface(tf);
    }


}
