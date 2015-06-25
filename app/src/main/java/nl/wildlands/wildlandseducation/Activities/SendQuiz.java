package nl.wildlands.wildlandseducation.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Set;

import nl.wildlands.wildlandseducation.GlobalSettings.DefaultApplication;
import nl.wildlands.wildlandseducation.R;
import nl.wildlands.wildlandseducation.Score;


public class SendQuiz extends Activity implements View.OnClickListener {

    private Button send;
    private ImageButton back;
    private HashMap<String, String> scores;
    private HashMap<String, Score> scoresNew;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_quiz);                              // Zet layout
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,        // Fullscreen
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        TextView dequiz = (TextView)findViewById(R.id.dequizis);
        TextView afgelopen = (TextView)findViewById(R.id.afgelopen);
        dequiz.setTypeface(DefaultApplication.tf2);
        afgelopen.setTypeface(DefaultApplication.tf2);
        send = (Button)findViewById(R.id.btn_verstuur);
        back = (ImageButton)findViewById(R.id.backbutton);
        send.setOnClickListener(this);
        back.setOnClickListener(this);
        send.setTypeface(DefaultApplication.tf);


        scores = ((DefaultApplication)this.getApplication()).getScores();       // Scores opvragen
        scoresNew = ((DefaultApplication)this.getApplication()).getScoresNew();
        Log.d("size2", String.valueOf(scoresNew.size()));
    }


    /**
     * Click acties
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.btn_verstuur:
                // Maak een nieuwe email aan
                String newline = System.getProperty("line.separator");
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_SUBJECT, "Uitslag quiz");
                String extraContent = "";
                Set<String> keys = scores.keySet();
                //for(String key: keys)
               // {
                //    extraContent += key + newline;                  // Voeg content toe
               //     extraContent += scores.get(key) + newline;
             //   }
                Set<String> keysNew = scoresNew.keySet();
                for(String name: keysNew)
                {
                    Score sc = scoresNew.get(name);
                    extraContent += name + newline;
                    extraContent += "Energie " + sc.getEnergiescore() + "/" + sc.getEnergietotaal() + newline;
                    extraContent += "Water " + sc.getWaterscore() + "/" + sc.getWatertotaal() + newline;
                    extraContent += "Materialen " + sc.getMateriaalscore() + "/" + sc.getMateriaaltotaal() + newline;
                    extraContent += "Bio Mimicry " + sc.getBioscore() + "/" + sc.getBiototaal() + newline;
                    extraContent += "Dierenwelzijn " + sc.getDierenscore() + "/" + sc.getDierentotaal() + newline + newline;

                }
                i.putExtra(Intent.EXTRA_TEXT, extraContent);
                try {
                    startActivity(Intent.createChooser(i, "Stuur uitslag..."));
                } catch (android.content.ActivityNotFoundException ex) {

                }
                break;
            case R.id.backbutton:
                // Ga terug
                Intent chooseQuiz = new Intent(this, ChooseQuizGroup.class);
                startActivity(chooseQuiz);
                this.finish();
                break;
        }
    }
}
