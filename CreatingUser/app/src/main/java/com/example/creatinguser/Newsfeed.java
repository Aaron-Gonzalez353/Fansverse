package com.example.creatinguser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.creatinguser.Models.NewsApiResponse;
import com.example.creatinguser.Models.NewsHeadlines;
import com.example.creatinguser.utilities.Constants;
import com.example.creatinguser.utilities.PreferenceManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;

public class Newsfeed extends AppCompatActivity implements SelectListener{

    RecyclerView recyclerView;
    CustomAdapter adapter;
    ProgressDialog dialog;
    BottomNavigationView bottomNavigationView;
    PreferenceManager preferenceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsfeed);

        preferenceManager = new PreferenceManager(getApplicationContext());

        // progress dialog while loading news articles
        dialog = new ProgressDialog(this);
        dialog.setTitle("Fetching news articles..");
        dialog.show();

        RequestManager manager = new RequestManager(this);
        manager.getNewsHeadlines(listener, "sports", null); // sports came from the newsApi.org page

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.info);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.logout:
                        Toast toast = Toast.makeText(getApplicationContext(),"Signing out...",Toast.LENGTH_LONG);
                        toast.show();
                        FirebaseFirestore database = FirebaseFirestore.getInstance();
                        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_USER_ID));
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
                        startActivity(new Intent(getApplicationContext(), SportsTeamsMainPage.class));
                        overridePendingTransition(0, 0);
                        return true;

                    // right now it directs to news and it works
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), HomePage.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

    }

    private final OnFetchDataListener<NewsApiResponse> listener = new OnFetchDataListener<NewsApiResponse>() {
        @Override
        public void onFetchData(List<NewsHeadlines> list, String message) {
            showNews(list);

            // dismiss dialog after adding news in recyclerView
            dialog.dismiss();
        }

        @Override
        public void onError(String message) {

        }
    };

    private void showNews(List<NewsHeadlines> list) {
        recyclerView = findViewById(R.id.recycler_main);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        adapter = new CustomAdapter(this, list, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void OnNewsClicked(NewsHeadlines headlines) {
        startActivity(new Intent(Newsfeed.this, DetailsActivity.class)
                .putExtra("data", headlines));
    }
}