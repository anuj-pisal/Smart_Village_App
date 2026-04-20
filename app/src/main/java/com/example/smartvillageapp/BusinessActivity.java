package com.example.smartvillageapp;

import android.content.Intent;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business);

        recyclerView = findViewById(R.id.businessRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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

    private void loadBusinesses() {

        db.collection("businesses")
                .orderBy("createdAt")
                .limit(20)   // VERY IMPORTANT (cost control)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    businessList.clear();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        BusinessModel business = doc.toObject(BusinessModel.class);
                        businessList.add(business);
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Failed to load businesses",
                                Toast.LENGTH_SHORT).show());
    }
}
