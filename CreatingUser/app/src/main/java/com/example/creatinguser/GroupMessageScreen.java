package com.example.creatinguser;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.creatinguser.Models.ChatMessage;
import com.example.creatinguser.Models.FanPage;
import com.example.creatinguser.Models.GroupChatMessage;
import com.example.creatinguser.Models.User;
import com.example.creatinguser.activities.ChatActivity;
import com.example.creatinguser.activities.CreateFanPageActivity;
import com.example.creatinguser.activities.CreateGroupChatActivity;
import com.example.creatinguser.activities.GroupChatActivity;
import com.example.creatinguser.activities.GroupUsersActivity;
import com.example.creatinguser.activities.UsersActivity;
import com.example.creatinguser.adapters.RecentConversationsAdapter;
import com.example.creatinguser.databinding.ActivityDirectmessageScreenBinding;
import com.example.creatinguser.databinding.ActivityGroupchatBinding;
import com.example.creatinguser.databinding.ActivityGroupmessageScreenBinding;
import com.example.creatinguser.listeners.ConversationListener;
import com.example.creatinguser.utilities.Constants;
import com.example.creatinguser.utilities.PreferenceManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
//
public class GroupMessageScreen extends AppCompatActivity implements ConversationListener {
    private RecyclerView recyclerView;
    Query query;
    FirebaseFirestore db;
    FloatingActionButton switchToDirectMessage, createGroupMessage;
//    String currentUserID;
    FirebaseAuth firebaseAuth;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupmessage_screen);

        switchToDirectMessage = findViewById(R.id.switchToDirectMessage);
        createGroupMessage = findViewById(R.id.fabNewGroupChat);

        getSupportActionBar().setTitle("Group Messages");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
//        currentUserID = firebaseAuth.getCurrentUser().getUid().toString();

        Intent intent = getIntent();
        userId = intent.getStringExtra(Constants.KEY_USER_ID);
        System.out.println("\n \n data being passed "+userId+"\n \n ");

        createGroupMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(com.example.creatinguser.GroupMessageScreen.this, CreateGroupChatActivity.class);
                mainIntent.putExtra(Constants.KEY_USER_ID,userId);
                startActivity(mainIntent);

            }
        });

        switchToDirectMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(GroupMessageScreen.this,DirectMessageScreen.class);
                mainIntent.putExtra(Constants.KEY_USER_ID,userId);
                startActivity(mainIntent);
            }
        });

        recyclerView = findViewById(R.id.conversationsRecyclerViewforGroupChat);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                new LinearLayoutManager(getApplicationContext()).getOrientation());
        recyclerView.addItemDecoration(mDividerItemDecoration);

        db = FirebaseFirestore.getInstance();


        query = db.collection(Constants.KEY_COLLECTION_GROUPCHAT).document(userId).collection("GroupMessage");
//        if (db.collection(Constants.KEY_COLLECTION_GROUPCHAT).document(userId).collection("GroupMessage") == null){
//            db.collection(Constants.KEY_COLLECTION_GROUPCHAT).add(userId);
//            db.collection(Constants.KEY_COLLECTION_GROUPCHAT).document(userId).collection("GroupMessage");
//            query = db.collection(Constants.KEY_COLLECTION_GROUPCHAT).document(userId).collection("GroupMessage");
//        }else{
//            query = db.collection(Constants.KEY_COLLECTION_GROUPCHAT).document(userId).collection("GroupMessage");
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirestoreRecyclerOptions<GroupChatMessage> options = new FirestoreRecyclerOptions.Builder<GroupChatMessage>()
                .setQuery(query, GroupChatMessage.class)
                .build();

        FirestoreRecyclerAdapter<GroupChatMessage, GroupMessageScreen.FanViewHolder> firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<GroupChatMessage, GroupMessageScreen.FanViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull FanViewHolder holder, int position, @NonNull @NotNull GroupChatMessage model) {
//                final String key = getSnapshots().getSnapshot(position).getId();
                final String key = getSnapshots().getSnapshot(position).getString(Constants.KEY_GROUP_CHAT_NAME);
                holder.setTitle(key);
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), GroupChatActivity.class);
                        intent.putExtra("KEY", key);
                        intent.putExtra(Constants.KEY_NAME,userId);
                        startActivity(intent);
                    }
                });
            }
            @NonNull
            @Override
            public GroupMessageScreen.FanViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.single_fan_page, viewGroup, false);
                return new GroupMessageScreen.FanViewHolder(view);
            }
        };
        firestoreRecyclerAdapter.startListening();
        recyclerView.setAdapter(firestoreRecyclerAdapter);
    }
    private static class FanViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public FanViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setTitle(String title) {
            TextView titleTV = mView.findViewById(R.id.single_page_title);
            titleTV.setText(title);
        }
    }

    @Override
    public void onConversionClicked(User user) {
        Intent intent = new Intent(getApplicationContext(), GroupChatActivity.class);
        intent.putExtra(Constants.KEY_USER,user);
        startActivity(intent);
    }
}
