package nl.wildlands.wildlandseducation.Activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Set;

import nl.wildlands.wildlandseducation.GlobalSettings.DefaultApplication;
import nl.wildlands.wildlandseducation.R;


public class view_15 extends Activity implements View.OnClickListener {

    Button send, back;
    HashMap<String, String> scores;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_15);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        TextView dequiz = (TextView)findViewById(R.id.dequizis);
        TextView afgelopen = (TextView)findViewById(R.id.afgelopen);
        dequiz.setTypeface(DefaultApplication.tf2);
        afgelopen.setTypeface(DefaultApplication.tf2);
        send = (Button)findViewById(R.id.btn_verstuur);
        send.setOnClickListener(this);
        send.setTypeface(DefaultApplication.tf);


        scores = ((DefaultApplication)this.getApplication()).getScores();
    }


    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.btn_verstuur:
                String newline = System.getProperty("line.separator");
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
               // i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"recipient@example.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "Uitslag quiz");
                String extraContent = "";
                Set<String> keys = scores.keySet();
                for(String key: keys)
                {
                    extraContent += key + newline;
                    extraContent += scores.get(key) + newline;
                }
                i.putExtra(Intent.EXTRA_TEXT, extraContent);
                try {
                    startActivity(Intent.createChooser(i, "Stuur uitslag..."));
                } catch (android.content.ActivityNotFoundException ex) {

                }
        }
    }
}
