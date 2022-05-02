package com.example.creatinguser.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.creatinguser.Models.GroupChatMessage;
import com.example.creatinguser.R;
import com.example.creatinguser.utilities.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class CreateGroupChatActivity extends AppCompatActivity {
    EditText titleET;
    Button createBtn;
    private ProgressDialog progressDialog;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_chat);

        getSupportActionBar().setTitle("Create Group Chat");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Intent intent = getIntent();
        userId = intent.getStringExtra(Constants.KEY_USER_ID);

        progressDialog = new ProgressDialog(this);
        initializeViews();



        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createGroupChat();
            }
        });
    }

    private void initializeViews(){
        titleET = findViewById(R.id.name_group_chat);
        createBtn = findViewById(R.id.create_group_chat_button);
    }

    private void createGroupChat(){
        String title = titleET.getText().toString();

        if(TextUtils.isEmpty(title)){
            titleET.setError("PLease enter a Name for your group chat");
            return;
        }

        progressDialog.setTitle("Creating your Group Chat");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_GROUPCHAT)
                .document(userId)
                .collection("GroupMessage")
                .whereEqualTo(Constants.KEY_GROUP_CHAT_NAME,title)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()){
                                Map<String, Object> map = new HashMap<>();
                                map.put(Constants.KEY_GROUP_CHAT_NAME, title);
                                map.put(Constants.KEY_USER_ID, userId);
                                DocumentReference ref = db.collection(Constants.KEY_COLLECTION_GROUPCHAT).document(userId);
                                ref.collection("GroupMessage").add(map);
                                Intent mainIntent = new Intent(getApplicationContext(),GroupChatActivity.class);
                                mainIntent.putExtra("KEY",title);
                                startActivity(mainIntent);
                            }
                            else{
                                progressDialog.setTitle("Could not create Group Chat");
                                progressDialog.setMessage("Name already used, please use another name");
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog.show();
                                new Thread(new Runnable() {
                                    public void run() {
                                        try {
                                            Thread.sleep(4000);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        progressDialog.dismiss();
                                    }
                                }).start();
                            }
                        } else {
                        }
                    }
                });

    }
}
