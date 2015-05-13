package nl.wildlands.wildlandseducation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


public class view_4 extends Activity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.gc();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_4);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Button btnDocent = (Button)findViewById(R.id.docent);
        Button btnLeerling = (Button)findViewById(R.id.leerling);
        btnDocent.setOnClickListener(this);
        btnLeerling.setOnClickListener(this);
        ImageButton backArrow = (ImageButton)findViewById(R.id.backbutton);
        backArrow.setOnClickListener(this);
        TextView tv = (TextView)findViewById(R.id.ikben);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/thematext.ttf");
        tv.setTypeface(tf);

    }


    @Override
    public void onClick(View v) {
        Intent i = new Intent(this, Home.class);
        System.gc();
        switch(v.getId())
        {
            case R.id.backbutton:
                startActivity(i);
                this.finish();
                break;
            case R.id.docent:
                // start some activity
                Intent docent = new Intent(this, GenerateQuiz.class);
                startActivity(docent);
                this.finish();
                System.gc();

                break;
            case R.id.leerling:
                // start some other activity
            //    Intent l = new Intent(this, view_5.class);
              //  startActivity(l);
                Intent test = new Intent(this, MainActivity.class);
                startActivity(test);
                this.finish();
                System.gc();
                break;
        }
    }
}
