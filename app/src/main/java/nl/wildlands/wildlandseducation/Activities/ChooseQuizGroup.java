package nl.wildlands.wildlandseducation.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import nl.wildlands.wildlandseducation.R;

/**
 * ChooseQuizGroup is de activity, waar wordt gekozen voor
 * docent(quizhost) of leerling(deelnemer)
 */

public class ChooseQuizGroup extends Activity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.gc();                                                            // Onnodige data verwijderen
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_quiz_group);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,       // Fullscreen gebruiken
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Button btnDocent = (Button)findViewById(R.id.docent);                   // Docentbutton uit layout
        Button btnLeerling = (Button)findViewById(R.id.leerling);               // Leerlingbutton uit layout
        ImageButton backBtn = (ImageButton)findViewById(R.id.backbutton);     // Terugknop uit layout

        /*
        Click listeners activeren
         */
        btnDocent.setOnClickListener(this);
        btnLeerling.setOnClickListener(this);
        backBtn.setOnClickListener(this);

        /*
        Verander het lettertype van de textview
         */
        TextView tv = (TextView)findViewById(R.id.ikben);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/thematext.ttf");
        tv.setTypeface(tf);

        Typeface tf2 = Typeface.createFromAsset(getAssets(), "fonts/text.ttf");
        btnDocent.setTypeface(tf2);
        btnLeerling.setTypeface(tf2);

    }


    @Override
    public void onClick(View v) {
        System.gc();                                                    // Onnodige data verwijderen, alvorens door te gaan
        switch(v.getId())
        {
            case R.id.backbutton:
                Intent i = new Intent(this, Home.class);                // Backbutton gaat naar home activity
                startActivity(i);
                this.finish();                                          // Beeindig deze activity
                break;
            case R.id.docent:
                Intent docent = new Intent(this, GenerateQuiz.class);   // Docentbutton gaat naar activity waar een quiz kan worden aangemaakt
                startActivity(docent);
                this.finish();                                          // Beeindig deze activity
                break;
            case R.id.leerling:                                         // Leerling button gaat naar activity waar aan een quiz kan worden deelgenomen
                Intent l = new Intent(this, JoinQuiz.class);
                startActivity(l);
                this.finish();                                          // Beeindig deze activity
                break;
        }
    }
}
