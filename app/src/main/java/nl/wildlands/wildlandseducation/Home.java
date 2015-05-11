package nl.wildlands.wildlandseducation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;


public class Home extends Activity implements View.OnClickListener {
    private Button btnVerkenning, btnQuiz;
    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.gc();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        btnVerkenning = (Button)findViewById(R.id.verkenning);
        btnQuiz = (Button)findViewById(R.id.quiz);
        logo = (ImageView)findViewById(R.id.logo);
        btnVerkenning.setOnClickListener(this);
        btnQuiz.setOnClickListener(this);
        animateFadeIn();
    }

    public void animateFadeIn()
    {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(1500);
        btnVerkenning.setAnimation(fadeIn);
        btnQuiz.setAnimation(fadeIn);
        logo.setAnimation(fadeIn);
    }


    @Override
    public void onClick(View v) {
        System.gc();
        switch(v.getId())
        {
            case R.id.verkenning:
                Intent h = new Intent(this, Filtermenu.class);
                startActivity(h);
                this.finish();
                break;
            case R.id.quiz:
                Intent i = new Intent(this, view_4.class);
                startActivity(i);
                this.finish();
                break;
        }
    }
}
