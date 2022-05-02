package com.example.creatinguser;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class CurrentScoresNFL extends AppCompatActivity {
    private ListView nflLiveScores;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livescoresnfl);

        nflLiveScores = (ListView)findViewById(R.id.nflLiveScores);

        URL url = null;
        HttpURLConnection connection;
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(" https://api.sportsdata.io/v3/nfl/scores/json/ScoresByDate/");
        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String date = formatter.format(todayDate);
        urlBuilder.append(date);
        try {
            url = new URL(urlBuilder.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        callAPINFLScores getNFLScores = new callAPINFLScores();
        String result = null;
        try {
            result = getNFLScores.execute(String.valueOf(url)).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ArrayList<String> nflLiveScoresList = new ArrayList<String>();
        nflLiveScoresList = onResponseTeam(result);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,nflLiveScoresList);
        nflLiveScores.setAdapter(arrayAdapter);
    }

    public class callAPINFLScores extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String stringURL = params[0];
            StringBuilder result = new StringBuilder();
            String finalResult;
            HttpURLConnection connection;
            try {
                URL url = new URL(stringURL);
                connection = (HttpURLConnection) url.openConnection();

                connection.setRequestProperty("Ocp-Apim-Subscription-Key", "d5cacbee788840cbbd9291a17b6d9dcc");
                connection.setRequestProperty("content-type", "application/json");

                connection.setRequestMethod("GET");

                InputStream in = connection.getInputStream();
                Reader reader = new BufferedReader(new InputStreamReader(in));

                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result.append(current);
                    data = reader.read();
                }
                finalResult = result.toString();

            } catch (IOException e) {
                e.printStackTrace();
                finalResult = null;
            }
            return finalResult;

        }

        protected void onPostExecute(String result){
            super.onPostExecute(result);
        }
    }

    private ArrayList<String> onResponseTeam(String result){
        ArrayList<String> nflLiveScores = new ArrayList<String> ();
        try{
            JSONArray jArrayTeam = new JSONArray(result);

            if(jArrayTeam.length() != 0){

                for(int n = 0; n < jArrayTeam.length(); n++) {
                    JSONObject teams = jArrayTeam.getJSONObject(n);
                    nflLiveScores.add(teams.getString("Date") + ": " + teams.getString("HomeTeam") + " versus " + teams.getString("AwayTeam") + "\n Status: " +
                            teams.getString("Status"));
                }
            }
            else{
                nflLiveScores.add("There is no live game right now \n");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return nflLiveScores;
    }
}
