package com.example.creatinguser;


import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.creatinguser.activities.SignUpActivity;
import com.example.creatinguser.databinding.LoginPageBinding;
import com.example.creatinguser.utilities.Constants;
import com.example.creatinguser.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginPage extends AppCompatActivity {

    private LoginPageBinding binding;
    private PreferenceManager preferenceManager;
    private FirebaseAuth firebaseAuth;
    private String time;

    private int totalAttempts = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());
        time = preferenceManager.getString("ATTEMPT_Time");
        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            Intent intent = new Intent(getApplicationContext(), HomePage.class);
            intent.putExtra(Constants.KEY_USER_ID,preferenceManager.getString(Constants.KEY_USER_ID));
            startActivity(intent);
            finish();
        }
        binding = LoginPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners() {
        binding.tvRegister.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));
        binding.loginButton.setOnClickListener(v ->
        {
            if (isValidSignInDetails()) {
                signIn();
            }
        });
    }

    private void signIn() {
        loading(true);
        if (time != null && (Long.parseLong(time) > (System.currentTimeMillis() - 300000))) {
            binding.loginButton.setEnabled(false);
            showToast("Button still disabled, please wait for the five minute timer to complete. Resetting.");
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.loginButton.setEnabled(true);
                    totalAttempts = 2;
                }
            }, 300000);
            loading(false);
        } else {
            loading(true);
            String email = binding.inputEmail.getText().toString();
            String password = binding.loginPass.getText().toString();
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        loading(false);
                    } else if (totalAttempts == 0) {
                        binding.loginButton.setEnabled(false);
                        showToast("Disabling login for 5 minutes");
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                binding.loginButton.setEnabled(true);
                                totalAttempts = 2;
                            }
                        }, 300000);
                        preferenceManager.putString("ATTEMPT_Time", String.valueOf(System.currentTimeMillis()));

                    } else {
                        loading(false);
                        totalAttempts--;
                        Toast.makeText(LoginPage.this, "You have " + (totalAttempts + 1) + " tries left",
                                Toast.LENGTH_SHORT).show();
                        showToast("Unable to sign in");
                    }

                }
            });

            FirebaseFirestore database = FirebaseFirestore.getInstance();
            database.collection(Constants.KEY_COLLECTION_USERS)
                    .whereEqualTo(Constants.KEY_EMAIL, binding.inputEmail.getText().toString())
                    .whereEqualTo(Constants.KEY_PASSWORD, binding.loginPass.getText().toString())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null
                                && task.getResult().getDocuments().size() > 0) {
                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                            preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                            preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
//                        preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));
                            Intent intent = new Intent(getApplicationContext(), HomePage.class);
                            intent.putExtra(Constants.KEY_USER_ID,documentSnapshot.getId());
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            loading(false);
                            showToast("Unable to sign in");
                        }
                    });
        }
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.loginButton.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.loginButton.setVisibility(View.VISIBLE);
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private Boolean isValidSignInDetails() {
        if (binding.inputEmail.getText().toString().trim().isEmpty()) {
            showToast("Enter email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()) {
            showToast("Enter valid email");
            return false;
        } else if (binding.loginPass.getText().toString().trim().isEmpty()) {
            showToast("Enter password");
            return false;
        } else {
            return true;
        }
    }
}