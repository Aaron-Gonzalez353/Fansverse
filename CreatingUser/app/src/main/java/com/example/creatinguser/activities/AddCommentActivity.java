package com.example.creatinguser.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.creatinguser.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddCommentActivity extends AppCompatActivity {
    EditText comment;
    Button add;

    String userImage, userfromDb, currentUserID;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    String key, currentDate, currentTime;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);

        getSupportActionBar().setTitle("Add Comment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        comment = findViewById(R.id.create_comment_edit);
        add = findViewById(R.id.create_comment_button);

        firebaseAuth = FirebaseAuth.getInstance();

        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        Intent intent = getIntent();
        key = intent.getStringExtra("POST_ID");

        progressDialog = new ProgressDialog(this);
        getCurrentUsername();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addComment();
            }
        });

    }

    private void addComment(){
        String commentText = comment.getText().toString();

        if(commentText.isEmpty()){
            Toast.makeText(getApplicationContext(), "Please enter a comment", Toast.LENGTH_LONG).show();
        }else{
            progressDialog.setTitle("Adding Comment...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String, Object> map = new HashMap<>();
            map.put("comment", commentText);
            map.put("username", userfromDb);
            map.put("post_id", key);
            map.put("currentDate", currentDate);
            map.put("currentTime",currentTime);

            db.collection("PostComments").add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if(task.isSuccessful()){
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Comment Added Successful", Toast.LENGTH_LONG).show();
                        Intent mainIntent = new Intent(getApplicationContext(), PostList.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Error adding the comment", Toast.LENGTH_LONG).show();
                    }
                }
            });


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
                        userImage = documentSnapshot.getData().get("imageUrl").toString();
                    }else{
                        userImage = "https://firebasestorage.googleapis.com/v0/b/fanverse-7e61e.appspot.com/o/ProfileImages%2Fprofile.png?alt=media&token=dd60c0a2-b023-4f93-805f-0574a5ab6dd4";
                    }
                }else{
                    userfromDb = "Unknown";
                }

            }
        });
    }
}