package com.example.smartvillageapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import android.view.View;
import android.widget.TextView;
import android.widget.RadioGroup;

public class AdminComplaintActivity extends BaseActivity {

    RecyclerView recyclerView;
    List<AdminComplaintModel> list = new ArrayList<>();
    List<AdminComplaintModel> fullList = new ArrayList<>();
    AdminComplaintAdapter adapter;
    View emptyStateLayout;
    TextView emptyStateMsg;
    RadioGroup filterGroup;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_admin_complaint);

        recyclerView = findViewById(R.id.recycler_complaints);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        emptyStateLayout = findViewById(R.id.empty_state_layout);
        emptyStateMsg = findViewById(R.id.empty_state_message);
        emptyStateMsg.setText("No pending complaints");

        adapter = new AdminComplaintAdapter(this, list);
        recyclerView.setAdapter(adapter);

        filterGroup = findViewById(R.id.complaint_filter_group);

        filterGroup.setOnCheckedChangeListener((group, checkedId) -> {
            updateRadioStyles(checkedId);   // ✅ highlight selected
            filterComplaints(checkedId);
        });

        updateRadioStyles(filterGroup.getCheckedRadioButtonId());
        loadComplaints();
    }

    private void updateRadioStyles(int checkedId) {

        for (int i = 0; i < filterGroup.getChildCount(); i++) {

            View child = filterGroup.getChildAt(i);

            if (child instanceof android.widget.RadioButton) {

                android.widget.RadioButton rb = (android.widget.RadioButton) child;

                if (rb.getId() == checkedId) {
                    rb.setBackgroundResource(R.drawable.bg_lang_selected);
                    rb.setTextColor(getResources().getColor(android.R.color.white));
                } else {
                    rb.setBackground(null);
                    rb.setTextColor(getResources().getColor(android.R.color.white));
                }
            }
        }
    }

    private void filterComplaints(int checkedId) {
        list.clear();
        for (AdminComplaintModel c : fullList) {
            if (checkedId == R.id.filter_pending) {
                if ("pending".equalsIgnoreCase(c.status)) {
                    list.add(c);
                }
            } else if (checkedId == R.id.filter_in_progress) {
                if ("in_progress".equalsIgnoreCase(c.status)) {
                    list.add(c);
                }
            } else {
                list.add(c);
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
    }



    private void loadComplaints() {
        FirebaseFirestore.getInstance()
                .collection("complaints")
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null || snapshot == null) return;

                    fullList.clear();

                    for (DocumentSnapshot d : snapshot.getDocuments()) {

                        AdminComplaintModel c = d.toObject(AdminComplaintModel.class);

                        if(!c.status.equals("resolved")) {
                            c.userId = d.getString("userId");
                            c.docId = d.getId();

                            fullList.add(c);
                        }

                    }

                    if (filterGroup != null) {
                        filterComplaints(filterGroup.getCheckedRadioButtonId());
                    } else {
                        list.clear();
                        list.addAll(fullList);
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}