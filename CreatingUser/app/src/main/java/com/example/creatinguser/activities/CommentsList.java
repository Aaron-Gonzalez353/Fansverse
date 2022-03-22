package com.example.creatinguser.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.creatinguser.Models.Comments;
import com.example.creatinguser.Models.Post;
import com.example.creatinguser.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class CommentsList extends AppCompatActivity {
    private RecyclerView recyclerView;
    Query query;
    FirebaseFirestore db;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_list);

        FloatingActionButton fab = findViewById(R.id.comment_fab);

        getSupportActionBar().setTitle("Comments");


        recyclerView = findViewById(R.id.commentRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                new LinearLayoutManager(getApplicationContext()).getOrientation());
        recyclerView.addItemDecoration(mDividerItemDecoration);

        Intent intent = getIntent();
        key = intent.getStringExtra("POST_ID");

        db = FirebaseFirestore.getInstance();

        CollectionReference chatsRef= db.collection("PostComments");
        query =  chatsRef.whereEqualTo("post_id", key);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent postintent = new Intent(getApplicationContext(), AddCommentActivity.class);
                postintent.putExtra("POST_ID",key);
                startActivity(postintent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirestoreRecyclerOptions<Comments> options = new FirestoreRecyclerOptions.Builder<Comments>()
                .setQuery(query, Comments.class)
                .build();

        FirestoreRecyclerAdapter<Comments, CommentViewHolder> firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<Comments, CommentViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull CommentViewHolder holder, int position, @NonNull @NotNull Comments model) {
                holder.setUsername(model.getUsername());
                holder.setComment(model.getComment());
                holder.setDatetime(model.getCurrentDate(), model.getCurrentTime());

            }

            @NonNull
            @Override
            public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.single_comment, viewGroup, false);
                return new CommentViewHolder(view);
            }
        };

        firestoreRecyclerAdapter.startListening();

        recyclerView.setAdapter(firestoreRecyclerAdapter);



    }

    private static class CommentViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

        }

        public void setUsername(String name){
            TextView username = mView.findViewById(R.id.comment_username);
            username.setText(name);
        }



        public void setComment(String comment){
           TextView com = mView.findViewById(R.id.comment_text);
           com.setText(comment);

        }

        public void setDatetime(String date, String time){
            TextView tim = mView.findViewById(R.id.comment_date);
            tim.setText("Posted on "+date+" at "+time);
        }


    }

}