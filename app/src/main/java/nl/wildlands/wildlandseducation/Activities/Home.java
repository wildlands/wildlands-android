package nl.wildlands.wildlandseducation.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
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

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nl.wildlands.wildlandseducation.GlobalSettings.DefaultApplication;
import nl.wildlands.wildlandseducation.GlobalSettings.Values;
import nl.wildlands.wildlandseducation.JSONParser;
import nl.wildlands.wildlandseducation.Layer;
import nl.wildlands.wildlandseducation.Level;
import nl.wildlands.wildlandseducation.Pinpoint.Page;
import nl.wildlands.wildlandseducation.Pinpoint.Pinpoint;

import nl.wildlands.wildlandseducation.R;
import nl.wildlands.wildlandseducation.SQLite.LayerDataSource;
import nl.wildlands.wildlandseducation.SQLite.LevelDataSource;
import nl.wildlands.wildlandseducation.SQLite.PinpointsDataSource;
import nl.wildlands.wildlandseducation.quiz.Answer;
import nl.wildlands.wildlandseducation.quiz.Question;
import nl.wildlands.wildlandseducation.SQLite.QuestionsDataSource;


public class Home extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private Button btnVerkenning, btnQuiz, btnCredits;                  // De 3 buttons van het home menu

    private ProgressBar spinner;                                        // Loader
    private TextView loadingTxt;                                        // "Inhoud laden" tekst
    private ImageView logo;                                             // Eco app logo

    public static final String MyPREFERENCES = "MyPrefs" ;              // String to get sharedprefs

    private SharedPreferences sharedpreferences;
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

    private static final String GET_LEVELS_URL = Values.BASE_URL + Values.GET_LEVELS;
    private static final String TAG_LEVEL_ID = "id";
    private static final String TAG_LEVEL_NAME = "name";

    private static final String GET_LAYERS_URL = Values.BASE_URL + Values.GET_LAYERS;
    private static final String TAG_THEMA_TYPE = "type";
    private static final String TAG_THEMA_ID = "id";
    private static final String TAG_IMAGE_ID = "image";

    private ArrayList<Question> questions;                              // Nieuwe arraylist met vraagobjecten

    private Context context = this;

    // SQLite Datasources voor de verschillende objecten
    private QuestionsDataSource datasource;
    private PinpointsDataSource pinpointsDataSource;
    private LayerDataSource layerDataSource;
    private LevelDataSource levelDataSource;

    // Arrays voor het verwerken van de json
    private JSONArray questionArray, pinpointArray;
    private JSONArray jsonArray, jsonArrayPinpoint, jsonArrayLevels, jsonArrayLayers;

    // JSONParser voor ophalen van data
    private JSONParser jsonParser = new JSONParser();


    private ArrayList<HashMap<String, String>> mQuestionList;

    private Spinner levels;                                             // Niveau keuze
    private Button gaverder;                                            // Knop na niveau keuze

    int selectedLevel;                                                  // Geselecteerde niveau

    private boolean pinpointsSaved;                                     // Check of pinpoints zijn opgeslagen

    private static final int MSDELAY = 2000;                            // Aantal ms voor het doorschakelen naar contentweergave
    private static final int DISPLAY_DATA = 1;                          // Checkwaarde voor Handler
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == DISPLAY_DATA)
            {
                checkNetwork();
            }
        }
    };

    private Typeface tf;                                                // Typeface voor textviews/buttons

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.gc();                                                    // Onnodige data verwijderen
        super.onCreate(savedInstanceState);                             // Zet layout
        setContentView(R.layout.home);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,// Fullscreen
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Maak nieuwe datasources voor alle objecten
        datasource = new QuestionsDataSource(context);
        pinpointsDataSource = new PinpointsDataSource(context);
        layerDataSource = new LayerDataSource(context);
        levelDataSource = new LevelDataSource(context);


        // Haal de buttons + logo van de layout
        btnVerkenning = (Button)findViewById(R.id.verkenning);
        btnQuiz = (Button)findViewById(R.id.quiz);
        btnCredits = (Button)findViewById(R.id.credits);
        logo = (ImageView)findViewById(R.id.logo);
        btnVerkenning.setOnClickListener(this);
        btnQuiz.setOnClickListener(this);
        btnCredits.setOnClickListener(this);

        tf = DefaultApplication.tf;                                         // Typeface uit DefaultApplication

        // Verander lettertypes
        btnQuiz.setTypeface(tf);
        btnVerkenning.setTypeface(tf);
        btnCredits.setTypeface(tf);

        pinpointsSaved = false;                                            // Nog niet opgeslagen

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE); // Haal sharedprefs op

        spinner = (ProgressBar)findViewById(R.id.progressBar1);            // Laadspinner uit layout
        loadingTxt = (TextView)findViewById(R.id.loading);                 // Laadtekst uit layout
        loadingTxt.setTypeface(tf);                                        // Lettertype aanpassen

        questions = new ArrayList<Question>();                             // Maak een lege arraylist aan

        levels = (Spinner)findViewById(R.id.levels_spinner);                // Niveau selectie uit layout

        gaverder = (Button)findViewById(R.id.gaverder);                     // Ga verder knop uit layout
        gaverder.setOnClickListener(this);                                  // Activeer click listener
        gaverder.setTypeface(tf);                                           // Verander lettertype

        // Check of homescreen al afgerond is
        if(((DefaultApplication)this.getApplication()).isHomeFinished())
        {
            logo.setVisibility(View.VISIBLE);                               // Showlogo
            btnVerkenning.setVisibility(View.VISIBLE);                      // Showbuttons
            btnQuiz.setVisibility(View.VISIBLE);
            btnCredits.setVisibility(View.VISIBLE);
            animateFadeIn();                                                // Fade alles in
        }
        else
        {
            spinner.setVisibility(View.VISIBLE);                            // Laad laadspinner
            loadingTxt.setVisibility(View.VISIBLE);                         // en tekst zien

            if (isNetworkAvailable())
            {                                                               // Als er netwerk is
                new CheckVersion().execute();                               // Start met het checken van de versie
            }
            else
            {
                makeToast("GEEN INTERNETVERBINDING");                       // Geef aan dat er geen verbinding is
                mHandler.sendEmptyMessageDelayed(DISPLAY_DATA, MSDELAY);    // Check elke 2 sec de verbinding
            }
        }
    }

    /**
     * Check of er beschikking is over een netwerk
     */
    private void checkNetwork()
    {
        if(isNetworkAvailable())                                            // Als er een verbinding is
        {
            new CheckVersion().execute();                                   // Start met het checken van de versie
        }
        else
        {
            makeToast("GEEN INTERNETVERBINDING");                           // Geef aan dat er geen verbinding is
            mHandler.sendEmptyMessageDelayed(DISPLAY_DATA, MSDELAY);        // Start na 2 sec weer opnieuw
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

    /**
     * Vil de niveaus
     */
    public void fillSpinner()
    {
        levelDataSource.open();                                                     // Open datasource
        ArrayList<String> spinnerArray = new ArrayList<String>();
        spinnerArray.add("SELECTEER NIVEAU");                                       // Bovenste niveau
        ArrayList<Level> levelObjects = levelDataSource.getAllLevels();
        for(Level level: levelObjects)
        {
            spinnerArray.add(level.getName().toUpperCase());                                      // Voeg alle niveaus toe
        }

        MySpinnerAdapter spinnerArrayAdapter = new MySpinnerAdapter(this, R.layout.spinner_dropdown_tv, spinnerArray); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Spinner vullen met items
        levels.setAdapter(spinnerArrayAdapter);
        levels.setOnItemSelectedListener(this);

        // Verander de zichtbaarheid
        spinner.setVisibility(View.INVISIBLE);
        loadingTxt.setVisibility(View.INVISIBLE);
        gaverder.setVisibility(View.VISIBLE);
        levels.setVisibility(View.VISIBLE);
        logo.setVisibility(View.VISIBLE);
    }


    /**
     * Animeer de buttons
     * Verander de alpha van 0 naar 1 in 1500
     */
    public void animateFadeIn()
    {
        Animation fadeIn = new AlphaAnimation(0, 1);                            // AlphaAnimatie
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(1500);                                               // Duratie

        // Buttons en logo infaden
        btnVerkenning.setAnimation(fadeIn);
        btnQuiz.setAnimation(fadeIn);
        btnCredits.setAnimation(fadeIn);
        logo.setAnimation(fadeIn);
    }

    /**
     * Update layers
     */
    public void updateLayerdata()
    {
        layerDataSource.open();
        try{
            JSONArray layerArray = jsonArrayLayers;                             // Json die opgehaald is
            for(int i = 0; i < layerArray.length(); i++)
            {
                JSONObject c = layerArray.getJSONObject(i);                     // Elk object uit de array
                JSONObject type = c.getJSONObject(TAG_THEMA_TYPE);              // type van het object
                int typeID = type.getInt(TAG_THEMA_ID);                         // typeId van het object
                String image = c.getString(TAG_IMAGE_ID);                       // imageString voor het ophalen van de image
                layerDataSource.createLayer(typeID,image);                      // Voeg layer toe aan sqlite
            }
        }
        catch (Exception e)
        {

        }
        layerDataSource.close();                                                // Sluit de datasoruce
        new LayerImageLoader().execute();                                       // Start de afbeeldinglader
    }

    /**
     * Update level
     */
    public void updateLeveldata(){
        levelDataSource.open();                                                 // Open datasource
        try
        {
            JSONArray levelArray = jsonArrayLevels;                             // Opgehaalde json in array

            for (int i = 0; i < levelArray.length(); i++)
            {
                JSONObject c = levelArray.getJSONObject(i);                     // Haal het object uit de array
                String level = c.getString(TAG_NAME);                           // Level naam
                int levelid = c.getInt(TAG_ID);                                 // Level id

                if(i == 0)
                {
                    selectedLevel = levelid;
                }
                levelDataSource.createLevel(levelid, level);                    // Voeg level toe aan SQLite

            }

           fillSpinner();                                                       // Vul de selectie met items

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Start de popup met een waarschuwing.
     */
    public void startAlertdialog()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        View resultsView = LayoutInflater.from(getBaseContext()).inflate(R.layout.custom_alert,     // Gebruik custom layout
                null);
        TextView title = (TextView)resultsView.findViewById(R.id.titleAlert);
        resultsView.setBackgroundResource(R.drawable.alert_niveau);
        title.setText("NIVEAU");                                                                    // Verander tekst
        title.setTypeface(tf);                                                                      // Verander lettertype

        alertDialogBuilder
                .setView(resultsView)
                .setCancelable(false)
        ;

        final AlertDialog alertDialog = alertDialogBuilder.create();                                // Maak de alertdialog

        TextView tv = (TextView)resultsView.findViewById(R.id.alertTextDialog);
        Button dismiss = (Button)resultsView.findViewById(R.id.alertBtn);

        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        tv.setText("Je moet een niveau selecteren");

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");     // Haal lettertype op
        TextView alertImage = (TextView)resultsView.findViewById(R.id.alertImage);                  // Textview voor het plaatje
        alertImage.setText(getString(R.string.user));                                                                     // FontAwesome string
        alertImage.setTypeface(font);                                                               // Verander lettertype

        alertDialog.show();                                                                         // Laat alertdialog zien
    }

    /**
     * Update pinpoint
     */
        public void updatePinpointdata()
        {
        mQuestionList = new ArrayList<HashMap<String, String>>();

        try
        {
            pinpointArray = jsonArrayPinpoint;                                          // JSON Array met data

            for (int i = 0; i < pinpointArray.length(); i++)
            {
                JSONObject c = pinpointArray.getJSONObject(i);                          // JSONObject uit array

                // Waardes uit objects
                int id = c.getInt(TAG_ID);
                String name = c.getString(TAG_NAME);
                String description = c.getString(TAG_DESCRIPTION);
                int xPos = c.getInt(TAG_XPOS);
                int yPos = c.getInt(TAG_YPOS);

                // Waardes van type
                JSONObject typeJSON = c.getJSONObject("type");
                String typeName = typeJSON.getString("name");

                pinpointsDataSource.open();
                JSONArray pages = c.getJSONArray("pages");
                for(int j = 0; j< pages.length();j++)
                {
                    JSONObject onePage = pages.getJSONObject(j);                    // Page object van pinpoint
                    JSONObject level = onePage.getJSONObject("level");              // Level object
                    int levelId = level.getInt("id");                               // LevelID
                    String title = onePage.getString("title");                      // Level title
                    String pageImage = onePage.getString("image");                  // Level image
                    String text = onePage.getString("text");                        // Level text

                    pinpointsDataSource.createPage(id, levelId, title, pageImage, text); // Page aanmaken
                }

                pinpointsDataSource.createPinpoint(id,name, description, typeName, xPos, yPos); // Pinpoint aanmaken
                pinpointsDataSource.close();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        new PageImageLoader().execute();                                            // Image Loader starten

        if(pinpointsSaved)
        {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean("questionsadded", true);
            editor.commit();
        }
        else
        {
            pinpointsSaved = true;
        }
    }

    /**
     * Update de vragen
     * @param failure
     */
    public void updateJSONdata(boolean failure){

        if(!failure) {
            mQuestionList = new ArrayList<HashMap<String, String>>();
            ((DefaultApplication) this.getApplication()).setQuestionsLoaded(true);
            try {
                questionArray = jsonArray;
                String baseUrl = Values.BASE_URL + Values.IMAGE_BASE;

                // Alle questions toevoegen
                for (int i = 0; i < questionArray.length(); i++) {
                    JSONObject c = questionArray.getJSONObject(i);
                    String bitImage = c.getString(TAG_IMAGE);
                    JSONObject level = c.getJSONObject(TAG_LEVEL);
                    String levelnaam = level.getString("name");
                    int levelid = level.getInt("id");
                    JSONObject typeObj = c.getJSONObject("type");
                    String type = typeObj.getString("name");

                    datasource.open();
                    Question addedQuestion = datasource.createQuestion(c.getString(TAG_TEXT), bitImage, levelid, type); // Voeg toe aan SQLite

                    JSONArray a = c.getJSONArray(TAG_ANSWERS);                                      // Antwoorden array
                    for (int j = 0; j < a.length(); j++)
                    {
                        JSONObject ans = a.getJSONObject(j);                                        // Antwoord
                        String answer = ans.getString(TAG_TEXT);                                    // antwoordtekst
                        boolean good = ans.getBoolean(TAG_RIGHTWRONG);                              // goed of fout
                        Answer newAnswer = new Answer(1, addedQuestion.getId(), answer, good);      // Voeg toe
                        datasource.createAnswer(newAnswer);
                     }

                    // creating new HashMap
                    HashMap<String, String> map = new HashMap<String, String>();

                    mQuestionList.add(map);
                }

                new ImageLoader().execute();                                                        // Image Loader aanroepen

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (pinpointsSaved)
            {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean("questionsadded", true);
                editor.commit();
            }
            else
            {
                pinpointsSaved = true;
            }

        }
        else
        {
            if (pinpointsSaved)
            {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean("questionsadded", true);
                editor.commit();

                // Verander zichtbaarheid
                spinner.setVisibility(View.INVISIBLE);
                loadingTxt.setVisibility(View.INVISIBLE);

                gaverder.setVisibility(View.VISIBLE);
                levels.setVisibility(View.VISIBLE);
            }
            else
            {
                pinpointsSaved = true;
            }
        }

    }

    private String saveToInternalStorage(Bitmap bitmapImage, String filename){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        Log.d("dir", directory.toString());
        // Create imageDir
        File mypath=new File(directory,filename);
        Log.d("path", mypath.toString());

        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(mypath);

            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("ingesteld path", directory.getAbsolutePath());
        return directory.getAbsolutePath();
    }


    @Override
    public void onClick(View v) {
        System.gc();
        switch(v.getId())
        {
            case R.id.verkenning:
                Intent h = new Intent(this, Filtermenu.class);
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
                ((DefaultApplication)this.getApplication()).setHomeFinished(true);
                this.finish();
                break;
            case R.id.gaverder:
                if(selectedLevel >= 1) {
                    ((DefaultApplication) this.getApplication()).setLevel(selectedLevel);
                    gaverder.setVisibility(View.INVISIBLE);
                    levels.setVisibility(View.INVISIBLE);
                    logo.setVisibility(View.VISIBLE);
                    btnVerkenning.setVisibility(View.VISIBLE);
                    btnQuiz.setVisibility(View.VISIBLE);
                    btnCredits.setVisibility(View.VISIBLE);
                    animateFadeIn();
                }
                else{
                    startAlertdialog();
                }

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
        selectedLevel = position;
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
            try {
                if(jsonArray.getJSONObject(0).getString("error").equals("haha"))
                {
                    failure = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return jsonArray.toString();
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            updateJSONdata(failure);

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

    class GetLayers extends AsyncTask<String, String, String>  {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args) {
            jsonArrayLayers = jsonParser.getJSONFromUrl(GET_LAYERS_URL);
            return jsonArrayLayers.toString();
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            updateLayerdata();
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
        JSONObject versionObj = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args) {
            versionObj = jsonParser.getJSONObjFromUrl(Values.BASE_URL + Values.GET_CHECKSUM);
            if(versionObj == null)
            {
                logo.setVisibility(View.VISIBLE);
                btnVerkenning.setVisibility(View.VISIBLE);
                btnQuiz.setVisibility(View.VISIBLE);
                btnCredits.setVisibility(View.VISIBLE);
                animateFadeIn();
                return "null";
            }
            else {
                Log.d("Versionjson", versionObj.toString());
                try {

                    appVersion = versionObj.getLong(TAG_CHECKSUM);
                    Log.d("version", String.valueOf(appVersion));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return versionObj.toString();
            }

        }

        protected void onPostExecute(String file_url) {
            if (versionObj != null) {
                if (appVersion == sharedpreferences.getLong("version", 0)) {
                    Log.d("versie", "Versies komen overeen");
                    fillSpinner();

                } else {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putLong("version", appVersion);
                    editor.commit();
                    Log.d("Check version", String.valueOf(appVersion));
                    new GetLevels().execute();
                    new Search().execute();
                    new PinpointSaver().execute();
                    new GetLayers().execute();
                }
            }
        }
    }



    /**
     * Class to load the images from the right urls
     */
    class ImageLoader extends  AsyncTask<String, String, String> implements View.OnClickListener {
        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args) {
            /**
             * Get the image for each question
             */
            for(Question question: datasource.getAllQuestions()){
                if(!question.getImage().equals("question/biodiesel.png")) {
                    String urlString = question.getImage();
                    Log.d("id", String.valueOf(question.getId()));
                    Log.d("urlstring", urlString);
                    Bitmap bitmap = null;
                    try {
                        URL url = new URL(urlString);
                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                    } catch (Exception e) {
                        Log.d(e.toString(), e.toString());
                    }
                    ;
                    String name = String.valueOf(question.getId()) + ".png";
                    String path = saveToInternalStorage(bitmap, name);
                    question.setImagePath(path);
                    datasource.createImage(path,name,question.getId());
                    Log.d("path", path);
                }
            }


            return "ja";
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted


        }

        @Override
        public void onClick(View v) {

        }
    }

    /**
     * Class to load the images from the right urls
     */
    class LayerImageLoader extends  AsyncTask<String, String, String>  {
        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args) {
            /**
             * Get the image for each question
             */
            layerDataSource.open();
            for(Layer layer: layerDataSource.getAllLayers()){

                    String urlString = layer.getImage();
                    Log.d("id", String.valueOf(layer.getThemaId()));
                    Log.d("urlstring", urlString);
                    Bitmap bitmap = null;
                    try {
                        URL url = new URL(urlString);
                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                    } catch (Exception e) {
                        Log.d(e.toString(), e.toString());
                    }
                    ;
                    String name = String.valueOf(layer.getThemaId()) + "layer.png";
                    String path = saveToInternalStorage(bitmap, name);
                    //question.setImagePath(path);
                    layerDataSource.createLayerImage(layer.getThemaId(), path, name);
                    //datasource.createImage(path,name,question.getId());
                    Log.d("path", path);

            }


            return "ja";
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted

        }

    }

    /**
     * Class to load the images from the right urls
     */
    class PageImageLoader extends  AsyncTask<String, String, String>  {
        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args) {
            /**
             * Get the image for each question
             */
            pinpointsDataSource.open();
            for(Page page: pinpointsDataSource.getAllPages()){

                String urlString = page.getImage();
                long id = page.getId();
                Log.d("urlstring", urlString);
                Bitmap bitmap = null;
                try {
                    URL url = new URL(urlString);
                    bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                } catch (Exception e) {
                    Log.d(e.toString(), e.toString());
                }
                ;
                String name = String.valueOf(id) + "page.png";
                String path = saveToInternalStorage(bitmap, name);

                pinpointsDataSource.createPageImage(id,path,name);
              //  layerDataSource.createLayerImage(layer.getThemaId(), path, name);

                Log.d("path", path);

            }


            return "ja";
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted

        }

    }

    private static class MySpinnerAdapter extends ArrayAdapter<String> {
        // Initialise custom font, for example:
        Typeface font = DefaultApplication.tf;

        // (In reality I used a manager which caches the Typeface objects)
        // Typeface font = FontManager.getInstance().getFont(getContext(), BLAMBOT);

        private MySpinnerAdapter(Context context, int resource, List<String> items) {
            super(context, resource, items);
        }

        // Affects default (closed) state of the spinner
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);
            view.setTypeface(font);
            return view;
        }

        // Affects opened state of the spinner
        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getDropDownView(position, convertView, parent);
            view.setTypeface(font);
            return view;
        }
    }
}
