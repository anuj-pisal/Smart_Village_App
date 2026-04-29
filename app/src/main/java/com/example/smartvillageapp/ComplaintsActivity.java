package com.example.smartvillageapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import android.view.View;
import android.widget.TextView;

public class ComplaintsActivity extends BaseActivity {

    RecyclerView recycler;
    List<ComplaintModel> list = new ArrayList<>();
    ComplaintAdapter adapter;
    View emptyStateLayout;
    TextView emptyStateMsg;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_complaints);

        recycler = findViewById(R.id.complaint_list);
        FloatingActionButton addBtn = findViewById(R.id.add_btn);

        emptyStateLayout = findViewById(R.id.empty_state_layout);
        emptyStateMsg = findViewById(R.id.empty_state_message);
        emptyStateMsg.setText("No complaints filed yet");

        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ComplaintAdapter(this, list);
        recycler.setAdapter(adapter);

        loadComplaints();

        addBtn.setOnClickListener(v ->
                startActivity(new Intent(this, AddComplaintActivity.class)));
    }

    private void loadComplaints() {

        String userId = FirebaseAuth.getInstance().getUid();

        FirebaseFirestore.getInstance()
                .collection("complaints")
                .whereEqualTo("userId", userId)
                .addSnapshotListener((q, e) -> {
                    if (e != null) {
                        return;
                    }
                    if (q != null) {
                        list.clear();

                        for (var d : q) {
                            ComplaintModel c = d.toObject(ComplaintModel.class);
                            c.id = d.getId();
                            list.add(c);
                        }

                        adapter.notifyDataSetChanged();
                        
                        if (list.isEmpty()) {
                            emptyStateLayout.setVisibility(View.VISIBLE);
                            recycler.setVisibility(View.GONE);
                        } else {
                            emptyStateLayout.setVisibility(View.GONE);
                            recycler.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }
}