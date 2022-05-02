package com.example.creatinguser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.creatinguser.activities.ChatActivity;
import com.example.creatinguser.activities.GroupChatActivity;
import com.example.creatinguser.activities.GroupUsersActivity;
import com.example.creatinguser.activities.UsersActivity;
import com.example.creatinguser.adapters.RecentConversationsAdapter;
import com.example.creatinguser.Models.User;
import com.example.creatinguser.databinding.ActivityDirectmessageScreenBinding;
import com.example.creatinguser.utilities.Constants;
import com.example.creatinguser.utilities.PreferenceManager;
import com.example.creatinguser.listeners.ConversationListener;
import com.example.creatinguser.Models.ChatMessage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DirectMessageScreen extends AppCompatActivity implements ConversationListener {

    private ActivityDirectmessageScreenBinding binding;
    private PreferenceManager preferenceManager;
    private List<ChatMessage> conversations;
    private RecentConversationsAdapter conversationsAdapter;
    private FirebaseFirestore database;
    String userId;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDirectmessageScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        //bottomNavigationView.setSelectedItemId(R.id.info);
        Intent intent = getIntent();
        userId = intent.getStringExtra(Constants.KEY_USER_ID);

        init();
//        loadUserDetails();
        getToken();
        setListeners();
        listenConversations();

        //bottom nav bar
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

    private void init() {
        conversations = new ArrayList<>();
        conversationsAdapter = new RecentConversationsAdapter(conversations, this);
        binding.conversationsRecyclerViewforDirectMessage.setAdapter(conversationsAdapter);
        database = FirebaseFirestore.getInstance();
    }

    private void setListeners() {
        binding.fabNewChat.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), UsersActivity.class)));
        binding.fabNewGroupChat.setOnClickListener(v -> newGroupChatMessage()
        );
    }

//    private void loadUserDetails(){
//        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
//        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE),Base64.DEFAULT);
//        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
//        binding.imageProfile.setImageBitmap(bitmap);
//    }

    private void newGroupChatMessage(){
        Intent intent = new Intent(getApplicationContext(), GroupMessageScreen.class);
        intent.putExtra(Constants.KEY_USER_ID,userId);
        startActivity(intent);
    }
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void listenConversations() {
        database.collection(Constants.KEY_COLLECTION_DIRECTCONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_DIRECTCONVERSATIONS)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId = senderId;
                    chatMessage.receiverId = receiverId;
                    if (preferenceManager.getString(Constants.KEY_USER_ID).equals(senderId)) {
//                        chatMessage.conversionImage = documentChange.getDocument().getString(Constants.KEY_RECEIVER_IMAGE);
                        chatMessage.conversionName = documentChange.getDocument().getString(Constants.KEY_RECEIVER_NAME);
                        chatMessage.conversionId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    } else {
//                        chatMessage.conversionImage = documentChange.getDocument().getString(Constants.KEY_SENDER_IMAGE);
                        chatMessage.conversionName = documentChange.getDocument().getString(Constants.KEY_SENDER_NAME);
                        chatMessage.conversionId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    }
                    chatMessage.message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                    chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    conversations.add(chatMessage);
                } else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                    for (int i = 0; i < conversations.size(); i++) {
                        String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                        String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                        if (conversations.get(i).senderId.equals(senderId) && conversations.get(i).receiverId.equals(receiverId)) {
                            conversations.get(i).message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                            conversations.get(i).dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                            break;
                        }
                    }
                }
            }
            Collections.sort(conversations, (obj1, obj2) -> obj2.dateObject.compareTo(obj1.dateObject));
            conversationsAdapter.notifyDataSetChanged();
            binding.conversationsRecyclerViewforDirectMessage.smoothScrollToPosition(0);
            binding.conversationsRecyclerViewforDirectMessage.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        }
    };

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnFailureListener(e -> showToast("Unable to update token"));
    }


    @Override
    public void onConversionClicked(User user) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER,user);
        startActivity(intent);
    }
}