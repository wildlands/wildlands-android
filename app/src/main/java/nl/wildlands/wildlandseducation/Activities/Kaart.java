package nl.wildlands.wildlandseducation.Activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import android.view.View.OnClickListener;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import nl.wildlands.wildlandseducation.GPS.GPSNotEnabledException;
import nl.wildlands.wildlandseducation.GPS.GPSTracker;
import nl.wildlands.wildlandseducation.LayerImage;
import nl.wildlands.wildlandseducation.MapAssets.GestureImageView;
import nl.wildlands.wildlandseducation.Pinpoint.Pinpoint;
import nl.wildlands.wildlandseducation.R;
import nl.wildlands.wildlandseducation.SQLite.LayerDataSource;
import nl.wildlands.wildlandseducation.SQLite.PinpointsDataSource;


public class Kaart extends Activity implements OnClickListener {

    // Stenden data // TODO Change to Wildlands data!
    private final double gpsTop = 52.778749;
    private final double gpsLeft = 6.910379;
    private final double gpsBottom = 52.777664;
    private final double gpsRight = 6.913659;

    private LayerDataSource layerDataSource;


   // private ImageButton pin1;
    private RelativeLayout rl;
    private GestureImageView map;

    private ArrayList<Pinpoint> pinpoints;

    private PinpointsDataSource pinpointsDataSource;

    int buttonId = 0;

    private GPSTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        pinpointsDataSource = new PinpointsDataSource(this.getApplicationContext());
        setContentView(R.layout.activity_kaart);
        map = (GestureImageView)findViewById(R.id.imageView2);

        layerDataSource = new LayerDataSource(this.getApplicationContext());

        startGPSTracker();

        pinpointsDataSource.open();
        pinpoints = pinpointsDataSource.getAllPinpoints();

        String thema = "";

        Bundle themaBundle =getIntent().getExtras();

        if(themaBundle!=null)
        {
            thema=themaBundle.getString("TYPE");
            Log.d("thema", thema);
        }

        int themaId = 0;
        if(thema.equals("Water"))
        {
          themaId = 3;
        }
        else if(thema.equals("Bio Mimicry"))
        {
            themaId = 1;
        }
        else if(thema.equals("Materiaal"))
        {
            themaId = 2;
        }
        else if(thema.equals("Energie"))
        {
            themaId = 4;
        }
        else {
            themaId = 5;
        }
        layerDataSource.open();
        for(LayerImage layerImage: layerDataSource.getAllLayerImages() )
        {
            if(layerImage.getThemaId() == themaId)
            {
                loadImageFromStorage(layerImage.getPath(),layerImage.getName());
                Log.d("wait", "this shit works?");
            }
        }



        displayPoints(thema);
    }

    public void startPopUp()
    {
        Log.d("Popup", "started");
    }

    /**
     * Load image from internal path
     * @param path
     */
    private void loadImageFromStorage(String path, String name)
    {
        try {
            File f=new File(path, name);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));

            Drawable bg = getResources().getDrawable(R.drawable.bgverkenning3);
            Drawable laag = new BitmapDrawable(b);
            Drawable [] lagen = {bg,laag};
            LayerDrawable ld = new LayerDrawable(lagen);
            map.setImageDrawable(ld);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }

    public void displayPoints(String thema)
    {

        for(Pinpoint pinpoint: pinpoints) {

            Log.d("Grootte", String.valueOf(pinpoints.size()));
            int x = pinpoint.getXPos();
            int y = pinpoint.getYPos();
            long id = pinpoint.getPinpointId();
            Log.d(String.valueOf(x), String.valueOf(y));
            String soort = pinpoint.getType();
            Log.d("soort", soort);
           if(soort.equals(thema)) {
                map.addButton(x, y, id, soort);
                buttonId++;
           }
        }
        map.redraw();
//        pin1.setTranslationX(x);
//        pin1.setTranslationY(y);

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //case R.id.imageButton:
             //   Intent h = new Intent(this, MainActivity.class);
              //  this.startActivity(h);

             //   break;

        }
    }

    private void startGPSTracker() {
        gpsTracker = new GPSTracker(this, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double x = ((location.getLongitude() - gpsLeft) / (gpsRight - gpsLeft)) * map.getWidth();
                double y = ((location.getLatitude() - gpsTop) / (gpsBottom - gpsTop)) * map.getHeight();

                // TODO Change position of 'spot' image.
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });

        try {
            gpsTracker.startTracking();
        } catch (GPSNotEnabledException e) {
            Toast.makeText(this, "GPS/Wifi Location not enabled.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }


}
