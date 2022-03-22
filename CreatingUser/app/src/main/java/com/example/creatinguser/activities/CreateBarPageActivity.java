package com.example.creatinguser.activities;

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

import com.example.creatinguser.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateBarPageActivity extends AppCompatActivity {
    EditText titleET, descriptionET;
    Toolbar toolbar;
    Button createBtn;
    private ProgressDialog progressDialog;
    private String currentUserID;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_bar_page);

        getSupportActionBar().setTitle("Create Bar Page");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserID = firebaseAuth.getCurrentUser().getUid().toString();

        initializeViews();

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createBarPage();
            }
        });
    }

    private void initializeViews(){
        titleET = findViewById(R.id.create_bar_title);
        descriptionET = findViewById(R.id.create_bar_description);
        createBtn = findViewById(R.id.create_bar_button);
    }

    private void createBarPage(){
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


        db.collection("BarPages")
                .add(map)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        progressDialog.dismiss();
                        Toast toast = Toast.makeText(getApplicationContext(), "Page Created successfully", Toast.LENGTH_LONG);
                        toast.show();
                        Intent mainIntent = new Intent(getApplicationContext(), BarPageActivity.class);
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