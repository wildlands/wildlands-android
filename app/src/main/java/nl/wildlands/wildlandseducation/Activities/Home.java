package nl.wildlands.wildlandseducation.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nl.wildlands.wildlandseducation.GlobalSettings.DefaultApplication;
import nl.wildlands.wildlandseducation.GlobalSettings.Values;
import nl.wildlands.wildlandseducation.JSONParser;
import nl.wildlands.wildlandseducation.Pinpoint.Pinpoint;
import nl.wildlands.wildlandseducation.Pinpoint.PinpointType;
import nl.wildlands.wildlandseducation.R;
import nl.wildlands.wildlandseducation.SQLite.PinpointsDataSource;
import nl.wildlands.wildlandseducation.quiz.Answer;
import nl.wildlands.wildlandseducation.quiz.Question;
import nl.wildlands.wildlandseducation.SQLite.QuestionsDataSource;


public class Home extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private Button btnVerkenning, btnQuiz, btnCredits;

    private ProgressBar spinner;
    private TextView loadingTxt;
    private ImageView logo;

    public static final String MyPREFERENCES = "MyPrefs" ;              // String to get sharedprefs

    SharedPreferences sharedpreferences;
    // Url to get JSON
    private static final String GET_QUESTION_URL = Values.BASE_URL + Values.GET_QUESTIONS;

    /*
    Tags om variabelen uit JSON te halen
     */
    private static final String TAG_ID = "id";
    private static final String TAG_PINPOINTID = "pinpointId";
    private static final String TAG_TEXT = "text";
    private static final String TAG_IMAGE = "image";
    private static final String TAG_ANSWERS = "answers";
    private static final String TAG_RIGHTWRONG = "rightWrong";
    private static final String TAG_LEVEL = "level";
    private static final String TAG_CHECKSUM = "checksum";

    private static final String GET_PINPOINT_URL= Values.BASE_URL + Values.GET_PINPOINTS;
    private static final String TAG_NAME = "name";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_XPOS = "xPos";
    private static final String TAG_YPOS = "yPos";

    private ArrayList<Question> questions;

    private QuestionsDataSource datasource;
    private PinpointsDataSource pinpointsDataSource;

    private JSONArray questionArray, pinpointArray;
    private JSONArray jsonArray, jsonArrayPinpoint, jsonArrayLevels;

    // JSONParser voor ophalen van data
    JSONParser jsonParser = new JSONParser();

    ArrayList<HashMap<String, String>> mQuestionList;

    Spinner levels;
    Button gaverder;
    int selectedLevel;
    private static final String GET_LEVELS_URL = Values.BASE_URL + Values.GET_LEVELS;
    private static final String TAG_LEVEL_ID = "id";
    private static final String TAG_LEVEL_NAME = "name";


    private boolean pinpointsSaved;

    private static final int MSDELAY = 3000;            // Aantal ms voor het doorschakelen naar contentweergave
    private static final int DISPLAY_DATA = 1;          // Checkwaarde voor Handler
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == DISPLAY_DATA)
            {
                checkNetwork();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.gc();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        datasource = new QuestionsDataSource(this.getApplicationContext());
        pinpointsDataSource = new PinpointsDataSource(this.getApplicationContext());

        btnVerkenning = (Button)findViewById(R.id.verkenning);
        btnQuiz = (Button)findViewById(R.id.quiz);
        btnCredits = (Button)findViewById(R.id.credits);
        logo = (ImageView)findViewById(R.id.logo);
        btnVerkenning.setOnClickListener(this);
        btnQuiz.setOnClickListener(this);
        btnCredits.setOnClickListener(this);
        questionArray = new JSONArray();
        jsonArray = new JSONArray();
        pinpointsSaved = false;

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        loadingTxt = (TextView)findViewById(R.id.loading);
        questions = new ArrayList<Question>();


        levels = (Spinner)findViewById(R.id.levels_spinner);

        gaverder = (Button)findViewById(R.id.gaverder);
        gaverder.setOnClickListener(this);

        if(((DefaultApplication)this.getApplication()).isHomeFinished())
        {
            logo.setVisibility(View.VISIBLE);
            btnVerkenning.setVisibility(View.VISIBLE);
            btnQuiz.setVisibility(View.VISIBLE);
            animateFadeIn();
        }
        else {


            if (isNetworkAvailable()) {
                spinner.setVisibility(View.VISIBLE);
                loadingTxt.setVisibility(View.VISIBLE);
                new CheckVersion().execute();
            } else {
                spinner.setVisibility(View.VISIBLE);
                loadingTxt.setVisibility(View.VISIBLE);
                makeToast("GEEN INTERNETVERBINDING");
                mHandler.sendEmptyMessageDelayed(DISPLAY_DATA, MSDELAY);
            }
        }


    }

    private void checkNetwork()
    {
        if(isNetworkAvailable()) {
            new CheckVersion().execute();
        }
        else{
            makeToast("GEEN INTERNETVERBINDING");
            mHandler.sendEmptyMessageDelayed(DISPLAY_DATA, MSDELAY);
        }
    }
    /**
     * Maakt een nieuwe toast aan met een customlayout
     * En laat deze zien
     * @param message
     */
    public void makeToast(String message)
    {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) findViewById(R.id.toast_layout_root));                  // Gebruik de custom_toast layout

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);                                                      // Zet de tekst aan de hand van de parameter
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);                                      // Zet korte duratie
        toast.setView(layout);
        toast.show();                                                               // Laat de toast zien
    }



    public void animateFadeIn()
    {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(1500);
        btnVerkenning.setAnimation(fadeIn);
        btnQuiz.setAnimation(fadeIn);
        btnCredits.setAnimation(fadeIn);
        logo.setAnimation(fadeIn);

    }

    public void updateLeveldata(){
        try {
            JSONArray levelArray = jsonArrayLevels;
            ArrayList<String> spinnerArray = new ArrayList<String>();

            for (int i = 0; i < levelArray.length(); i++)
            {
                JSONObject c = levelArray.getJSONObject(i);
                String level = c.getString(TAG_NAME);
                int levelid = c.getInt(TAG_ID);
                String content = level.toUpperCase();
                if(i == 0)
                {
                    selectedLevel = levelid;
                }
                spinnerArray.add(content);
            }

            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_dropdown_tv, spinnerArray); //selected item will look like a spinner set from XML
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            levels.setAdapter(spinnerArrayAdapter);
            levels.setOnItemSelectedListener(this);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        spinner.setVisibility(View.INVISIBLE);
        loadingTxt.setVisibility(View.INVISIBLE);
        gaverder.setVisibility(View.VISIBLE);
        levels.setVisibility(View.VISIBLE);

    }
    public void updatePinpointdata() {


        mQuestionList = new ArrayList<HashMap<String, String>>();

        try {
            pinpointArray = jsonArrayPinpoint;

            // looping through all posts according to the json object returned
            for (int i = 0; i < pinpointArray.length(); i++) {
                JSONObject c = pinpointArray.getJSONObject(i);
                Log.d("C object", c.toString());
                int id = c.getInt(TAG_ID);
                String name = c.getString(TAG_NAME);
                String description = c.getString(TAG_DESCRIPTION);
                int xPos = c.getInt(TAG_XPOS);
                int yPos = c.getInt(TAG_YPOS);
                JSONObject typeJSON = c.getJSONObject("type");
                int typeID = typeJSON.getInt("id");
                String image = typeJSON.getString("image");
                String unit = typeJSON.getString("unit");
                String typeName = typeJSON.getString("name");
                PinpointType type = new PinpointType(typeID, image, unit, typeName);

                pinpointsDataSource.open();
                JSONArray pages = c.getJSONArray("pages");
                for(int j = 0; j< pages.length();j++)
                {
                    JSONObject onePage = pages.getJSONObject(j);
                    JSONObject level = onePage.getJSONObject("level");
                    int levelId = level.getInt("id");
                    String title = onePage.getString("title");
                    String pageImage = onePage.getString("image");
                    String text = onePage.getString("text");

                    pinpointsDataSource.createPage(id, levelId, title, pageImage, text);
                }



                Pinpoint addedPinpoint = pinpointsDataSource.createPinpoint(name, description, typeName, xPos, yPos);
                pinpointsDataSource.close();

                Log.d("pin", addedPinpoint.getDescription());
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(pinpointsSaved) {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean("questionsadded", true);
            editor.commit();

            spinner.setVisibility(View.INVISIBLE);
            loadingTxt.setVisibility(View.INVISIBLE);
            /*
            logo.setVisibility(View.VISIBLE);
            btnVerkenning.setVisibility(View.VISIBLE);
            btnQuiz.setVisibility(View.VISIBLE);
            animateFadeIn();
            */
            gaverder.setVisibility(View.VISIBLE);
            levels.setVisibility(View.VISIBLE);
        }
        else{
            pinpointsSaved = true;
        }
    }

    public void updateJSONdata(){

        mQuestionList = new ArrayList<HashMap<String, String>>();
        ((DefaultApplication)this.getApplication()).setQuestionsLoaded(true);
        try {
            questionArray = jsonArray;
            String baseUrl = Values.BASE_URL + Values.IMAGE_BASE;

            // looping through all posts according to the json object returned
            for (int i = 0; i < questionArray.length(); i++) {
                JSONObject c = questionArray.getJSONObject(i);
                String bitImage = c.getString(TAG_IMAGE);
                JSONObject level = c.getJSONObject(TAG_LEVEL);
                String levelnaam = level.getString("name");
                int levelid = level.getInt("id");
                JSONObject typeObj = c.getJSONObject("type");
                String type = typeObj.getString("name");
                Log.d("typenaam", type);
                Log.d("url", bitImage);
                String urlString = baseUrl + bitImage;
                Log.d("urlstring", urlString);

                datasource.open();
                Question addedQuestion = datasource.createQuestion(c.getString(TAG_TEXT), urlString, levelid, type);



                JSONArray a = c.getJSONArray(TAG_ANSWERS);
                for(int j = 0; j < a.length(); j++){
                    JSONObject ans = a.getJSONObject(j);
                    String answer = ans.getString(TAG_TEXT);
                    boolean good = ans.getBoolean(TAG_RIGHTWRONG);
                    Answer newAnswer = new Answer(1, addedQuestion.getId(), answer,good);
                    Answer wut = datasource.createAnswer(newAnswer);
                    Log.d("wut", wut.getAnswer());


                }
                // creating new HashMap
                HashMap<String, String> map = new HashMap<String, String>();

                datasource.close();
                //map.put(TAG_ID, text);
                // adding HashList to ArrayList
                mQuestionList.add(map);

                // annndddd, our JSON data is up to date same with our array
                // list
            }
           // Log.d("Goed vraag 1", questions.get(0).getCorrectAnswer());

        } catch (JSONException e) {
            e.printStackTrace();
        }


        if(pinpointsSaved) {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean("questionsadded", true);
            editor.commit();
            spinner.setVisibility(View.INVISIBLE);
            loadingTxt.setVisibility(View.INVISIBLE);
            gaverder.setVisibility(View.VISIBLE);
            levels.setVisibility(View.VISIBLE);
            /*
            logo.setVisibility(View.VISIBLE);
            btnVerkenning.setVisibility(View.VISIBLE);
            btnQuiz.setVisibility(View.VISIBLE);
            animateFadeIn();
            */


        }
        else{
            pinpointsSaved = true;
        }



    }


    @Override
    public void onClick(View v) {
        System.gc();
        switch(v.getId())
        {
            case R.id.verkenning:
                Intent h = new Intent(this, Credits.class);
                startActivity(h);
                ((DefaultApplication)this.getApplication()).setHomeFinished(true);
                this.finish();
                break;
            case R.id.quiz:
                Intent i = new Intent(this, ChooseQuizGroup.class);
                startActivity(i);
                ((DefaultApplication)this.getApplication()).setHomeFinished(true);
                this.finish();
                break;
            case R.id.credits:
                Intent k = new Intent(this, Credits.class);
                startActivity(k);
                this.finish();
                break;
            case R.id.gaverder:
                ((DefaultApplication)this.getApplication()).setLevel(selectedLevel);
                gaverder.setVisibility(View.INVISIBLE);
                levels.setVisibility(View.INVISIBLE);
                logo.setVisibility(View.VISIBLE);
                btnVerkenning.setVisibility(View.VISIBLE);
                btnQuiz.setVisibility(View.VISIBLE);
                btnCredits.setVisibility(View.VISIBLE);
                animateFadeIn();

                break;
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedLevel = position+1;
        Log.d("level", String.valueOf(selectedLevel));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
            //Posting user data to script
            jsonArray = jsonParser.makeHttpRequest(
                    GET_QUESTION_URL, "POST", params);
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

    class PinpointSaver extends AsyncTask<String, String, String> implements View.OnClickListener {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args) {
         jsonArrayPinpoint = jsonParser.getJSONFromUrl(GET_PINPOINT_URL);
            return jsonArrayPinpoint.toString();
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            updatePinpointdata();
        }

        @Override
        public void onClick(View v) {

        }
    }

    class GetLevels extends AsyncTask<String, String, String>  {

        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args) {
            //Posting user data to script
            jsonArrayLevels = jsonParser.getJSONFromUrl(GET_LEVELS_URL);
            return jsonArrayLevels.toString();
        }

        protected void onPostExecute(String file_url) {
            updateLeveldata();
        }

    }

    class CheckVersion extends AsyncTask<String, String, String> {
        long appVersion;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args) {
            JSONObject versionObj = jsonParser.getJSONObjFromUrl(Values.BASE_URL + Values.GET_CHECKSUM);
            Log.d("Versionjson", versionObj.toString());
            try {

                appVersion = versionObj.getLong(TAG_CHECKSUM);
                Log.d("version", String.valueOf(appVersion));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return versionObj.toString();

        }

        protected void onPostExecute(String file_url) {
            if(appVersion == sharedpreferences.getLong("version", 0))
            {
                Log.d("versie", "Versies komen overeen");
                //logo.setVisibility(View.VISIBLE);
               // btnVerkenning.setVisibility(View.VISIBLE);

                //btnQuiz.setVisibility(View.VISIBLE);


              //  animateFadeIn();
                new GetLevels().execute();
                gaverder.setVisibility(View.VISIBLE);
                levels.setVisibility(View.VISIBLE);

            }
            else {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putLong("version", appVersion);
                editor.commit();
                Log.d("Check version", String.valueOf(appVersion));
                new Search().execute();
                new PinpointSaver().execute();
            }
        }
    }
}
