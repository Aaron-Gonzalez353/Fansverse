package com.example.creatinguser;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class SpecificNFLTeam extends AppCompatActivity {
    private ListView nflTeamList;
    private ArrayList<String> playerID = new ArrayList<String> ();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nflteam);

        nflTeamList = (ListView)findViewById(R.id.nflTeamList);

        URL url = null;
        HttpURLConnection connection;
        StringBuilder urlBuilder = new StringBuilder();
        Bundle extras = getIntent().getExtras();
        urlBuilder.append("https://api.sportsdata.io/v3/nfl/scores/json/Players/");
        urlBuilder.append(extras.getString("teamKey"));
        try {
            url = new URL(urlBuilder.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        callAPINFLTeam getNFLTeam = new callAPINFLTeam();
        String result = null;
        try {
            result = getNFLTeam.execute(String.valueOf(url)).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ArrayList<String> nflTeam = new ArrayList<String>();
        nflTeam = onResponseTeam(result);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,nflTeam);
        nflTeamList.setAdapter(arrayAdapter);

        nflTeamList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SpecificNFLTeam.this, SpecificNFLPlayer.class);
                intent.putExtra("playerIDNFL", playerID.get(position));
                startActivity(intent);
            }
        });
    }

    public class callAPINFLTeam extends AsyncTask<String, Void, String> {
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
        ArrayList<String> nflTeam = new ArrayList<String> ();
        try{
            JSONArray jArrayTeams = new JSONArray(result);

            if(jArrayTeams.length() != 0){

                for(int n = 0; n < jArrayTeams.length(); n++) {
                    JSONObject teams = jArrayTeams.getJSONObject(n);
                    nflTeam.add(teams.getString("FirstName") + " " + teams.getString("LastName") + " Position: " +
                            teams.getString("Position") + "\n");
                    playerID.add(teams.getString("PlayerID"));
                }
            }
            else{
                String nflTeam2 = "There are no teams you messed up the code \n";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return nflTeam;
    }
}
