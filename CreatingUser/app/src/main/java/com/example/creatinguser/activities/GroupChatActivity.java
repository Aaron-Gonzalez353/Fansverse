package com.example.creatinguser.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.creatinguser.HomePage;
import com.example.creatinguser.LoginPage;
import com.example.creatinguser.Models.GroupChatMessage;
import com.example.creatinguser.R;
import com.example.creatinguser.utilities.Constants;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChatActivity extends AppCompatActivity {
    private EditText userMessage;
    private ImageButton sendMessageBtn;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    String currentUserID;
    BottomNavigationView bottomNavigationView;
    FirebaseAuth firebaseAuth;
    String userfromDb;
    String chat_image_url;
    String key, currentName;
    Query query;
    FirebaseFirestore db;
    String currentDate;
    String currentTime;

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupchat);

        Intent intent = getIntent();
        key = intent.getStringExtra("KEY");
        currentName = intent.getStringExtra(Constants.KEY_NAME);
        getSupportActionBar().setTitle(key);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        currentTime = new SimpleDateFormat("HH:mm aa", Locale.getDefault()).format(new Date());

        userMessage = findViewById(R.id.group_activity_message_input);
        sendMessageBtn = findViewById(R.id.group_activity_send_button);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserID = firebaseAuth.getCurrentUser().getUid().toString();

        getCurrentUsername();
        db = FirebaseFirestore.getInstance();

        CollectionReference chatsRef= db.collection("GroupChatMessageCont");
        query =  chatsRef.whereEqualTo(Constants.KEY_GROUP_CHAT_NAME, key).orderBy("timestamp", Query.Direction.ASCENDING);


        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
       @Override
       public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
           Intent intent = new Intent(getApplicationContext(), FriendsListGroupChat.class);
           intent.putExtra("KEY", key);
           startActivity(intent);

           return true;
       }
   });
        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });


        recyclerView = findViewById(R.id.group_activity_recyclerView);
//        recyclerView.hasFixedSize();
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        userMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length()==0){
                    sendMessageBtn.setEnabled(false);
                    sendMessageBtn.setVisibility(View.GONE);

                } else {
                    sendMessageBtn.setEnabled(true);
                    sendMessageBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        FirestoreRecyclerOptions<GroupChatMessage> options = new FirestoreRecyclerOptions.Builder<GroupChatMessage>()
                .setQuery(query, GroupChatMessage.class)
                .build();

        FirestoreRecyclerAdapter<GroupChatMessage, com.example.creatinguser.activities.GroupChatActivity.MessagesViewHolder> firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<GroupChatMessage, com.example.creatinguser.activities.GroupChatActivity.MessagesViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull com.example.creatinguser.activities.GroupChatActivity.MessagesViewHolder holder, int position, @NonNull @NotNull GroupChatMessage model) {
                final String  key = getSnapshots().getSnapshot(position).getId();

                holder.setMessage(model.getChat_message());
                holder.setMessageTime(model.getChat_time());
                holder.setSenderImage(model.getChat_image());
                holder.setSenderUsername(model.getChat_username());

            }

            @NonNull
            @Override
            public com.example.creatinguser.activities.GroupChatActivity.MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view;
                if(i == MSG_TYPE_RIGHT){
                    view  = LayoutInflater.from(viewGroup.getContext())
                            .inflate(R.layout.message_layout_sender, viewGroup, false);
                }else{
                    view = LayoutInflater.from(viewGroup.getContext())
                            .inflate(R.layout.message_layout_receiver, viewGroup, false);
                }
                return new com.example.creatinguser.activities.GroupChatActivity.MessagesViewHolder(view);
            }

            @Override
            public int getItemViewType(int position) {
                if(getItem(position).getUser_id().equals(currentUserID)){
                    return MSG_TYPE_RIGHT;
                }else{
                    return  MSG_TYPE_LEFT;
                }
            }
        };

        firestoreRecyclerAdapter.startListening();

        firestoreRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.scrollToPosition(firestoreRecyclerAdapter.getItemCount() - 1);
            }
        });
        recyclerView.setAdapter(firestoreRecyclerAdapter);
    }

    private static class MessagesViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public MessagesViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        private void setSenderImage(String image){
            CircleImageView circleImageView = mView.findViewById(R.id.message_sender_image);
            Picasso.get().load(image).into(circleImageView);
        }

        private void setMessage(String text){
            TextView senderText = mView.findViewById(R.id.message_sender_text);
            senderText.setText(text);
        }


        private void setMessageTime(String time){
            TextView senderTime = mView.findViewById(R.id.message_sender_time);
            senderTime.setText(time);
        }

        private void setSenderUsername(String name){
            TextView senderName = mView.findViewById(R.id.message_sender_username);
            senderName.setText(name);
        }


    }

    private void getCurrentUsername(){
        currentUserID = firebaseAuth.getCurrentUser().getUid().toString();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        final DocumentReference docRef = db.collection("Profile").document(currentUserID);
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

    public void sendMessage(){
        String message = userMessage.getText().toString();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> map = new HashMap<>();
        map.put("chat_image", chat_image_url);
        map.put("chat_message", message);
        map.put("chat_time", currentTime);
        map.put("chat_date", currentDate);
        map.put("chat_username",userfromDb);
        map.put(Constants.KEY_GROUP_CHAT_NAME,key);
        map.put("timestamp", FieldValue.serverTimestamp());
        map.put("user_id", currentUserID);
        db.collection("GroupChatMessageCont").add(map);
        userMessage.setText("");
    }

}