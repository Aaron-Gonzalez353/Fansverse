package com.example.creatinguser.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.creatinguser.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class DetailPages extends AppCompatActivity {
    private TextView textTitle, textDescription,textMembers;
    private Button chat, posts, join;
    String key;
    String currentUserID;
    FirebaseAuth firebaseAuth;
    int numbers_of_members;
    private ProgressDialog progressDialog;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pages);

        getSupportActionBar().setTitle("Page Detail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        progressDialog = new ProgressDialog(this);

        textTitle = findViewById(R.id.detail_page_title);
        textDescription = findViewById(R.id.detail_page_description);
        textMembers = findViewById(R.id.detail_page_members);

        chat = findViewById(R.id.fan_page_chat_room);
        join = findViewById(R.id.fan_page_join);
        posts = findViewById(R.id.fan_page_posts);

        firebaseAuth = FirebaseAuth.getInstance();



        Intent intent = getIntent();
        key = intent.getStringExtra("KEY");



        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinPage();
            }
        });

        posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(getApplicationContext(), PostList.class);
                mainIntent.putExtra("KEY",key);
                startActivity(mainIntent);
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(getApplicationContext(), FanChatActivity.class);
                mainIntent.putExtra("ROOM_ID",key);
                startActivity(mainIntent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        retrievePage();
        retrieveMembers();
    }


    public void retrieveMembers(){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        currentUserID = firebaseAuth.getCurrentUser().getUid().toString();
        final DocumentReference docRef = db.collection("Page Members").document(key);

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.exists()){
                    String user = value.getData().get("userID").toString();

                    if(user.equals(currentUserID)){
                        chat.setVisibility(View.VISIBLE);
                        posts.setVisibility(View.VISIBLE);
                        join.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });


    }

    public void joinPage(){
        progressDialog.setTitle("Joining Page");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> map = new HashMap<>();
        map.put("userID", currentUserID);
        db.collection("Page Members").document(key).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    final FirebaseFirestore db = FirebaseFirestore.getInstance();


                    Map<String, Object> map1 = new HashMap<>();
                    int updated_number = numbers_of_members + 1;
                    map1.put("total_members", updated_number);
                    db.collection("FanPages").document(key).update(map1);

                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "You are now a member of this page", Toast.LENGTH_LONG).show();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Error joining.Try again later", Toast.LENGTH_LONG).show();

                }
            }
        });

    }




    public void retrievePage(){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        currentUserID = firebaseAuth.getCurrentUser().getUid().toString();
        final DocumentReference docRef = db.collection("FanPages").document(key);

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.exists()){
                    String title = value.getData().get("title").toString();
                    String members = value.getData().get("total_members").toString();
                    String desc = value.getData().get("description").toString();

                    String user = value.getData().get("userID").toString();

                    if(user.equals(currentUserID)){
                        chat.setVisibility(View.VISIBLE);
                        posts.setVisibility(View.VISIBLE);
                        join.setVisibility(View.INVISIBLE);
                    }

                    numbers_of_members = Integer.parseInt(members);

                    textTitle.setText(title);
                    textDescription.setText(desc);
                    textMembers.setText(members + "Members");
                }

            }
        });


    }
}