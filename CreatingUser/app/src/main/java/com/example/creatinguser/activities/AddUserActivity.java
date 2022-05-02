package com.example.creatinguser.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.creatinguser.Models.User;
import com.example.creatinguser.adapters.UsersAdapter;
import com.example.creatinguser.databinding.ActivityUsersBinding;
import com.example.creatinguser.listeners.UserListener;
import com.example.creatinguser.utilities.Constants;
import com.example.creatinguser.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AddUserActivity extends AppCompatActivity implements UserListener {

    private ActivityUsersBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        getUsers();
    }
    private void getUsers(){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<User> users = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (currentUserId.equals(queryDocumentSnapshot.getId())) {
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
//                        if (users.size() > 0){
//                            UsersAdapter usersAdapter = new UsersAdapter(users,this);
//                            binding.usersRecyclerView.setAdapter(usersAdapter);
//                            binding.usersRecyclerView.setVisibility(View.VISIBLE);
//                        }
//                        else {
//                            com.example.creatinguser.activities.UsersActivity.showErrorMessage();
//                        }
//                    }else{
//                        showErrorMessage();
//                    }
//                });
                    }
                });
    }
    @Override
    public void onUserClicked(User user) {
        Intent intent = new Intent(getApplicationContext(), GroupChatActivity.class);
        intent.putExtra(Constants.KEY_USER,user);
        startActivity(intent);
        finish();
    }
}
