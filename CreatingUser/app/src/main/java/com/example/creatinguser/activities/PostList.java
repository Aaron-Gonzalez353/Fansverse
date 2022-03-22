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
import android.widget.Toast;

import com.example.creatinguser.Models.Post;
import com.example.creatinguser.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostList extends AppCompatActivity {
    private RecyclerView recyclerView;
    Query query;
    FirebaseFirestore db;
    String key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);
        FloatingActionButton fab = findViewById(R.id.fab);

        getSupportActionBar().setTitle("Posts");


        recyclerView = findViewById(R.id.postRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                new LinearLayoutManager(getApplicationContext()).getOrientation());
        recyclerView.addItemDecoration(mDividerItemDecoration);

        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        key = intent.getStringExtra("KEY");

        CollectionReference postsRef= db.collection("posts");
        query =  postsRef.whereEqualTo("post_id", key);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent postintent = new Intent(getApplicationContext(), AddPost.class);
                postintent.putExtra("KEY",key);
                startActivity(postintent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class)
                .build();

        FirestoreRecyclerAdapter<Post, PostViewHolder> firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<Post, PostViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull PostViewHolder holder, int position, @NonNull @NotNull Post model) {
                final String  key = getSnapshots().getSnapshot(position).getId();

                holder.setCaption(model.getCaption());
                holder.setDatetime(model.getDatetime());
                holder.setUsername(model.getUsername());
                holder.setPostImage(model.getImage());


                holder.setLikeButtonStatus(key);

                holder.like_btn.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       FirebaseFirestore db2 = FirebaseFirestore.getInstance();
                       DocumentReference likesRef = db2.collection("PostLikes").document(key);
                       FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                       String currentUserId = firebaseAuth.getCurrentUser().getUid().toString();
                       if(holder.likeChecker){
                           likesRef.update(currentUserId, FieldValue.delete());
                           holder.likeChecker = false;
                       }else{
                           Map<String, Object> data = new HashMap<>();
                           data.put(currentUserId, true);
                           likesRef.set(data, SetOptions.merge());
                           holder.likeChecker = true;
                       }

                   }
                });

                holder.comment_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent mainIntent = new Intent(getApplicationContext(), CommentsList.class);
                        mainIntent.putExtra("POST_ID",key);
                        startActivity(mainIntent);
                    }
                });
            }

            @NonNull
            @Override
            public PostList.PostViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.single_post, viewGroup, false);
                return new PostViewHolder(view);
            }
        };

        firestoreRecyclerAdapter.startListening();

        recyclerView.setAdapter(firestoreRecyclerAdapter);


    }

    private static class PostViewHolder extends RecyclerView.ViewHolder{
        View mView;
        ImageButton like_btn;
        Boolean likeChecker = false;
        ImageButton comment_button;
        TextView countNum;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            like_btn = mView.findViewById(R.id.like_but);
            comment_button = mView.findViewById(R.id.comment_button);
            countNum = mView.findViewById(R.id.like_count);

        }

        public void setLikeButtonStatus(String postKey){
            FirebaseFirestore db2 = FirebaseFirestore.getInstance();
            CollectionReference likesRef = db2.collection("PostLikes");
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            String currentUserId = firebaseAuth.getCurrentUser().getUid().toString();

            likesRef.document(postKey).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(value.exists()){
                        int likes = value.getData().size();
                        String likesStr = String.valueOf(likes);
                        countNum.setText(likesStr +" Likes");
                        if(value.getData().get(currentUserId) != null){
                            like_btn.setImageResource(R.drawable.liked);
                            likeChecker = true;
                        }else {
                            like_btn.setImageResource(R.drawable.like);
                        }
                    }
                }
            });

        }

        public void setUsername(String name){
            TextView username = mView.findViewById(R.id.username);
            username.setText(name);
        }

        public void setCaption(String cap){
            TextView caption = mView.findViewById(R.id.caption);
            caption.setText(cap);
        }

        public void setPostImage(String post_url){
            ImageView imageView = mView.findViewById(R.id.image);
            Picasso.get().load(post_url).fit().centerCrop().into(imageView);

        }

        public void setDatetime(String time){
            TextView tim = mView.findViewById(R.id.datetime);
            tim.setText(time);
        }


    }
}