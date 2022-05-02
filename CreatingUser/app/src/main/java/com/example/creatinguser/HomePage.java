package com.example.creatinguser;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.creatinguser.activities.BarPageActivity;

import com.example.creatinguser.utilities.Constants;
import com.example.creatinguser.utilities.PreferenceManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.creatinguser.activities.FanPageActivity;
import com.example.creatinguser.activities.ProfileActivity;

import java.util.HashMap;

import com.google.android.youtube.player.YouTubeStandalonePlayer;

public class HomePage extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    PreferenceManager preferenceManager;
    private FirebaseFirestore database;
    String userId;

    CardView cvMessage, cvMap, cvScore, cvNews, cvFanPage, cvProfilePage, cvBarPage, cvPlayoffBracket, cvVideo, cvSportsTeams, cvSchedule;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        database = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());
        Intent intent = getIntent();
        userId = intent.getStringExtra(Constants.KEY_USER_ID);

        System.out.println("\n \n Data being passed "+ userId+"\n \n ");

        // id to feature connection
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        cvMessage = findViewById(R.id.cvMessage);
        cvMap = findViewById(R.id.cvMap);
        cvScore = findViewById(R.id.cvScore);
        cvNews = findViewById(R.id.cvNews);
        cvBarPage = findViewById(R.id.cvBar);


        cvBarPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BarPageActivity.class);
                startActivity(intent);
            }
        });
      
        cvFanPage = findViewById(R.id.cvFanPage);
        cvProfilePage = findViewById(R.id.cvProfilePage);
        cvPlayoffBracket = findViewById(R.id.cvPlayoffBracket);
        cvBarPage = findViewById(R.id.cvBar);
        cvSportsTeams = findViewById(R.id.cvSportsTeams);
        cvVideo = findViewById(R.id.cvYoutube);
        cvSchedule = findViewById(R.id.cvSchedules);

        bottomNavigationView.setSelectedItemId(R.id.home); // sets highlight on bar

        getOnClickListeners();
        bottomNavBar();
    }

    public void bottomNavBar(){
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.logout:
                        Toast toast = Toast.makeText(getApplicationContext(),"Signing out...",Toast.LENGTH_LONG);
                        toast.show();
                        FirebaseFirestore database = FirebaseFirestore.getInstance();
                        DocumentReference documentReference =
                                database.collection(Constants.KEY_COLLECTION_USERS).document(
                                        preferenceManager.getString(Constants.KEY_USER_ID)
                                );
                        HashMap<String,Object> updates = new HashMap<>();
                        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
                        documentReference.update(updates)
                                .addOnSuccessListener(unused -> {
                                    preferenceManager.clear();
                                    startActivity(new Intent(getApplicationContext(), LoginPage.class));
                                    finish();
                                });
                        return true;

                    case R.id.home:
                        return true;

                    case R.id.sportsTeams:
                        startActivity(new Intent(getApplicationContext(), SportsTeamsMainPage.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

    }

    // all of the onClickListeners
    public void getOnClickListeners(){
        cvVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), StandaloneActivity.class);
                startActivity(intent);
//                Intent intent = YouTubeStandalonePlayer.createPlaylistIntent(this, YoutubeActivity.GOOGLE_API_KEY, YoutubeActivity.YOUTUBE_PLAYLIST);
//                startActivity(intent);
            }
        });

        cvSportsTeams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SportsTeamsMainPage.class);
                startActivity(intent);
            }
        });

        cvBarPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BarPageActivity.class);
                startActivity(intent);
            }
        });

        cvFanPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FanPageActivity.class);
                //intent.putExtra("message", current_username);
                startActivity(intent);
            }
        });

        cvProfilePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                //intent.putExtra("message", current_username);
                startActivity(intent);
            }
        });

        cvNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Newsfeed.class);
                //intent.putExtra("message", current_username);
                startActivity(intent);
            }
        });

        cvMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), GoogleMaps.class);
                //intent.putExtra("message", current_username);
                startActivity(intent);
            }
        });

        cvMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DirectMessageScreen.class);
                intent.putExtra(Constants.KEY_USER_ID, userId);
                startActivity(intent);
            }
        });

        cvScore.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LiveScores.class);
                startActivity(intent);
            }
        });

        cvPlayoffBracket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BracketCell.class);
                startActivity(intent);
            }
        });

        cvSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ScheduleMainPage.class);
                startActivity(intent);
            }
        });

    }

//    @Override
//    public void onClick(View view) {
//        cvVideo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Intent intent = new Intent(getApplicationContext(), YoutubeActivity.class);
////                startActivity(intent);
//                Intent intent = YouTubeStandalonePlayer.createPlaylistIntent(this, YoutubeActivity.GOOGLE_API_KEY, YoutubeActivity.YOUTUBE_PLAYLIST);
//                startActivity(intent);
//            }
//        });
//
//    }
}

