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

import com.example.creatinguser.utilities.Constants;
import com.example.creatinguser.utilities.PreferenceManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SportsTeamsMainPage extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    PreferenceManager preferenceManager;
    private TextView Title;
    private Button homePage;
    private Button nbaTeams;
    private Button nflTeams;
    //private TextView currScoresText;
    //private Button stats;
    //private Button myProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sportsteams);

        Title = findViewById(R.id.sportsTeamsMainPageFansverse);
        nbaTeams = findViewById(R.id.buttonNBATeams);
        nflTeams = findViewById(R.id.buttonNFLTeams);
        //currScoresText = findViewById(R.id.currScoresText);
        //stats = findViewById(R.id.buttonStats);
        //myProfile = findViewById(R.id.buttonProfile);
        preferenceManager = new PreferenceManager(getApplicationContext());
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.sportsTeams);
        bottomNavBar();


        nbaTeams.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NBATeamsMainPage.class);
                startActivity(intent);
            }
        });

        nflTeams.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NFLTeamsMainPage.class);
                startActivity(intent);
            }
        });
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

                    case R.id.sportsTeams:
                        return true;

                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), HomePage.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

    }

}
