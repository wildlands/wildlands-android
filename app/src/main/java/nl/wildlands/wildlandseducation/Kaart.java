package nl.wildlands.wildlandseducation;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Kaart extends Activity implements OnClickListener {

    // Stenden data // TODO Change to Wildlands data!
    private final double gpsTop = 52.778749;
    private final double gpsLeft = 6.910379;
    private final double gpsBottom = 52.777664;
    private final double gpsRight = 6.913659;

    JSONParser jsonParser = new JSONParser();

    private JSONObject questionObj = null;
    private JSONArray questionArray;
    private JSONArray jsonArray;

   // private ImageButton pin1;
    private RelativeLayout rl;
    private GestureImageView map;
    ArrayList<HashMap<String, String>> mQuestionList;

    private static final String GET_PINPOINT_URL= "http://wildlands.doornbosagrait.tk/api/api.php?c=GetAllPinpoints";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_XPOS = "xPos";
    private static final String TAG_YPOS = "yPos";

    private ArrayList<Pinpoint> pinpoints;

    int buttonId = 0;

    private GPSTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

       // requestWindowFeature(Window.FEATURE_NO_TITLE);
       // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_kaart);
    //    pin1 = (ImageButton)findViewById(R.id.imageButton);
       // pin1.setOnClickListener(this);
        map = (GestureImageView)findViewById(R.id.imageView2);

        startGPSTracker();

       // map.addImageButton(pin1);
        pinpoints = new ArrayList<Pinpoint>();
        new Search().execute();
    }

    public void startPopUp()
    {
        Log.d("Popup", "started");
    }

    public void updateJSONdata() {
        map.addUnderbar();
       // map.addButton();
        mQuestionList = new ArrayList<HashMap<String, String>>();

        try {
            questionArray = jsonArray;
            Log.d("Error 1 gok ik", questionArray.toString());

            // looping through all posts according to the json object returned
            for (int i = 0; i < questionArray.length(); i++) {
                JSONObject c = questionArray.getJSONObject(i);
                Log.d("C object", c.toString());
                int id = c.getInt(TAG_ID);
                String name = c.getString(TAG_NAME);
                int xPos = c.getInt(TAG_XPOS);
                int yPos = c.getInt(TAG_YPOS);
                JSONObject typeJSON = c.getJSONObject("type");
                int typeID = typeJSON.getInt("id");
                String image = typeJSON.getString("image");
                String unit = typeJSON.getString("unit");
                String typeName = typeJSON.getString("name");
                PinpointType type = new PinpointType(typeID, image, unit, typeName);
                pinpoints.add(new Pinpoint(id,name,type,xPos, yPos));


                // creating new HashMap
                HashMap<String, String> map = new HashMap<String, String>();

                //map.put(TAG_ID, text);
                // adding HashList to ArrayList
                mQuestionList.add(map);

                // annndddd, our JSON data is up to date same with our array
                // list
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        displayPoints();
    }


    public void displayPoints()
    {
        for(Pinpoint pinpoint: pinpoints) {
            Log.d("Grootte", String.valueOf(pinpoints.size()));
            int x = pinpoint.getXPos();
            int y = pinpoint.getYPos();
            int id = pinpoint.getId();
            Log.d(String.valueOf(x), String.valueOf(y));
            String soort = pinpoint.getType().getName();
            Log.d("soort", soort);
            map.addButton(x, y, id, soort);
            buttonId++;
        }
        map.redraw();
//        pin1.setTranslationX(x);
//        pin1.setTranslationY(y);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    class Search extends AsyncTask<String, String, String> implements View.OnClickListener {

        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("search", ""));
            Log.d("request!", "starting");

            //Posting user data to script
            jsonArray = jsonParser.makeHttpRequest(
                    GET_PINPOINT_URL, "POST", params);
            Log.d("Json meuk", jsonArray.toString());

            return jsonArray.toString();

        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            updateJSONdata();


        }

        @Override
        public void onClick(View v) {

        }
    }
}
