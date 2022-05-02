package com.example.creatinguser.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.creatinguser.Models.User;
import com.example.creatinguser.adapters.UsersAdapter;
import com.example.creatinguser.databinding.ActivityFriendsListGroupChatBinding;
import com.example.creatinguser.listeners.UserListener;
import com.example.creatinguser.utilities.Constants;
import com.example.creatinguser.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendsListGroupChat extends AppCompatActivity implements UserListener {

    private ActivityFriendsListGroupChatBinding binding;
    private PreferenceManager preferenceManager;
    FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFriendsListGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());
        Intent intent = getIntent();
        key = intent.getStringExtra("KEY");
        System.out.println("\n \n Data being pulled in friendlistgroupchat.java"+key +"\n \n");
        setListeners();
        getUsers();
    }
    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }

    private void getUsers(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if(task.isSuccessful() && task.getResult() != null){
                        List<User> users = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (currentUserId.equals(queryDocumentSnapshot.getId())){
                                continue;
                            }
                            User user = new User();
                            user.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                            user.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                            user.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                            user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                            user.id = queryDocumentSnapshot.getId();
                            users.add(user);
                        }
                        if (users.size() > 0){
                            UsersAdapter usersAdapter = new UsersAdapter(users,this);
                            binding.usersRecyclerView.setAdapter(usersAdapter);
                            binding.usersRecyclerView.setVisibility(View.VISIBLE);
                        }else {
                            showErrorMessage();
                        }
                    }else{
                        showErrorMessage();
                    }
                });
    }
    private void showErrorMessage(){
        binding.textErrorMessage.setText(String.format("%s","No user available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    private void loading(Boolean isLoading){
        if (isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    public void onUserClicked(User user) {
        System.out.println("\n \n Clicking on user"+user+"\n \n id"+user.id+"\n \n name"+user.name);
        db.collection(Constants.KEY_COLLECTION_GROUPCHAT)
                .document(user.id)
                .collection("GroupMessage")
                .whereEqualTo(Constants.KEY_GROUP_CHAT_NAME,key)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()){
                                Map<String, Object> map = new HashMap<>();
                                map.put(Constants.KEY_GROUP_CHAT_NAME, key);
                                map.put(Constants.KEY_USER_ID, user.id);
                                DocumentReference ref = db.collection(Constants.KEY_COLLECTION_GROUPCHAT).document(user.id);
                                ref.collection("GroupMessage").add(map);
                                Intent mainIntent = new Intent(getApplicationContext(),GroupChatActivity.class);
                                mainIntent.putExtra("KEY",key);
                                startActivity(mainIntent);
                                finish();
                            }
                            else{
                                Intent mainIntent = new Intent(getApplicationContext(),GroupChatActivity.class);
                                mainIntent.putExtra("KEY",key);
                                startActivity(mainIntent);
                                finish();
                            }
                        } else {
                        }
                    }
                });
//        System.out.println("\n \n "+currentUserID);
//        Map<String, Object> map = new HashMap<>();
//        map.put(Constants.KEY_GROUP_CHAT_NAME, key);
//        map.put(Constants.KEY_USER_ID, user.id);
////        db.collection("TestingSent").add(map);

//        DocumentReference ref = db.collection(Constants.KEY_COLLECTION_GROUPCHAT).document(user.id);
//        ref.collection("GroupMessage").add(map);
//        Intent mainIntent = new Intent(getApplicationContext(),GroupChatActivity.class);
//        mainIntent.putExtra("KEY",key);
//        startActivity(mainIntent);
//        finish();
    }
}