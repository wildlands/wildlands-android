package nl.wildlands.wildlandseducation;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.Image;
import android.media.ImageReader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.nkzawa.socketio.client.Socket;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Quiz extends Activity implements OnClickListener {

    private ArrayList<Question> questions;                              // ArrayList to store all questions
    private ArrayList<Answer> answers;

    private Answer actualAnswer;
    public static final String MyPREFERENCES = "MyPrefs" ;              // String to get sharedprefs
    private Socket socket;

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

    /*
    Grafische elementen voor layout
     */
    private TextView question;
    private Button answer1, answer2, answer3, answer4, answer5, answer6, answer7, answer8;
    private ImageView img1;

    /*
    Datasource voor SQLite
     */
    private QuestionsDataSource datasource;

    /*
    Sharedpreferences voor versie
     */
    SharedPreferences sharedpreferences;

    /*
    Quizvariabelen om goede vragen en vraagnummer bij te houden
     */
    private int questionNumber;
    private int questionsCorrect;

    // JSONParser voor ophalen van data
    JSONParser jsonParser = new JSONParser();

    /*
    JSONObject van de data
     */
    private JSONObject questionObj = null;
    private JSONArray questionArray;
    private JSONArray jsonArray;

    ArrayList<HashMap<String, String>> mQuestionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        socket = ((DefaultApplication)this.getApplication()).getSocket();
        // To use the full width and high of the screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        question = (TextView)findViewById(R.id.textView); // Textview to display the question(s)

        img1 = (ImageView)findViewById(R.id.imageView); // Image above the question

        /*
            Buttons toewijzen
         */
        answer1 = (Button)findViewById(R.id.button);
        answer2 = (Button)findViewById(R.id.button2);
        answer3 = (Button)findViewById(R.id.button3);
        answer4 = (Button)findViewById(R.id.button4);
        answer5 = (Button)findViewById(R.id.button5);
        answer6 = (Button)findViewById(R.id.button6);
        answer7 = (Button)findViewById(R.id.button7);
        answer8 = (Button)findViewById(R.id.button8);



        /*
            Mogelijkheid tot klikken
         */
        answer1.setOnClickListener(this);
        answer2.setOnClickListener(this);
        answer3.setOnClickListener(this);
        answer4.setOnClickListener(this);
        answer5.setOnClickListener(this);
        answer6.setOnClickListener(this);
        answer7.setOnClickListener(this);
        answer8.setOnClickListener(this);

        questionArray = new JSONArray();
        jsonArray = new JSONArray();

       // questions = ((DefaultApplication)this.getApplication()).getQuestions();

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        datasource = new QuestionsDataSource(this);
        datasource.open();

        questions = datasource.getAllQuestions();
        answers = datasource.getAllAnswers();

        ArrayList<Question> values = datasource.getAllQuestions();

        display(questionNumber);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(0);

       // new CheckVersion().execute();

    }

    public void updateJSONdata(){

        mQuestionList = new ArrayList<HashMap<String, String>>();

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

             //   datasource.createQuestion(c.getString(TAG_TEXT));
              //  Question q = new Question(c.getString(TAG_TEXT), urlString);

               // questions.add(q);
                JSONArray a = c.getJSONArray(TAG_ANSWERS);
                for(int j = 0; j < a.length(); j++){
                    JSONObject ans = a.getJSONObject(j);
                    String answer = ans.getString(TAG_TEXT);
                    boolean good = ans.getBoolean(TAG_RIGHTWRONG);

                  //  q.addAnswer(answer, good);

                }
                // creating new HashMap
                HashMap<String, String> map = new HashMap<String, String>();

                //map.put(TAG_ID, text);
                // adding HashList to ArrayList
                mQuestionList.add(map);

                // annndddd, our JSON data is up to date same with our array
                // list
            }
            Log.d("Goed vraag 1", questions.get(0).getCorrectAnswer());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        new ImageLoader().execute();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        display(questionNumber);


    }

    public void display(int i){
        int searchId = i + 1;
        ArrayList<Answer> answers1 = new ArrayList<Answer>();
        for(Answer answer: answers)
        {
            if(answer.getVraagId() == searchId)
            {
                answers1.add(answer);
                if(answer.isGood())
                {
                    actualAnswer = answer;
                }
            }
        }

        if(questions.size() <= i){
            Intent quizEnd = new Intent(this, view_11.class);
            startActivity(quizEnd);
            this.finish();
            socket.disconnect();

        }
        else {
            answer1.setVisibility(View.VISIBLE);
            answer2.setVisibility(View.VISIBLE);
            answer3.setVisibility(View.VISIBLE);
            answer4.setVisibility(View.VISIBLE);
            answer5.setVisibility(View.VISIBLE);
            answer6.setVisibility(View.VISIBLE);
            answer7.setVisibility(View.VISIBLE);
            answer8.setVisibility(View.VISIBLE);
            //if(datasource.getAllQuestions().get(i).getQuestion() != null){
           //     question.setText(datasource.getAllQuestions().get(i).getQuestion());
          //  }
           // else {
                question.setText(questions.get(i).getQuestion());
          //  }
            if(questions.get(i).getImagePath() != "") {
               // img1.setImageBitmap(questions.get(i).getBmp());

                loadImageFromStorage(questions.get(i).getImagePath());
            }
            if(answers1.size() > 0) {
                if (answers1.size() == 3) {
                    answer1.setText(answers1.get(0).getAnswer());
                    answer2.setText(answers1.get(1).getAnswer());
                    answer3.setText(answers1.get(2).getAnswer());
                } else {
                    answer1.setText(answers1.get(0).getAnswer());
                    answer2.setText(answers1.get(1).getAnswer());
                    answer3.setText(answers1.get(2).getAnswer());
                    answer4.setText(answers1.get(3).getAnswer());
                }
            }
            if(answers1.size() <= 7){answer8.setVisibility(View.GONE);}
            if(answers1.size() <= 6){answer7.setVisibility(View.GONE);}
            if(answers1.size() <= 5){answer6.setVisibility(View.GONE);}
            if(answers1.size() <= 4){answer5.setVisibility(View.GONE);}
            if(answers1.size() <= 3){answer4.setVisibility(View.GONE);}
            if(answers1.size() <= 2){answer3.setVisibility(View.GONE);}


        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(hasFocus == false)
        {
            socket.disconnect();
        }
    }

    /**
     * Method to store the images internally
     * @param bitmapImage
     * @param filename
     * @return path to file
     */

    private String saveToInternalStorage(Bitmap bitmapImage, String filename){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        Log.d("dir", directory.toString());
        // Create imageDir
        File mypath=new File(directory,filename);

        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(mypath);

            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath();
    }

    /**
     * Load image from internal path
     * @param path
     */
    private void loadImageFromStorage(String path)
    {
        try {
            File f=new File(path, "profile.png");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            ImageView img=(ImageView)findViewById(R.id.imageView);
            img.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }

    /**
     * Check if the correct answer is pressed
     * if so, socre is incremented
     * @param answer
     */
    public void checkAnswer(String answer)
    {
        int code = ((DefaultApplication)this.getApplication()).getSocketcode();
        String naam = ((DefaultApplication)this.getApplication()).getSocketnaam();
        Log.d("Ans en ques", answer + questionNumber);
        Log.d("Correct",  questions.get(questionNumber).getCorrectAnswer());
        boolean correct = false;
        if(answer == actualAnswer.getAnswer())
        {
            questionsCorrect += 1;
            correct = true;
        }
        JSONObject message = new JSONObject();

        try {
            message.put("naam", naam);
            message.put("vraag", questionNumber);
            message.put("goed", correct);
            message.put("quizID",code );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit("sendAnswer", message);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                //Intent h = new Intent(this, Kaart.class);
               // this.startActivity(h);
                checkAnswer(answer1.getText().toString());
                questionNumber += 1;
                display(questionNumber);
                break;
            case R.id.button2:
                checkAnswer(answer2.getText().toString());
                questionNumber += 1;
                display(questionNumber);
                break;
            case R.id.button3:
                checkAnswer(answer3.getText().toString());
                questionNumber += 1;
                display(questionNumber);
                break;
            case R.id.button4:
                checkAnswer(answer4.getText().toString());
                questionNumber += 1;
                display(questionNumber);
                break;
        }
    }

    /**
     * Class to load the images from the right urls
     */
    class ImageLoader extends  AsyncTask<String, String, String> implements  OnClickListener{
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
            for(Question question: questions){
                if(question.getImage() != "http://doornbosagrait.no-ip.org/wildlandsBackend/app/images/") {
                    String urlString = question.getImage();
                    Bitmap bitmap = null;
                    try {

                        URL url = new URL(urlString);

                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());


                    } catch (Exception e) {
                        Log.d(e.toString(), e.toString());
                    }
                    ;
                    String path = saveToInternalStorage(bitmap, "picture.png");
                    question.setImagePath(path);
                }
            }

            for(Question question: datasource.getAllQuestions()){
                if(question.getImage() != "http://doornbosagrait.no-ip.org/wildlandsBackend/app/images/") {
                    String urlString = question.getImage();
                    Bitmap bitmap = null;
                    try {

                        URL url = new URL(urlString);

                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());


                    } catch (Exception e) {
                        Log.d(e.toString(), e.toString());
                    }
                    ;
                    String path = saveToInternalStorage(bitmap, "picture.png");
                    question.setImagePath(path);
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

    class Search extends AsyncTask<String, String, String> implements OnClickListener {

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

    class CheckVersion extends AsyncTask<String, String, String> implements OnClickListener {

        boolean failure = false;
        boolean loadNewData = true;
        long appVersion;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args) {


            JSONObject versionObj = jsonParser.getJSONObjFromUrl("http://wildlands.doornbosagrait.tk//api/api.php?c=GetDatabaseChecksum");
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
            // dismiss the dialog once product deleted

            /*
            if(appVersion == sharedpreferences.getLong("version", 0))
            {
                Log.d("versie", "Versies komen overeen");
                display(questionNumber);
            }
            else {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putLong("version", appVersion);
                editor.commit();
                Log.d("Check version", String.valueOf(appVersion));
                new Search().execute();
            }*/
            new Search().execute();


        }

        @Override
        public void onClick(View v) {

        }
    }
}
