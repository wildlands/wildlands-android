package nl.wildlands.wildlandseducation.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import nl.wildlands.wildlandseducation.GlobalSettings.DefaultApplication;
import nl.wildlands.wildlandseducation.GlobalSettings.Values;
import nl.wildlands.wildlandseducation.JSONParser;
import nl.wildlands.wildlandseducation.R;


public class LevelChoice extends Activity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    Spinner spinner;
    Button gaverder;
    JSONParser jsonParser;
    int selectedLevel;
    private static final String GET_LEVELS_URL = Values.BASE_URL + Values.GET_LEVELS;
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    JSONArray jsonArray;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_choice);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        spinner = (Spinner)findViewById(R.id.levels_spinner);

        gaverder = (Button)findViewById(R.id.gaverder);
        gaverder.setOnClickListener(this);
        jsonParser = new JSONParser();
        jsonArray = new JSONArray();
        new GetLevels().execute();
    }

    public void updateJSONdata(){
        try {
            JSONArray questionArray = jsonArray;
            ArrayList<String> spinnerArray = new ArrayList<String>();

            for (int i = 0; i < questionArray.length(); i++)
            {
                JSONObject c = questionArray.getJSONObject(i);
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
            spinner.setAdapter(spinnerArrayAdapter);
            spinner.setOnItemSelectedListener(this);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedLevel = position+1;

        Log.d("level", String.valueOf(selectedLevel));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.gaverder:
                ((DefaultApplication)this.getApplication()).setLevel(selectedLevel);
                Intent h = new Intent(this, Home.class);
                startActivity(h);
                this.finish();
                break;

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
            jsonArray = jsonParser.getJSONFromUrl(GET_LEVELS_URL);
            return jsonArray.toString();
        }

        protected void onPostExecute(String file_url) {
            updateJSONdata();
        }

    }
}
