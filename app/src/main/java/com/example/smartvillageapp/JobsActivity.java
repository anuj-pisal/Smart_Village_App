package com.example.smartvillageapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import android.widget.RadioGroup;
import android.view.View;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;

public class JobsActivity extends BaseActivity {

    RecyclerView recyclerView;
    List<JobModel> list;
    List<JobModel> fullList;
    JobAdapter adapter;
    RadioGroup filterGroup;
    Set<String> appliedJobIds = new HashSet<>();
    View emptyStateLayout;
    TextView emptyStateMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs);

        recyclerView = findViewById(R.id.jobs_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        fullList = new ArrayList<>();
        adapter = new JobAdapter(this, list);
        recyclerView.setAdapter(adapter);

        emptyStateLayout = findViewById(R.id.empty_state_layout);
        emptyStateMsg = findViewById(R.id.empty_state_message);
        emptyStateMsg.setText(getString(R.string.empty_jobs));

        filterGroup = findViewById(R.id.job_filter_group);
        filterGroup.setOnCheckedChangeListener((group, checkedId) -> applyFilter());

        findViewById(R.id.add_job_btn).setOnClickListener(v ->
                startActivity(new Intent(this, AddJobActivity.class)));

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("applications")
            .whereEqualTo("applicantId", userId)
            .addSnapshotListener((query, error) -> {
                if (query != null) {
                    appliedJobIds.clear();
                    for (DocumentSnapshot doc : query) {
                        appliedJobIds.add(doc.getString("jobId"));
                    }
                    applyFilter();
                }
            });

        loadJobs();
    }

    private void loadJobs() {
        FirebaseFirestore.getInstance()
                .collection("jobs")
                .addSnapshotListener((query, error) -> {
                    if (query != null) {
                        fullList.clear();
                        for (DocumentSnapshot doc : query) {
                            JobModel job = doc.toObject(JobModel.class);
                            job.jobId = doc.getId();
                            fullList.add(job);
                        }
                        applyFilter();
                    }
                });
    }

    private void applyFilter() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        list.clear();
        if (filterGroup == null) return;
        int selectedId = filterGroup.getCheckedRadioButtonId();

        for (JobModel job : fullList) {
            if (selectedId == R.id.filter_posted) {
                if (userId.equals(job.postedBy)) {
                    list.add(job);
                }
            } else if (selectedId == R.id.filter_applied) {
                if (appliedJobIds.contains(job.jobId)) {
                    list.add(job);
                }
            } else {
                list.add(job);
            }
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        
        if (list.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}