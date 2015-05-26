package nl.wildlands.wildlandseducation.Activities;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import android.view.View.OnClickListener;

import android.widget.RelativeLayout;
import android.widget.Toast;


import java.util.ArrayList;

import nl.wildlands.wildlandseducation.GPS.GPSNotEnabledException;
import nl.wildlands.wildlandseducation.GPS.GPSTracker;
import nl.wildlands.wildlandseducation.MapAssets.GestureImageView;
import nl.wildlands.wildlandseducation.Pinpoint.Pinpoint;
import nl.wildlands.wildlandseducation.R;
import nl.wildlands.wildlandseducation.SQLite.PinpointsDataSource;


public class Kaart extends Activity implements OnClickListener {

    // Stenden data // TODO Change to Wildlands data!
    private final double gpsTop = 52.778749;
    private final double gpsLeft = 6.910379;
    private final double gpsBottom = 52.777664;
    private final double gpsRight = 6.913659;


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

        map.addUnderbar();
        displayPoints(thema);
    }

    public void startPopUp()
    {
        Log.d("Popup", "started");
    }


    public void displayPoints(String thema)
    {

        for(Pinpoint pinpoint: pinpoints) {

            Log.d("Grootte", String.valueOf(pinpoints.size()));
            int x = pinpoint.getXPos();
            int y = pinpoint.getYPos();
            long id = pinpoint.getId();
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
