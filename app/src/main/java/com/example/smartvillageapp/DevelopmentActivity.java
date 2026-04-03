package com.example.smartvillageapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class DevelopmentActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<DevelopmentModel> list;
    DevelopmentAdapter adapter;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_development);

        recyclerView = findViewById(R.id.dev_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapter = new DevelopmentAdapter(this, list);
        recyclerView.setAdapter(adapter);

        loadData();
    }

    private void loadData() {

        FirebaseFirestore.getInstance()
                .collection("developments")
                .get()
                .addOnSuccessListener(q -> {

                    list.clear();

                    for (DocumentSnapshot d : q) {
                        DevelopmentModel m = d.toObject(DevelopmentModel.class);
                        m.id = d.getId();
                        list.add(m);
                    }

                    adapter.notifyDataSetChanged();
                });
    }
}