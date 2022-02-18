package com.example.creatinguser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class FanPageActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    Query query;
    Toolbar toolbar;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fan_page);

        toolbar = findViewById(R.id.fan_page_toolbar);



        toolbar.setTitle("Fan Pages");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(FanPageActivity.this, HomePage.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainIntent);
            }
        });


        recyclerView = findViewById(R.id.fan_page_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                new LinearLayoutManager(getApplicationContext()).getOrientation());
        recyclerView.addItemDecoration(mDividerItemDecoration);

        db = FirebaseFirestore.getInstance();

        query = db.collection("FanPages");


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirestoreRecyclerOptions<FanPage> options = new FirestoreRecyclerOptions.Builder<FanPage>()
                .setQuery(query, FanPage.class)
                .build();

        FirestoreRecyclerAdapter<FanPage, FanViewHolder> firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<FanPage, FanViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull FanViewHolder holder, int position, @NonNull @NotNull FanPage model) {
                holder.setTitle(model.getTitle());
                holder.setTotalMembers(model.getTotal_members());
                holder.setButtonText(model.getUserID());
            }

            @NonNull
            @Override
            public FanPageActivity.FanViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.single_fan_page, viewGroup, false);
                return new FanViewHolder(view);
            }
        };

        firestoreRecyclerAdapter.startListening();

        recyclerView.setAdapter(firestoreRecyclerAdapter);


    }

    private static class FanViewHolder extends RecyclerView.ViewHolder{
        View mView;
        FirebaseAuth firebaseAuth;
        String currentUser;

        public FanViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;


            firebaseAuth = FirebaseAuth.getInstance();

            currentUser = firebaseAuth.getCurrentUser().getUid().toString();
        }

        public void setTitle(String title){
            TextView titleTV = mView.findViewById(R.id.single_page_title);
            titleTV.setText(title);
        }

        public void setTotalMembers(int number){
            TextView caption = mView.findViewById(R.id.single_page_count);
            caption.setText(String.valueOf(number) + " Member(s)");
        }

        public void setButtonText(String currentUserdb){
            Button button = mView.findViewById(R.id.single_page_btn);
            if(currentUserdb.equals(currentUser)){
                button.setVisibility(View.INVISIBLE);
            }
        }




    }

}