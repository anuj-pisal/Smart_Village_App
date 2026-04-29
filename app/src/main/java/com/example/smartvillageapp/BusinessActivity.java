package com.example.smartvillageapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class BusinessActivity extends BaseActivity {

    RecyclerView recyclerView;
    BusinessAdapter adapter;
    List<BusinessModel> businessList;
    FirebaseFirestore db;
    View emptyStateLayout;
    TextView emptyStateMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business);

        recyclerView = findViewById(R.id.businessRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        emptyStateLayout = findViewById(R.id.empty_state_layout);
        emptyStateMsg = findViewById(R.id.empty_state_message);
        emptyStateMsg.setText(getString(R.string.empty_businesses));

        businessList = new ArrayList<>();
        adapter = new BusinessAdapter(this, businessList);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.addBusinessBtn);

        fab.setOnClickListener(v -> {
            startActivity(new Intent(this, AddBusinessActivity.class));
        });

        db = FirebaseFirestore.getInstance();

        loadBusinesses();
    }

    private void toggleEmptyState(int size) {
        if (size == 0) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void loadBusinesses() {

        db.collection("businesses")
                .orderBy("createdAt")
                .limit(20)   // VERY IMPORTANT (cost control)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(this,
                                "Failed to load businesses",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (queryDocumentSnapshots != null) {
                        businessList.clear();

                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            BusinessModel business = doc.toObject(BusinessModel.class);
                            businessList.add(business);
                        }

                        adapter.notifyDataSetChanged();
                        toggleEmptyState(businessList.size());
                    }
                });
    }
}
