package com.example.smartvillageapp;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class AdminUserDetailActivity extends BaseActivity {

    TextView nameTv, emailTv, idTv;
    RecyclerView recyclerView;
    List<ActivityLogModel> logList = new ArrayList<>();
    AdminLogsAdapter adapter;
    View emptyStateLayout;
    TextView emptyStateMsg;

    String userId, userName, userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_detail);

        nameTv = findViewById(R.id.user_name);
        emailTv = findViewById(R.id.user_email);
        idTv = findViewById(R.id.user_id);
        recyclerView = findViewById(R.id.recycler_user_logs);
        emptyStateLayout = findViewById(R.id.empty_state_layout);
        emptyStateMsg = findViewById(R.id.empty_state_message);

        userId = getIntent().getStringExtra("userId");
        userName = getIntent().getStringExtra("name");
        userEmail = getIntent().getStringExtra("email");

        nameTv.setText(userName);
        emailTv.setText(userEmail);
        idTv.setText("ID: " + userId);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminLogsAdapter(this, logList);
        recyclerView.setAdapter(adapter);

        loadUserLogs();
    }

    private void loadUserLogs() {
        FirebaseFirestore.getInstance()
                .collection("logs")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshot -> {
                    logList.clear();
                    for (DocumentSnapshot d : snapshot.getDocuments()) {
                        ActivityLogModel log = d.toObject(ActivityLogModel.class);
                        // Filter logs for this specific user
                        // userId in logs contains user name and ID
                        if (log.userId != null && (log.userId.contains(userId) || log.userId.contains(userName))) {
                            logList.add(log);
                        }
                    }
                    adapter.notifyDataSetChanged();

                    if (logList.isEmpty()) {
                        emptyStateLayout.setVisibility(View.VISIBLE);
                        emptyStateMsg.setText("No activity logs for this user");
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        emptyStateLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                });
    }
}
