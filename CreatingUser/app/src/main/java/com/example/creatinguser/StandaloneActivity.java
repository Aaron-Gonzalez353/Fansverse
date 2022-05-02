package com.example.creatinguser;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.creatinguser.utilities.Constants;
import com.example.creatinguser.utilities.PreferenceManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class StandaloneActivity extends AppCompatActivity implements View.OnClickListener {

    CardView cvNba, cvNfl;
    BottomNavigationView bottomNavigationView;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standalone);

        cvNba = findViewById(R.id.cvNba);
        cvNfl = findViewById(R.id.cvNfl);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        cvNba.setOnClickListener(this);
        cvNfl.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        Intent intent = null;
        switch (view.getId()){
            case R.id.cvNba:
                intent = YouTubeStandalonePlayer.createPlaylistIntent(this, YoutubeActivity.GOOGLE_API_KEY, YoutubeActivity.NBA_HIGHLIGHTS);
                break;
            case R.id.cvNfl:
                intent = YouTubeStandalonePlayer.createPlaylistIntent(this, YoutubeActivity.GOOGLE_API_KEY, YoutubeActivity.NFL_PLAYLIST);
                break;
            default:
        }

        if(intent != null){
            startActivity(intent);
        }
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
                        startActivity(new Intent(getApplicationContext(), HomePage.class));
                        overridePendingTransition(0, 0);
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
}
