package com.example.creatinguser.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.creatinguser.HomePage;
import com.example.creatinguser.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddPost extends AppCompatActivity {
    ImageView img;
    Uri imageUri;
    EditText caption;
    StorageReference postImages;
    ProgressDialog progressDialog;
    String downloadUrl;
    private byte[] myData;
    String currentDate;
    String currentTime, key;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    String currentUserId, userfromDb;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);


        img = findViewById(R.id.postImg);
        caption = findViewById(R.id.addCaption);
        Button addBtn = findViewById(R.id.addPostBtn);

        progressDialog  = new ProgressDialog(this);
        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        postImages = FirebaseStorage.getInstance().getReference().child("PostImages");

        Intent intent = getIntent();
        key = intent.getStringExtra("KEY");

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkAndRequestPermissions(AddPost.this)){
                    chooseImage(AddPost.this);
                }
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postImage();
                addBtn.setEnabled(false);
            }
        });

        getCurrentUsername();
    }

    private void chooseImage(Context context){

        final CharSequence[] optionsMenu = {"Choose from Gallery", "Exit" }; // create a menuOption Array

        // create a dialog for showing the optionsMenu

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // set the items in builder

        builder.setItems(optionsMenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(optionsMenu[i].equals("Take Photo")){

                    // Open the camera and get the photo

                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);
                }
                else if(optionsMenu[i].equals("Choose from Gallery")){

                    // choose from  external storage

                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 1);

                }
                else if (optionsMenu[i].equals("Exit")) {
                    dialogInterface.dismiss();
                }

            }
        });
        builder.show();
    }


    // function to check permission

    public static boolean checkAndRequestPermissions(final Activity context) {
        int WExtstorePermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int cameraPermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (WExtstorePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded
                    .add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(context, listPermissionsNeeded
                            .toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS:
                if (ContextCompat.checkSelfPermission(AddPost.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Camara.", Toast.LENGTH_SHORT)
                            .show();

                } else if (ContextCompat.checkSelfPermission(AddPost.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Your Storage.",
                            Toast.LENGTH_SHORT).show();

                } else {
                    chooseImage(AddPost.this);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        imageUri = data.getData();
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        img.setImageBitmap(selectedImage);
                        Bitmap bitmap2 = null;
                        try {
                            bitmap2 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Not found", Toast.LENGTH_LONG).show();
                        }
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap2.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                        myData = baos.toByteArray();
                    }
                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        imageUri = data.getData();
                        Bitmap bitmap2 = null;
                        try {
                            bitmap2 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Not found", Toast.LENGTH_LONG).show();
                        }
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap2.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                        myData = baos.toByteArray();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                img.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                cursor.close();
                            }
                        }

                    }
                    break;
            }
        }
    }

    private void getCurrentUsername(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid().toString();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        final DocumentReference docRef = db.collection("Profile").document(currentUserId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if(documentSnapshot.exists()){
                    userfromDb = documentSnapshot.getData().get("username").toString();
                }else{
                    userfromDb = "Unknown";
                }

            }
        });
    }

    private void postImage(){
        if(imageUri == null){
            Toast toast = Toast.makeText(getApplicationContext(),"Please choose a photo", Toast.LENGTH_LONG);
            toast.show();
            progressDialog.dismiss();
            return;
        }

//        if(username.getText().toString().isEmpty() || caption.getText().toString().isEmpty()){
//            Toast toast = Toast.makeText(getApplicationContext(),"Please fill all the fields", Toast.LENGTH_LONG);
//            toast.show();
//            progressDialog.dismiss();
//            return;
//        }

        String cap = caption.getText().toString().trim();
        String datime = currentTime + ","+ currentDate;
        progressDialog.setTitle("Adding post...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        long randomTime = System.currentTimeMillis();
        String random = String.valueOf(randomTime);
        final StorageReference filepath = postImages.child(random);
        final UploadTask uploadTask = filepath.putBytes(myData);
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                DecimalFormat precision = new DecimalFormat("0.00");
                String prog = precision.format(progress);
                progressDialog.setMessage("Upload is " + prog + "% done");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.getMessage();
                Toast toast = Toast.makeText(getApplicationContext(),"Error: this"+ message, Toast.LENGTH_LONG);
                toast.show();
                progressDialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();

                        }
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            downloadUrl = task.getResult().toString();
                            final FirebaseFirestore db = FirebaseFirestore.getInstance();

                            Map<String, Object> map = new HashMap<>();
                            map.put("username", userfromDb);
                            map.put("caption",cap);
                            map.put("image", downloadUrl);
                            map.put("datetime", datime);
                            map.put("post_id", key);

                            db.collection("posts")
                                    .add(map)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            progressDialog.dismiss();
                                            Toast toast = Toast.makeText(getApplicationContext(), "Post added successfully", Toast.LENGTH_LONG);

                                            toast.show();
                                            Intent mainIntent = new Intent(getApplicationContext(), FanPageActivity.class);
                                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(mainIntent);
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            String message = e.getMessage();
                                            Toast toast = Toast.makeText(getApplicationContext(),"Error: "+ message, Toast.LENGTH_LONG);
                                            toast.show();
                                            progressDialog.dismiss();
                                        }
                                    });

                        }else{
                            String message = task.getException().getMessage();
                            Toast toast = Toast.makeText(getApplicationContext(),"Error: "+ message, Toast.LENGTH_LONG);
                            toast.show();
                            progressDialog.dismiss();
                        }

                    }
                });

            }
        });



    }
}