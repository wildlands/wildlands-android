package nl.wildlands.wildlandseducation.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Set;

import nl.wildlands.wildlandseducation.GlobalSettings.DefaultApplication;
import nl.wildlands.wildlandseducation.R;


public class uitslag_venster extends Activity {

    TextView energieScore, waterScore, materiaalScore, bioScore, dierenScore, totaalScore;
    int totaalGoed, beantwoord;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uitslag_venster);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        energieScore = (TextView)findViewById(R.id.energieScore);
        waterScore = (TextView)findViewById(R.id.waterScore);
        materiaalScore = (TextView)findViewById(R.id.materiaalScore);
        bioScore = (TextView)findViewById(R.id.bioScore);
        dierenScore = (TextView)findViewById(R.id.dierenScore);
        totaalScore = (TextView)findViewById(R.id.totaalcore);

        beantwoord = 0;
        totaalGoed = 0;

        HashMap<String, HashMap<Integer, Integer>> scores = ((DefaultApplication)this.getApplication()).getThemaScores();
        Set<String> soorten = scores.keySet();

        for(String string: soorten) {
            HashMap<Integer, Integer> score = scores.get(string);
            String scoreString = "";
            Set<Integer> aantalGoed = score.keySet();
            int goed = 0;
            int totaal = 0;
            for (Integer integer : aantalGoed) {
                goed = integer;
                totaal = score.get(integer);
                beantwoord += totaal;
                totaalGoed += goed;
            }
            if (string.equals("Water"))
            {
                waterScore.setText(goed + "/" + totaal);
            }
            else if(string.equals("Bio Mimicry"))
            {
                bioScore.setText(goed + "/" + totaal);
            }
            else if(string.equals("Energie"))
            {
                energieScore.setText(goed + "/" + totaal);
            }
            else if(string.equals("Materiaal"))
            {
                materiaalScore.setText(goed + "/" + totaal);
            }
            else if(string.equals("Dierenwelzijn"))
            {
                dierenScore.setText(goed + "/" + totaal);
            }
            else if(string.equals("Totaal"))
            {
                totaalScore.setText(goed + "/" + totaal);
            }

        }

    }



}
