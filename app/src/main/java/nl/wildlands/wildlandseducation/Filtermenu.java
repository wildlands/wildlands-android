package nl.wildlands.wildlandseducation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.*;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.lang.reflect.Type;


public class Filtermenu extends Activity implements View.OnClickListener {
    Button btnWater, btnEnergie, btnMaterialen, btnBio;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.gc();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filtermenu);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        btnWater = (Button)findViewById(R.id.btnWater);
        btnEnergie = (Button)findViewById(R.id.btnEnergie);
        btnMaterialen = (Button)findViewById(R.id.btnMaterialen);
        btnBio = (Button)findViewById(R.id.btnBio);
        ImageButton btnBack = (ImageButton)findViewById(R.id.backbutton);
        btnBack.setOnClickListener(this);
        TextView tv = (TextView)findViewById(R.id.themaKiezen);

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/thematext.ttf");
        tv.setTypeface(tf);

        btnWater.setOnClickListener(this);
        btnEnergie.setOnClickListener(this);
        btnMaterialen.setOnClickListener(this);
        btnBio.setOnClickListener(this);

        animateFadeIn();


    }

    public void animateFadeIn()
    {
        btnBio.setTranslationX(1000);
        btnEnergie.setTranslationX(-1000);
        btnWater.setTranslationX(1000);
        btnMaterialen.setTranslationX(-1000);
        btnBio.setVisibility(View.VISIBLE);
        btnEnergie.setVisibility(View.VISIBLE);
        btnWater.setVisibility(View.VISIBLE);
        btnMaterialen.setVisibility(View.VISIBLE);
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
    }




    @Override
    public void onClick(View v) {
        System.gc();
        Intent i = new Intent(this, Home.class);
        Intent h = new Intent(this, Kaart.class);
        switch(v.getId())
        {
            case R.id.backbutton:

                startActivity(i);
                this.finish();
                break;
            case R.id.btnBio:
                // make dis
                h.putExtra("TYPE", "bio");
                startActivity(h);
                break;
            case R.id.btnEnergie:
                h.putExtra("TYPE", "energie");
                startActivity(h);
                break;
            case R.id.btnWater:
                h.putExtra("TYPE", "water");
                startActivity(h);
                break;
            case R.id.btnMaterialen:
                h.putExtra("TYPE", "materialen");
                startActivity(h);
                break;
        }
    }
}
