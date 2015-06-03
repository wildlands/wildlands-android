package nl.wildlands.wildlandseducation.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Set;

import nl.wildlands.wildlandseducation.GlobalSettings.DefaultApplication;
import nl.wildlands.wildlandseducation.R;


public class uitslag_venster extends Activity {

    TextView energieScore, waterScore, materiaalScore, bioScore, dierenScore, totaalScore, alertText;
    ImageView bush;
    RelativeLayout alertView;
    int totaalGoed, beantwoord;
    final Context context = this;
    LinearLayout layoutOfPopup; PopupWindow popupMessage; Button popupButton, insidePopupButton; TextView popupText;

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
        alertText = (TextView)findViewById(R.id.alertText);
        alertView = (RelativeLayout)findViewById(R.id.alertView);
        bush = (ImageView)findViewById(R.id.bush);

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
                double percentage = (double)goed / (double)totaal * 100;
                String info = "";
                int geheelPercentage =(int)percentage;
                Log.d("geheelpercentage", String.valueOf(geheelPercentage));

                if(geheelPercentage < 30)
                {
                    info = "Oeij! Jouw uitslag is niet zo goed. Je kunt het beste nog een keer naar op verkenning gaan bij de thema's";
                    alertView.setBackgroundResource(R.drawable.alert_red);
                }
                else if(geheelPercentage >= 30 && geheelPercentage < 55)
                {
                    info = "Jammer, net niet! Jouw uitslag is net geen voldoende. Je kunt het beste nog een keer naar op verkenning gaan bij de volgende thema's";
                    alertView.setBackgroundResource(R.drawable.alert_orange);
                }
                else if(geheelPercentage >= 55 && geheelPercentage < 80)
                {
                    info = "Yes, voldoende! Als je wilt kun je het beste nog een keer naar op verkenning gaan bij de volgende thema's";
                    alertView.setBackgroundResource(R.drawable.alert_yellow);
                }
                else if(geheelPercentage >= 80)
                {
                    info ="Geweldig, jij hebt een goed! Bij de volgende thema's heb je misschien iets laten liggen:";
                    alertView.setBackgroundResource(R.drawable.alert_green);
                }
                else if(geheelPercentage == 100)
                {
                    info ="SUPER!!!! Alles goed! Jij bent waarschijnlijk een Ecologisch wezen!";
                    alertView.setBackgroundResource(R.drawable.alert_extra_green);
                }
                alertText.setText(info);
                showAlert(info);
            }

        }






    }

    public void showAlert(String info)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = this.getLayoutInflater();

        // set title


        View resultsView = LayoutInflater.from(getBaseContext()).inflate(R.layout.custom_alert,
            null);



        // set dialog message
        alertDialogBuilder
                .setView(resultsView)


                .setCancelable(false)
                .setPositiveButton("Oke", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        dialog.dismiss();

                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        TextView tv = (TextView)resultsView.findViewById(R.id.alertTextDialog);
        tv.setText(info);


        // show it
        alertDialog.show();
    }




}
