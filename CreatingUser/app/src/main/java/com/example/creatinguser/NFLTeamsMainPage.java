package com.example.creatinguser;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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

public class NFLTeamsMainPage extends AppCompatActivity {
    private ListView nflTeamsList;
    private ArrayList<String> teamKeys = new ArrayList<String> ();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nflteams);

        //Title = findViewById(R.id.nbaTeamsPageFansverse);
        //sportsMainPage = findViewById(R.id.buttonSportsTeamMainPage);
        //nbaTeamsText = findViewById(R.id.nbaTeamsText);
        //showNBATeams = findViewById(R.id.buttonShowTeams);
        nflTeamsList = (ListView)findViewById(R.id.nflTeamsList);

        /*sportsMainPage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SportsTeamsMainPage.class);
                startActivity(intent);
            }
        });*/

        //showNBATeams.setOnClickListener(new View.OnClickListener(){

        //public void onClick(View v) {
        URL myUrl = null;
        try {
            myUrl = new URL("https://api.sportsdata.io/v3/nfl/scores/json/Teams");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        callAPINFLTeams getNFLTeams = new callAPINFLTeams();
        String result = null;
        try {
            result = getNFLTeams.execute(String.valueOf(myUrl)).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ArrayList<String> nflTeams = new ArrayList<String>();
        nflTeams = onResponseTeams(result);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,nflTeams);
        nflTeamsList.setAdapter(arrayAdapter);

        nflTeamsList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NFLTeamsMainPage.this, SpecificNFLTeam.class);
                intent.putExtra("teamKey", teamKeys.get(position));
                startActivity(intent);
            }
        });

        //}
        //});
    }

    public class  callAPINFLTeams extends AsyncTask<String, Void, String> {
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
    private ArrayList<String> onResponseTeams(String result){
        ArrayList<String> nflTeams = new ArrayList<String> ();
        try{
            JSONArray jArrayTeams = new JSONArray(result);

            if(jArrayTeams.length() != 0){

                for(int n = 0; n < jArrayTeams.length(); n++) {
                    JSONObject teams = jArrayTeams.getJSONObject(n);
                    nflTeams.add(teams.getString("City") + " " + teams.getString("Name"));
                    teamKeys.add(teams.getString("Key"));
                    //setText(this.nbaTeamsText, nbaTeams, teams);
                }
            }
            else{
                System.out.println("There are no teams you messed up the code \n");
                //setText(this.nbaTeamsText, nbaTeams, null);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return nflTeams;
    }

    /*private void setText(TextView text, String value, JSONObject teams){
        text.setText(value);
        text.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), SpecificNBATeam.class);
                try {
                    intent.putExtra("teamID", teams.getString("teamId"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
            }
        });
    }*/

}
