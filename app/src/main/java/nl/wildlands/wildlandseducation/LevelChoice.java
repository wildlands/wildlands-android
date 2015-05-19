package nl.wildlands.wildlandseducation;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class LevelChoice extends Activity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    Spinner spinner;
    Button gaverder;
    JSONParser jsonParser;
    String selectedLevel;
    private static final String GET_LEVELS_URL = "http://wildlands.doornbosagrait.tk/api/api.php?c=GetAllLevels";
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
                String content = level.toUpperCase();
                if(i == 0)
                {
                    selectedLevel = content;
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
        selectedLevel = parent.getItemAtPosition(position).toString();

        Log.d("level", selectedLevel);
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

    class GetLevels extends AsyncTask<String, String, String> implements View.OnClickListener {

        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args) {
            //Posting user data to script
            jsonArray = jsonParser.getJSONFromUrl(GET_LEVELS_URL);
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
