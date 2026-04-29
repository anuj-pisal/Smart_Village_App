package com.example.smartvillageapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.widget.SearchView;

public class AdminLogsActivity extends BaseActivity {

    RecyclerView recycler;
    List<ActivityLogModel> list = new ArrayList<>();
    PriorityQueue<ActivityLogModel> pq;
    AdminLogsAdapter adapter;
    View emptyStateLayout;
    TextView emptyStateMsg;
    SearchView searchView;
    List<ActivityLogModel> fullLogList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_admin_logs);

        recycler = findViewById(R.id.recycler_logs);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        emptyStateLayout = findViewById(R.id.empty_state_layout);
        emptyStateMsg = findViewById(R.id.empty_state_message);
        emptyStateMsg.setText(getString(R.string.empty_logs));

        adapter = new AdminLogsAdapter(this, list);
        recycler.setAdapter(adapter);

        searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterLogs(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterLogs(newText);
                return true;
            }
        });

        pq = new PriorityQueue<>((x,y) ->
                Math.toIntExact(y.timestamp - x.timestamp));

        FirebaseFirestore.getInstance()
                .collection("logs")
//                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshot -> {
                    pq.clear();
                    for (DocumentSnapshot d : snapshot.getDocuments()) {
                        ActivityLogModel log = d.toObject(ActivityLogModel.class);
                        pq.add(log);
                    }

                    fullLogList.clear();
                    while(!pq.isEmpty()) {
                        fullLogList.add(pq.poll());
                    }

                    list.clear();
                    list.addAll(fullLogList);
                    adapter.notifyDataSetChanged();
                    updateEmptyState();
                });
    }

    private void filterLogs(String query) {
        list.clear();
        if (query.isEmpty()) {
            list.addAll(fullLogList);
        } else {
            String lowerQuery = query.toLowerCase();
            for (ActivityLogModel log : fullLogList) {
                if ((log.userId != null && log.userId.toLowerCase().contains(lowerQuery)) ||
                    (log.details != null && log.details.toLowerCase().contains(lowerQuery)) ||
                    (log.action != null && log.action.toLowerCase().contains(lowerQuery))) {
                    list.add(log);
                }
            }
        }
        adapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (list.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            if (searchView != null && !searchView.getQuery().toString().isEmpty()) {
                emptyStateMsg.setText(getString(R.string.empty_logs_search));
            } else {
                emptyStateMsg.setText(getString(R.string.empty_logs));
            }
            recycler.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            recycler.setVisibility(View.VISIBLE);
        }
    }
}