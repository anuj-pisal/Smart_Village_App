package com.example.smartvillageapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;
import android.widget.RadioGroup;
import android.view.View;
import android.widget.TextView;

public class BillsActivity extends BaseActivity {

    RecyclerView recycler;
    List<BillModel> list = new ArrayList<>();
    List<BillModel> fullList = new ArrayList<>();
    BillAdapter adapter;
    RadioGroup filterGroup;
    View emptyStateLayout;
    TextView emptyStateMsg;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_bills);

        recycler = findViewById(R.id.bill_list);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        emptyStateLayout = findViewById(R.id.empty_state_layout);
        emptyStateMsg = findViewById(R.id.empty_state_message);
        emptyStateMsg.setText("No bills found");

        adapter = new BillAdapter(this, list);
        recycler.setAdapter(adapter);

        filterGroup = findViewById(R.id.bill_filter_group);
        filterGroup.setOnCheckedChangeListener((group, checkedId) -> filterBills(checkedId));

        loadBills();
    }

    private void filterBills(int checkedId) {
        list.clear();
        for (BillModel b : fullList) {
            if (checkedId == R.id.filter_paid) {
                if ("paid".equalsIgnoreCase(b.status)) {
                    list.add(b);
                }
            } else if (checkedId == R.id.filter_unpaid) {
                if (!"paid".equalsIgnoreCase(b.status)) {
                    list.add(b);
                }
            } else {
                list.add(b);
            }
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

    private void loadBills() {

        String userId = FirebaseAuth.getInstance().getUid();

        FirebaseFirestore.getInstance()
                .collection("bills")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(q -> {

                    list.clear();
                    fullList.clear();

                    long currentTime = System.currentTimeMillis();

                    for (var d : q) {
                        BillModel b = d.toObject(BillModel.class);
                        
                        if (b.expiryTimestamp > 0 && currentTime > b.expiryTimestamp) {
                            continue; // Skip expired bills
                        }
                        
                        b.id = d.getId();
                        list.add(b);
                        fullList.add(b);
                    }

                    if (filterGroup != null) {
                        filterBills(filterGroup.getCheckedRadioButtonId());
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}