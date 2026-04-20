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

public class AdminLogsActivity extends AppCompatActivity {

    RecyclerView recycler;
    List<ActivityLogModel> list = new ArrayList<>();
    PriorityQueue<ActivityLogModel> pq;
    AdminLogsAdapter adapter;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_admin_logs);

        recycler = findViewById(R.id.recycler_logs);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AdminLogsAdapter(this, list);
        recycler.setAdapter(adapter);

        pq = new PriorityQueue<>((x,y) ->
                Math.toIntExact(y.timestamp - x.timestamp));

        FirebaseFirestore.getInstance()
                .collection("logs")
//                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshot -> {

                    list.clear();
                    pq.clear();

                    for (DocumentSnapshot d : snapshot.getDocuments()) {
                        ActivityLogModel log = d.toObject(ActivityLogModel.class);
                        pq.add(log);
                    }

                    while(!pq.isEmpty()) {
                        list.add(pq.poll());
                    }

                    adapter.notifyDataSetChanged();
                });
    }
}