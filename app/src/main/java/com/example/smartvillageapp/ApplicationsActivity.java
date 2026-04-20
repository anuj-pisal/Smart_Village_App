package com.example.smartvillageapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ApplicationsActivity extends BaseActivity {

    RecyclerView recyclerView;
    List<ApplicationModel> list;
    ApplicationAdapter adapter;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_applications);

        String jobId = getIntent().getStringExtra("jobId");

        recyclerView = findViewById(R.id.app_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapter = new ApplicationAdapter(this, list);
        recyclerView.setAdapter(adapter);

        loadApplications(jobId);
    }

    private void loadApplications(String jobId) {

        FirebaseFirestore.getInstance()
                .collection("applications")
                .whereEqualTo("jobId", jobId)
                .get()
                .addOnSuccessListener(q -> {

                    list.clear();

                    for (DocumentSnapshot d : q) {
                        ApplicationModel m = d.toObject(ApplicationModel.class);
                        m.docId = d.getId();
                        list.add(m);
                    }

                    adapter.notifyDataSetChanged();
                });
    }
}