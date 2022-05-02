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

public class SpecificNFLPlayer extends AppCompatActivity {
    private TextView Title;
    private ListView nflPlayerStats;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nflplayer);

        Title = findViewById(R.id.nflPlayerTitle);
        nflPlayerStats = (ListView)findViewById(R.id.nflPlayerStats);

        URL url = null;
        HttpURLConnection connection;
        StringBuilder urlBuilder = new StringBuilder();
        Bundle extras = getIntent().getExtras();
        urlBuilder.append("https://api.sportsdata.io/v3/nfl/stats/json/PlayerSeasonStatsByPlayerID/2021/");
        urlBuilder.append(extras.getString("playerIDNFL"));
        try {
            url = new URL(urlBuilder.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        callAPINFLPlayer getPlayerStats = new callAPINFLPlayer();
        String result = null;
        try {
            result = getPlayerStats.execute(String.valueOf(url)).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ArrayList<String> playerStats = new ArrayList<String>();
        try {
            playerStats = onResponseTeam(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,playerStats);
        nflPlayerStats.setAdapter(arrayAdapter);
    }

    public class callAPINFLPlayer extends AsyncTask<String, Void, String> {
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

    private ArrayList<String> onResponseTeam(String result) throws JSONException {
        ArrayList<String> playerStats = new ArrayList<String> ();
        JSONArray jArrayTeam = new JSONArray(result);
        if(jArrayTeam.length() != 0){

            for(int n = 0; n < jArrayTeam.length(); n++) {
                JSONObject jsonResult = jArrayTeam.getJSONObject(n);
                playerStats.add("Played: " + jsonResult.getString("Played") + "\nStarted: " + jsonResult.getString("Started") + "\nPassingAttempts: " +
                        jsonResult.getString("PassingAttempts") + "\nPassingCompletions: " + jsonResult.getString("PassingCompletions") + "\nPassingYards: "
                        + jsonResult.getString("PassingYards") + "\nPassingYardsPerAttempt: " + jsonResult.getString("PassingYardsPerAttempt")
                        + "\nPassingYardsPerCompletion: " + jsonResult.getString("PassingYardsPerCompletion") + "\nPassingTouchdowns: " + jsonResult.getString("PassingTouchdowns")
                        + "\nPassingInterceptions: " + jsonResult.getString("PassingInterceptions") + "\nPassingRating: " + jsonResult.getString("PassingRating")
                        + "\nRushingAttempts: " + jsonResult.getString("RushingAttempts") + "\nRushingYards: " + jsonResult.getString("RushingYards")
                        + "\nRushingTouchdowns: " + jsonResult.getString("RushingTouchdowns") + "\nReceivingTargets: " + jsonResult.getString("ReceivingTargets")
                        + "\nReceptions: " + jsonResult.getString("Receptions") + "\nReceivingYards: " + jsonResult.getString("ReceivingYards")
                        + "\nReceivingYardsPerReception: " + jsonResult.getString("ReceivingYardsPerReception") + "\nReceivingTouchdowns: " + jsonResult.getString("ReceivingTouchdowns")
                        + "\nFumbles: " + jsonResult.getString("Fumbles") + "\nPuntReturns: " + jsonResult.getString("PuntReturns") + "\nPuntReturnYards: " + jsonResult.getString("PuntReturnYards")
                        + "\nPuntReturnYardsPerAttempt: " + jsonResult.getString("PuntReturnYardsPerAttempt") + "\nPuntReturnTouchdowns: " + jsonResult.getString("PuntReturnTouchdowns") + "\nKickReturns: " + jsonResult.getString("KickReturns")
                        + "\nKickReturnYards: " + jsonResult.getString("KickReturnYards") + "\nKickReturnYardsPerAttempt: " + jsonResult.getString("KickReturnYardsPerAttempt")
                        + "\nKickReturnTouchdowns: " + jsonResult.getString("KickReturnTouchdowns") + "\nSoloTackles: " + jsonResult.getString("SoloTackles")
                        + "\nAssistedTackles: " + jsonResult.getString("AssistedTackles") + "\nTacklesForLoss: " + jsonResult.getString("TacklesForLoss")
                        + "\nSacks: " + jsonResult.getString("Sacks") + "\nSackYards: " + jsonResult.getString("SackYards")
                        + "\nQuarterbackHits: " + jsonResult.getString("QuarterbackHits") + "\nPassesDefended: " + jsonResult.getString("PassesDefended")
                        + "\nFumblesForced: " + jsonResult.getString("FumblesForced") + "\nFumblesRecovered: " + jsonResult.getString("FumblesRecovered")
                        + "\nFumbleReturnYards: " + jsonResult.getString("FumbleReturnYards") + "\nFumbleReturnTouchdowns: " + jsonResult.getString("FumbleReturnTouchdowns")
                        + "\nInterceptions: " + jsonResult.getString("Interceptions") + "\nInterceptionReturnYards: " + jsonResult.getString("InterceptionReturnYards")
                        + "\nInterceptionReturnTouchdowns: " + jsonResult.getString("InterceptionReturnTouchdowns") + "\nBlockedKicks: " + jsonResult.getString("BlockedKicks") + "\nPunts: " + jsonResult.getString("Punts")
                        + "\nPuntYards: " + jsonResult.getString("PuntYards") + "\nFieldGoalsAttempted: " + jsonResult.getString("FieldGoalsAttempted") + "\nFieldGoalsMade: " + jsonResult.getString("FieldGoalsMade")
                        + "\nFieldGoalsLongestMade: " + jsonResult.getString("FieldGoalsLongestMade") + "\nKickReturnYardsPerAttempt: " + jsonResult.getString("KickReturnYardsPerAttempt")
                        + "\nKickReturnTouchdowns: " + jsonResult.getString("KickReturnTouchdowns") + "\nSoloTackles: " + jsonResult.getString("SoloTackles"));
                setText(this.Title, jsonResult.getString("Name") + ", " + jsonResult.getString("Position"));
            }
        }
        return playerStats;
    }

    private void setText(TextView text, String value){
        text.setText(value);
    }
}
