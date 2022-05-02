package com.example.creatinguser;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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

public class SpecificNBAPlayer extends AppCompatActivity {
    private TextView Title;
    private ListView nbaPlayerStats;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nbaplayer);

        Title = findViewById(R.id.nbaPlayerTitle);
        nbaPlayerStats = (ListView)findViewById(R.id.nbaPlayerStats);

        URL url = null;
        HttpURLConnection connection;
        StringBuilder urlBuilder = new StringBuilder();
        Bundle extras = getIntent().getExtras();
        urlBuilder.append("https://api.sportsdata.io/v3/nba/stats/json/PlayerSeasonStatsByPlayer/2022/");
        urlBuilder.append(extras.getString("playerID"));
        try {
            url = new URL(urlBuilder.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        callAPINBAPlayer getPlayerStats = new callAPINBAPlayer();
        String result = null;
        try {
            result = getPlayerStats.execute(String.valueOf(url)).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ArrayList<String> playerStats = new ArrayList<String>();
        System.out.println(result);
        try {
            playerStats = onResponseTeam(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(playerStats);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,playerStats);
        nbaPlayerStats.setAdapter(arrayAdapter);
    }

    public class callAPINBAPlayer extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String stringURL = params[0];
            StringBuilder result = new StringBuilder();
            String finalResult;
            HttpURLConnection connection;
            try {
                URL url = new URL(stringURL);
                connection = (HttpURLConnection) url.openConnection();

                connection.setRequestProperty("Ocp-Apim-Subscription-Key", "65ed88bbffb9431ca49d73da12737d0b");
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

    private ArrayList<String> onResponseTeam(String result) throws JSONException {
        ArrayList<String> playerStats = new ArrayList<String> ();
            JSONObject jsonResult = new JSONObject(result);
                    playerStats.add("Games: " + jsonResult.getString("Games") + "\nStarted: " + jsonResult.getString("Started") + "\nMinutes: " +
                            jsonResult.getString("Minutes") + "\nFieldGoalsMade: " + jsonResult.getString("FieldGoalsMade") + "\nFieldGoalsAttempted: "
                            + jsonResult.getString("FieldGoalsAttempted") + "\nFieldGoalsPercentage: " + jsonResult.getString("FieldGoalsPercentage")
                            + "\nTwoPointersMade: " + jsonResult.getString("TwoPointersMade") + "\nTwoPointersAttempted: " + jsonResult.getString("TwoPointersAttempted")
                            + "\nTwoPointersPercentage: " + jsonResult.getString("TwoPointersPercentage") + "\nThreePointersMade: " + jsonResult.getString("ThreePointersMade")
                            + "\nThreePointersAttempted: " + jsonResult.getString("ThreePointersAttempted") + "\nThreePointersPercentage: " + jsonResult.getString("ThreePointersPercentage")
                            + "\nFreeThrowsMade: " + jsonResult.getString("FreeThrowsMade") + "\nFreeThrowsAttempted: " + jsonResult.getString("FreeThrowsAttempted")
                            + "\nFreeThrowsPercentage: " + jsonResult.getString("FreeThrowsPercentage") + "\nRebounds: " + jsonResult.getString("Rebounds")
                            + "\nOffensiveRebounds: " + jsonResult.getString("OffensiveRebounds") + "\nDefensiveRebounds: " + jsonResult.getString("DefensiveRebounds")
                            + "\nAssists: " + jsonResult.getString("Assists") + "\nSteals: " + jsonResult.getString("Steals") + "\nBlockedShots: " + jsonResult.getString("BlockedShots")
                            + "\nTurnovers: " + jsonResult.getString("Turnovers") + "\nPersonalFouls: " + jsonResult.getString("PersonalFouls") + "\nPoints: " + jsonResult.getString("Points")
                            + "\nPlayerEfficiencyRating: " + jsonResult.getString("PlayerEfficiencyRating") + "\nPlusMinus: " + jsonResult.getString("PlusMinus")
                            + "\nDoubleDoubles: " + jsonResult.getString("DoubleDoubles") + "\nTripleDoubles: " + jsonResult.getString("TripleDoubles"));
        setText(this.Title, jsonResult.getString("Name"));
        return playerStats;
    }

    private void setText(TextView text, String value){
        text.setText(value);
    }
}

