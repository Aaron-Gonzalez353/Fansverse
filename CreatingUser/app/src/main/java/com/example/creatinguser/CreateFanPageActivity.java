package com.example.creatinguser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateFanPageActivity extends AppCompatActivity {
    EditText titleET, descriptionET;
    Toolbar toolbar;
    Button createBtn;
    private ProgressDialog progressDialog;
    private String currentUserID;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_fan_page);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserID = firebaseAuth.getCurrentUser().getUid().toString();
        initializeViews();

        toolbar.setTitle("Create Page");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(CreateFanPageActivity.this, ProfileActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainIntent);
            }
        });


        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createFanPage();
            }
        });
    }

    private void initializeViews(){
        titleET = findViewById(R.id.create_fan_title);
        descriptionET = findViewById(R.id.create_fan_description);
        toolbar = findViewById(R.id.create_fan_toolbar);
        createBtn = findViewById(R.id.create_fan_button);
    }

    private void createFanPage(){
        String title = titleET.getText().toString();
        String description = descriptionET.getText().toString();

        if(TextUtils.isEmpty(title)){
            titleET.setError("PLease enter a title");
            return;
        }
        if(TextUtils.isEmpty(description)){
            descriptionET.setError("PLease enter a description");
            return;
        }

        progressDialog.setTitle("Creating your page");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("description",description);
        map.put("total_members", 1);
        map.put("userID", currentUserID);


        db.collection("FanPages")
                .add(map)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        progressDialog.dismiss();
                        Toast toast = Toast.makeText(getApplicationContext(), "Page Created successfully", Toast.LENGTH_LONG);
                        toast.show();
                        Intent mainIntent = new Intent(getApplicationContext(), FanPageActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.getMessage();
                Toast toast = Toast.makeText(getApplicationContext(),"Error: "+ message, Toast.LENGTH_LONG);
                toast.show();
                progressDialog.dismiss();
            }
        });

    }



}