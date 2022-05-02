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
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
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
    String chat_image_url, userfromDb, cur_user_id;





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


        textMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(getApplicationContext(), FanMembers.class);
                mainIntent.putExtra("PAGE_ID",key);
                startActivity(mainIntent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        getCurrentUserDets();
        retrievePage();
        retrievePage2();

    }


//    public void retrieveMembers(){
//        final FirebaseFirestore db = FirebaseFirestore.getInstance();
//        currentUserID = firebaseAuth.getCurrentUser().getUid().toString();
//        final DocumentReference docRef = db.collection("Page Members").document(key);
//
//        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//                if(value.exists()){
//                    String user = value.getData().get("userID").toString();
//
//                    if(user.equals(currentUserID)){
//                        chat.setVisibility(View.VISIBLE);
//                        posts.setVisibility(View.VISIBLE);
//                        join.setVisibility(View.INVISIBLE);
//                    }
//                }
//            }
//        });
//
//
//    }


    public void joinPage(){
        progressDialog.setTitle("Joining Page");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put("userID", currentUserID);
        data.put("userImage", chat_image_url);
        data.put("username", userfromDb);
        data.put("page_id", key);

        db.collection("Page Members").add(data).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
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

    private void getCurrentUserDets(){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        cur_user_id = firebaseAuth.getCurrentUser().getUid().toString();
        final DocumentReference docRef = db.collection("Profile").document(cur_user_id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if(documentSnapshot.exists()){
                    userfromDb = documentSnapshot.getData().get("username").toString();
                    if(documentSnapshot.getData().get("imageUrl") != null){
                        chat_image_url = documentSnapshot.getData().get("imageUrl").toString();
                    }else{
                        chat_image_url = "https://firebasestorage.googleapis.com/v0/b/fanverse-7e61e.appspot.com/o/ProfileImages%2Fprofile.png?alt=media&token=dd60c0a2-b023-4f93-805f-0574a5ab6dd4";
                    }
                }else{
                    userfromDb = "Unknown";
                }

            }
        });
    }


    public void retrievePage2() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        currentUserID = firebaseAuth.getCurrentUser().getUid().toString();
        db.collection("Page Members")
                .whereEqualTo("page_id", key)
                .whereEqualTo("userID", currentUserID)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(value.size() == 0){
                            chat.setVisibility(View.INVISIBLE);
                            posts.setVisibility(View.INVISIBLE);
                            join.setVisibility(View.VISIBLE);
                        }else{
                            chat.setVisibility(View.VISIBLE);
                            posts.setVisibility(View.VISIBLE);
                            join.setVisibility(View.INVISIBLE);
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

//                    if(user.equals(currentUserID)){
//                        chat.setVisibility(View.VISIBLE);
//                        posts.setVisibility(View.VISIBLE);
//                        join.setVisibility(View.INVISIBLE);
//                    }


                    numbers_of_members = Integer.parseInt(members);

                    textTitle.setText(title);
                    textDescription.setText(desc);
                    textMembers.setText(members + "Members");
                }

            }
        });


    }
}