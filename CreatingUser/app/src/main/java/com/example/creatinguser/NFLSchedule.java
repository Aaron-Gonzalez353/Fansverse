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
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class NFLSchedule extends AppCompatActivity {
    private ListView nflSchedule;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nflschedule);

        nflSchedule = (ListView)findViewById(R.id.nflSchedule);

        URL url = null;
        HttpURLConnection connection;
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(" https://api.sportsdata.io/v3/nfl/scores/json/Schedules/2021");
        try {
            url = new URL(urlBuilder.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        callAPINFLSchedule getNFLSchedule = new callAPINFLSchedule();
        String result = null;
        try {
            result = getNFLSchedule.execute(String.valueOf(url)).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ArrayList<String> nflScheduleList = new ArrayList<String>();
        nflScheduleList = onResponseTeam(result);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,nflScheduleList);
        nflSchedule.setAdapter(arrayAdapter);
    }

    public class callAPINFLSchedule extends AsyncTask<String, Void, String> {
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
        ArrayList<String> nflSchedule = new ArrayList<String> ();
        try{
            JSONArray jArrayTeam = new JSONArray(result);

            if(jArrayTeam.length() != 0){

                for(int n = 0; n < jArrayTeam.length(); n++) {
                    JSONObject teams = jArrayTeam.getJSONObject(n);
                    nflSchedule.add(teams.getString("Date") + ": " + teams.getString("HomeTeam") + " versus " + teams.getString("AwayTeam") + "\n Status: " +
                            teams.getString("Status"));
                }
            }
            else{
                String nflScheduleFail = "There is no schedule you messed up the code \n";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return nflSchedule;
    }
}
