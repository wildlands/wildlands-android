package nl.wildlands.wildlandseducation.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import nl.wildlands.wildlandseducation.GlobalSettings.DefaultApplication;
import nl.wildlands.wildlandseducation.R;


public class uitslag_venster extends Activity implements View.OnClickListener {

    TextView energieScore, waterScore, materiaalScore, bioScore, dierenScore, totaalScore, alertText;
    ImageView bush;
    RelativeLayout alertView;
    int totaalGoed, beantwoord;
    int scoreNiveau;
    ImageButton backBtn;
    final Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);                                         // Zet de layout
        setContentView(R.layout.activity_uitslag_venster);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,            // Fullscreen
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        energieScore = (TextView)findViewById(R.id.energieScore);
        waterScore = (TextView)findViewById(R.id.waterScore);
        materiaalScore = (TextView)findViewById(R.id.materiaalScore);
        bioScore = (TextView)findViewById(R.id.bioScore);
        dierenScore = (TextView)findViewById(R.id.dierenScore);
        totaalScore = (TextView)findViewById(R.id.totaalcore);
        alertText = (TextView)findViewById(R.id.alertText);
        alertView = (RelativeLayout)findViewById(R.id.alertView);
        bush = (ImageView)findViewById(R.id.bush);

        TextView tv1 = (TextView)findViewById(R.id.bio);
        TextView tv2 = (TextView)findViewById(R.id.materiaal);
        TextView tv3 = (TextView)findViewById(R.id.dieren);
        TextView tv4 = (TextView)findViewById(R.id.water);
        TextView tv5 = (TextView)findViewById(R.id.energie);
        TextView totaalview = (TextView)findViewById(R.id.totaal);

        Typeface tf = DefaultApplication.tf2;
        tv1.setTypeface(tf);
        tv2.setTypeface(tf);
        tv3.setTypeface(tf);
        tv4.setTypeface(tf);
        tv5.setTypeface(tf);
        totaalview.setTypeface(tf);
        energieScore.setTypeface(tf);
        waterScore.setTypeface(tf);
        materiaalScore.setTypeface(tf);
        bioScore.setTypeface(tf);
        dierenScore.setTypeface(tf);
        totaalScore.setTypeface(tf);

        backBtn = (ImageButton)findViewById(R.id.backbutton);
        backBtn.setOnClickListener(this);

        beantwoord = 0;
        totaalGoed = 0;

        HashMap<String, HashMap<Integer, Integer>> scores = ((DefaultApplication)this.getApplication()).getThemaScores();
        Set<String> soorten = scores.keySet();
        ArrayList<String> retryThemas = new ArrayList<String>();

        // Bereken de scores en geef de uitslag
        for(String string: soorten) {
            HashMap<Integer, Integer> score = scores.get(string);
            String scoreString = "";
            Set<Integer> aantalGoed = score.keySet();
            int goed = 0;
            int totaal = 0;


            for (Integer integer : aantalGoed)
            {
                goed = integer;
                totaal = score.get(integer);
                beantwoord += totaal;
                totaalGoed += goed;
            }
            double percentageThema = (double)goed / (double)totaal * 100;
            // Per thema verander de tekst
            if (string.equals("Water"))
            {
                waterScore.setText(goed + "/" + totaal);
                if(percentageThema <= 50)
                {
                    retryThemas.add("Water");
                }
            }
            else if(string.equals("Bio Mimicry"))
            {
                bioScore.setText(goed + "/" + totaal);
                if(percentageThema <= 50)
                {
                    retryThemas.add("Bio Mimicry");
                }
            }
            else if(string.equals("Energie"))
            {
                energieScore.setText(goed + "/" + totaal);
                if(percentageThema <= 50)
                {
                    retryThemas.add("Energie");
                }
            }
            else if(string.equals("Materiaal"))
            {
                materiaalScore.setText(goed + "/" + totaal);
                if(percentageThema <= 50)
                {
                    retryThemas.add("Materiaal");
                }
            }
            else if(string.equals("Dierenwelzijn"))
            {
                dierenScore.setText(goed + "/" + totaal);
                if(percentageThema <= 50)
                {
                    retryThemas.add("Dierenwelzijn");
                }
            }
            else if(string.equals("Totaal"))
            {
                // Bij het totoaal, reken het percentage en vul de info in
                totaalScore.setText(goed + "/" + totaal);
                double percentage = (double)goed / (double)totaal * 100;
                String info = "";
                int geheelPercentage =(int)percentage;
                Log.d("geheelpercentage", String.valueOf(geheelPercentage));

                if(geheelPercentage < 30)
                {
                    if(retryThemas.size() >= 2) {
                        info = "Oeij! Jouw uitslag is niet zo goed. Je kunt het beste nog een keer naar op verkenning gaan bij de thema's";

                        info += ": " + retryThemas.get(0) + " en " + retryThemas.get(1);
                    }
                    else{
                        info = "Oeij! Jouw uitslag is niet zo goed.";
                    }
                    alertView.setBackgroundResource(R.drawable.alert_red);
                    scoreNiveau = 0;
                }
                else if(geheelPercentage >= 30 && geheelPercentage < 55)
                {
                    if(retryThemas.size() >= 2) {
                        info = "Jammer, net niet! Jouw uitslag is net geen voldoende. Je kunt het beste nog een keer naar op verkenning gaan bij de volgende thema's";
                        info += ": " + retryThemas.get(0) + " en " + retryThemas.get(1);

                    }
                    else{
                        info = "Jammer, net niet! Jouw uitslag is net geen voldoende.";
                    }
                    alertView.setBackgroundResource(R.drawable.alert_orange);
                    scoreNiveau = 1;
                }
                else if(geheelPercentage >= 55 && geheelPercentage < 80)
                {
                    if(retryThemas.size() >= 2) {
                        info = "Yes, voldoende! Als je wilt kun je het beste nog een keer naar op verkenning gaan bij de volgende thema's";
                        info += ": " + retryThemas.get(0) + " en " + retryThemas.get(1);
                    }
                    else{
                        info = "Yes, voldoende!";
                    }
                    alertView.setBackgroundResource(R.drawable.alert_yellow);
                    scoreNiveau = 2;
                }
                else if(geheelPercentage >= 80 && geheelPercentage < 100)
                {
                    info ="Geweldig, jij hebt een goed!";
                    alertView.setBackgroundResource(R.drawable.alert_green);
                    scoreNiveau = 3;
                }
                else if(geheelPercentage == 100)
                {
                    info ="SUPER!!!! Alles goed! Jij bent waarschijnlijk een Ecologisch wezen!";
                    alertView.setBackgroundResource(R.drawable.alert_extra_green);
                    scoreNiveau = 4;
                }
                alertText.setText(info);
                showAlert(info, scoreNiveau);
            }

        }

    }

    /**
     * Laat alert zien met info en niveau
     * @param info
     * @param niveau
     */

    public void showAlert(String info, int niveau)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        View resultsView = LayoutInflater.from(getBaseContext()).inflate(R.layout.custom_alert,
            null);
        TextView title = (TextView)resultsView.findViewById(R.id.titleAlert);
        title.setTypeface(DefaultApplication.tf);
        String image = "";

        // Zet afhankelijk van niveau de background, tekst en afbeelding
        if(niveau == 0) {
            resultsView.setBackgroundResource(R.drawable.alert_red);
            title.setText("SLECHT");
            image = "thumbs";
        }
        else if(niveau == 1)
        {
            resultsView.setBackgroundResource(R.drawable.alert_orange);
            title.setText("ONVOLDOENDE");
            image = "thumbs";
        }
        else if(niveau == 2)
        {
            resultsView.setBackgroundResource(R.drawable.alert_yellow);
            title.setText("VOLDOENDE");
            image = "check";
        }
        else if(niveau == 3)
        {
            resultsView.setBackgroundResource(R.drawable.alert_green);
            title.setText("GOED");
            image = "check";
        }
        else if(niveau == 4)
        {
            resultsView.setBackgroundResource(R.drawable.alert_extra_green);
            title.setText("SUPER");
            image = "thumbsUp";
        }


        // set dialog message
        alertDialogBuilder
                .setView(resultsView)
                .setCancelable(false)
               ;

        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();
        TextView tv = (TextView)resultsView.findViewById(R.id.alertTextDialog);
        Button dismiss = (Button)resultsView.findViewById(R.id.alertBtn);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
        TextView alertImage = (TextView)resultsView.findViewById(R.id.alertImage);
        String string = "";
        // Verander de afbeeldingsstring
        if(image.equals("thumbs")) {
            string = getString(R.string.thumbs);
        }
        else if(image.equals("check"))
        {
            string = getString(R.string.check);
        }
        else{
            string = getString(R.string.thumbsUp);
        }

        // Zet de afbeelding
        alertImage.setText(string);
        alertImage.setTypeface(font);

        tv.setText(info);


        // show it
        alertDialog.show();
    }


    /**
     * Backbutton gaat terug naar menu
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.backbutton:
                Intent chooseQuiz = new Intent(this, ChooseQuizGroup.class);
                startActivity(chooseQuiz);
                this.finish();
                break;
        }
    }
}
