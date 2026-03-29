package com.example.smartvillageapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class JobsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<JobModel> list;
    JobAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs);

        recyclerView = findViewById(R.id.jobs_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapter = new JobAdapter(this, list);
        recyclerView.setAdapter(adapter);

        findViewById(R.id.add_job_btn).setOnClickListener(v ->
                startActivity(new Intent(this, AddJobActivity.class)));

        loadJobs();
    }

    private void loadJobs() {
        FirebaseFirestore.getInstance()
                .collection("jobs")
                .get()
                .addOnSuccessListener(query -> {
                    list.clear();
                    for (DocumentSnapshot doc : query) {
                        JobModel job = doc.toObject(JobModel.class);
                        job.jobId = doc.getId();
                        list.add(job);
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}