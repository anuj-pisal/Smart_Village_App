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
import android.view.View;
import android.widget.TextView;

public class NoticesActivity extends BaseActivity {

    RecyclerView recyclerView;
    List<NoticeModel> list;
    NoticeAdapter adapter;
    View emptyStateLayout;
    TextView emptyStateMsg;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_notices);

        recyclerView = findViewById(R.id.notice_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        emptyStateLayout = findViewById(R.id.empty_state_layout);
        emptyStateMsg = findViewById(R.id.empty_state_message);
        emptyStateMsg.setText("No recent notices found");

        list = new ArrayList<>();
        adapter = new NoticeAdapter(this, list);
        recyclerView.setAdapter(adapter);

        loadNotices();
    }

    private void loadNotices() {

        FirebaseFirestore.getInstance()
                .collection("notices")
                .get()
                .addOnSuccessListener(q -> {

                    list.clear();

                    long currentTime = System.currentTimeMillis();

                    for (DocumentSnapshot d : q) {

                        NoticeModel n = d.toObject(NoticeModel.class);

                        // 🔥 CHECK 48 HOURS (48 * 60 * 60 * 1000)
                        long diff = currentTime - n.timestamp;

                        if (diff <= 48L * 60 * 60 * 1000) {
                            list.add(n); // ✅ only valid notices
                        }
                    }

                    adapter.notifyDataSetChanged();
                    
                    if (list.isEmpty()) {
                        emptyStateLayout.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        emptyStateLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                });
    }
}