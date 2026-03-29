package com.example.smartvillageapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class LocationsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    LocationAdapter adapter;
    List<LocationModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);

        recyclerView = findViewById(R.id.location_recycler);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapter = new LocationAdapter(this, list);
        recyclerView.setAdapter(adapter);

        loadLocations();
    }

    private void loadLocations() {

        FirebaseFirestore.getInstance()
                .collection("locations")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    list.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        LocationModel model = doc.toObject(LocationModel.class);
                        list.add(model);
                    }

                    adapter.notifyDataSetChanged();
                });
    }
}