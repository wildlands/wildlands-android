package nl.wildlands.wildlandseducation;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;


public class TestMap extends Activity {
    TouchImageView map, laag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_map);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,                // Fullscreen
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        map = (TouchImageView)findViewById(R.id.map);
        Drawable bg = getResources().getDrawable(R.drawable.bgverkenning);
        setRes(map, bg);
        Log.d("map", "is kapot");


    }

    @SuppressWarnings("deprecation")
    private void setRes(ImageView iv,Drawable drawable){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            iv.setBackground(drawable);
        else
            iv.setBackgroundDrawable(drawable);
    }


    public static Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp2, 0, 0, null);
        return bmOverlay;
    }

    public Bitmap combineImages(Bitmap c, Bitmap s) {
        Bitmap cs = null;

        int width, height = 0;

        if(c.getWidth() > s.getWidth()) {
            width = c.getWidth();
            height = c.getHeight();
        } else {
            width = s.getWidth();
            height = c.getHeight();
        }

        Log.d("fuckdit", String.valueOf(height));
        cs = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        Canvas comboImage = new Canvas(cs);
        comboImage.drawBitmap(c, new Matrix(), null);
        comboImage.drawBitmap(s, new Matrix(), null);

            return cs;
    }





}
