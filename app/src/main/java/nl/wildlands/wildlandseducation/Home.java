package nl.wildlands.wildlandseducation;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Home extends Activity implements View.OnClickListener {
    private Button btnVerkenning, btnQuiz;

    private ProgressBar spinner;
    private TextView loadingTxt;
    private ImageView logo;
    // Url to get JSON
    private static final String GET_QUESTION_URL = "http://wildlands.doornbosagrait.tk/api/api.php?c=GetAllQuestions";

    /*
    Tags om variabelen uit JSON te halen
     */
    private static final String TAG_ID = "id";
    private static final String TAG_PINPOINTID = "pinpointId";
    private static final String TAG_TEXT = "text";
    private static final String TAG_IMAGE = "image";
    private static final String TAG_ANSWERS = "answers";
    private static final String TAG_RIGHTWRONG = "rightWrong";
    private static final String TAG_CHECKSUM = "checksum";

    private ArrayList<Question> questions;

    private JSONArray questionArray;
    private JSONArray jsonArray;

    // JSONParser voor ophalen van data
    JSONParser jsonParser = new JSONParser();

    ArrayList<HashMap<String, String>> mQuestionList;

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
        questionArray = new JSONArray();
        jsonArray = new JSONArray();

        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        loadingTxt = (TextView)findViewById(R.id.loading);
        questions = new ArrayList<Question>();
        if(((DefaultApplication)this.getApplication()).isQuestionsLoaded() == false) {
            spinner.setVisibility(View.VISIBLE);
            loadingTxt.setVisibility(View.VISIBLE);
            new Search().execute();
        }
        else{
            //spinner.setVisibility(View.INVISIBLE);
            //loadingTxt.setVisibility(View.INVISIBLE);
            logo.setVisibility(View.VISIBLE);
            btnVerkenning.setVisibility(View.VISIBLE);
            btnQuiz.setVisibility(View.VISIBLE);
            animateFadeIn();
        }
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

    public void updateJSONdata(){

        mQuestionList = new ArrayList<HashMap<String, String>>();
        ((DefaultApplication)this.getApplication()).setQuestionsLoaded(true);
        try {
            questionArray = jsonArray;
            String baseUrl = "http://wildlands.doornbosagrait.tk/app/images/";

            // looping through all posts according to the json object returned
            for (int i = 0; i < questionArray.length(); i++) {
                JSONObject c = questionArray.getJSONObject(i);
                String bitImage = c.getString(TAG_IMAGE);
                Log.d("url", bitImage);
                String urlString = baseUrl + bitImage;
                Log.d("urlstring", urlString);

                //datasource.createQuestion(c.getString(TAG_TEXT));
                Question q = new Question(c.getString(TAG_TEXT), urlString);

                //questions.add(q);
                ((DefaultApplication)this.getApplication()).addQuestion(q);
                JSONArray a = c.getJSONArray(TAG_ANSWERS);
                for(int j = 0; j < a.length(); j++){
                    JSONObject ans = a.getJSONObject(j);
                    String answer = ans.getString(TAG_TEXT);
                    boolean good = ans.getBoolean(TAG_RIGHTWRONG);

                    q.addAnswer(answer, good);

                }
                // creating new HashMap
                HashMap<String, String> map = new HashMap<String, String>();

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

        spinner.setVisibility(View.INVISIBLE);
        loadingTxt.setVisibility(View.INVISIBLE);
        logo.setVisibility(View.VISIBLE);
        btnVerkenning.setVisibility(View.VISIBLE);
        btnQuiz.setVisibility(View.VISIBLE);
        animateFadeIn();


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
                Intent i = new Intent(this, ChooseQuizGroup.class);
                startActivity(i);
                this.finish();
                break;
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
                    GET_QUESTION_URL, "POST", params);
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
