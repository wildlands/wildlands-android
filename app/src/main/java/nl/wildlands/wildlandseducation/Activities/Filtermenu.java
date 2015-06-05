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
 * Filtermenu is de activity waarbij er tussen een van de vijf hoofdthema's kan worden
 * gekozen, alvorens de kaart te bekijken.
 */

public class Filtermenu extends Activity implements View.OnClickListener {

    private Button btnWater, btnEnergie, btnMaterialen, btnBio, btnDieren;              // De 5 buttons voor de hoofdthema's
    private ImageButton backBtn;                                                        // backbutton om terug te kunnen


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.gc();                                                                    // Onnodige data verwijderen

        super.onCreate(savedInstanceState);                                             // Zet layout
        setContentView(R.layout.filtermenu);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,                // Op fullscreen
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Buttons uit de layout halen
        btnWater = (Button)findViewById(R.id.btnWater);
        btnEnergie = (Button)findViewById(R.id.btnEnergie);
        btnMaterialen = (Button)findViewById(R.id.btnMaterialen);
        btnBio = (Button)findViewById(R.id.btnBio);
        btnDieren = (Button)findViewById(R.id.btnDieren);
        backBtn = (ImageButton)findViewById(R.id.backbutton);

        // De terugknop, dit is het pijltje
        ImageButton btnBack = (ImageButton)findViewById(R.id.backbutton);

        // Verander het lettertype van de tekst
        TextView tv = (TextView)findViewById(R.id.themaKiezen);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/thematext.ttf");
        Typeface tf2 = Typeface.createFromAsset(getAssets(), "fonts/text.ttf");

        tv.setTypeface(tf);

        // Zet de clicklisteners van de buttons aan
        btnBack.setOnClickListener(this);
        btnWater.setOnClickListener(this);
        btnEnergie.setOnClickListener(this);
        btnMaterialen.setOnClickListener(this);
        btnBio.setOnClickListener(this);
        btnDieren.setOnClickListener(this);
        backBtn.setOnClickListener(this);

        btnWater.setTypeface(tf2);
        btnEnergie.setTypeface(tf2);
        btnMaterialen.setTypeface(tf2);
        btnBio.setTypeface(tf2);
        btnDieren.setTypeface(tf2);
        // Begin met het infaden van de buttons.
        animateFadeIn();


    }

    /**
     * Schuift de buttons van de zijkanten naar het midden, bij het starten van het scherm
     */
    public void animateFadeIn()
    {
        // Verplaats de buttons naar fysieke plaatsen buiten het scherm
        btnBio.setTranslationX(1000);
        btnEnergie.setTranslationX(-1000);
        btnWater.setTranslationX(1000);
        btnMaterialen.setTranslationX(-1000);
        btnDieren.setTranslationX(-1000);

        // Maak de buttons zichtbaar
        btnBio.setVisibility(View.VISIBLE);
        btnEnergie.setVisibility(View.VISIBLE);
        btnWater.setVisibility(View.VISIBLE);
        btnMaterialen.setVisibility(View.VISIBLE);
        btnDieren.setVisibility(View.VISIBLE);

        // Animeer alle buttons naar x = 0, oftewel het midden van het scherm in 1 sec
        btnEnergie.animate()
                .translationX(0)
                .setDuration(1000);
        btnBio.animate()
                .translationX(0)
                .setDuration(1000);
        btnMaterialen.animate()
                .translationX(0)
                .setDuration(1000);
        btnWater.animate()
                .translationX(0)
                .setDuration(1000);
        btnDieren.animate()
                .translationX(0)
                .setDuration(1000);
    }




    @Override
    public void onClick(View v) {
        System.gc();                                                                // Onnodige data verwijderen
        Intent i = new Intent(this, Home.class);                                    // Intent om terug te keren naar homescreen
        Intent h = new Intent(this, Kaart.class);                                   // Intent om naar de map te gaan
        switch(v.getId())
        {
            case R.id.backbutton:                                                   // Bij terugknop
                startActivity(i);                                                   // Start homescreen
                this.finish();
                break;
            case R.id.btnBio:                                                       // Bio mimicry knop
                h.putExtra("TYPE", "Bio Mimicry");                                  // Geef een type mee aan de intent
                startActivity(h);                                                   // Start de intent
                break;
            case R.id.btnEnergie:                                                   // Voor deze en de andere 3 buttons geldt hetzelfde
                h.putExtra("TYPE", "Energie");                                      // als bij Bio Mimicry
                startActivity(h);
                break;
            case R.id.btnWater:
                h.putExtra("TYPE", "Water");
                startActivity(h);
                break;
            case R.id.btnMaterialen:
                h.putExtra("TYPE", "Materiaal");
                startActivity(h);
                break;
            case R.id.btnDieren:
                h.putExtra("TYPE", "Dierenwelzijn");
                startActivity(h);
                break;

        }
    }
}
