package com.example.creatinguser.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.creatinguser.Models.BarPage;
import com.example.creatinguser.Models.FanPage;
import com.example.creatinguser.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class BarPageActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    Query query;
    FirebaseFirestore db;
    FloatingActionButton addBarPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_page);

        addBarPage = findViewById(R.id.floating_add_bar_page);

        getSupportActionBar().setTitle("Bar Pages");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        addBarPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(BarPageActivity.this, CreateBarPageActivity.class);
                startActivity(mainIntent);

            }
        });

        recyclerView = findViewById(R.id.bar_page_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                new LinearLayoutManager(getApplicationContext()).getOrientation());
        recyclerView.addItemDecoration(mDividerItemDecoration);

        db = FirebaseFirestore.getInstance();

        query = db.collection("BarPages");
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirestoreRecyclerOptions<BarPage> options = new FirestoreRecyclerOptions.Builder<BarPage>()
                .setQuery(query, BarPage.class)
                .build();

        FirestoreRecyclerAdapter<BarPage, BarPageActivity.BarViewHolder> firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<BarPage, BarPageActivity.BarViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull BarPageActivity.BarViewHolder holder, int position, @NonNull @NotNull BarPage model) {
                holder.setTitle(model.getTitle());
                holder.setTotalMembers(model.getTotal_members());
                holder.setButtonText(model.getUserID());
            }

            @NonNull
            @Override
            public BarPageActivity.BarViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.single_bar_page, viewGroup, false);
                return new BarPageActivity.BarViewHolder(view);
            }
        };

        firestoreRecyclerAdapter.startListening();

        recyclerView.setAdapter(firestoreRecyclerAdapter);
    }


    private static class BarViewHolder extends RecyclerView.ViewHolder{
        View mView;
        FirebaseAuth firebaseAuth;
        String currentUser;

        public BarViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;


            firebaseAuth = FirebaseAuth.getInstance();

            currentUser = firebaseAuth.getCurrentUser().getUid().toString();
        }

        public void setTitle(String title){
            TextView titleTV = mView.findViewById(R.id.single_bar_page_title);
            titleTV.setText(title);
        }

        public void setTotalMembers(int number){
            TextView caption = mView.findViewById(R.id.single_bar_page_count);
            caption.setText(String.valueOf(number) + " Member(s)");
        }

        public void setButtonText(String currentUserdb){
            Button button = mView.findViewById(R.id.single_bar_page_btn);
            if(currentUserdb.equals(currentUser)){
                button.setVisibility(View.INVISIBLE);
            }else{
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent mainIntent = new Intent(view.getContext(), DetailPages.class);
                        view.getContext().startActivity(mainIntent);
                    }
                });
            }
        }




    }


}