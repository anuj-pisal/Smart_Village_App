package com.example.smartvillageapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class UserBillsActivity extends BaseActivity {

    RecyclerView recycler;
    List<BillModel> billList = new ArrayList<>();
    BillUserAdapter adapter; // 🔥 IMPORTANT

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_user_bills);

        recycler = findViewById(R.id.recycler_bills);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        // 🔥 INITIALIZE ADAPTER
        adapter = new BillUserAdapter(this, billList);
        recycler.setAdapter(adapter);

        String userId = getIntent().getStringExtra("userId");

        FirebaseFirestore.getInstance()
                .collection("bills")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(snapshot -> {

                    Toast.makeText(this, getString(R.string.bills_found_prefix) + snapshot.size(), Toast.LENGTH_SHORT).show();

                    billList.clear();

                    for (DocumentSnapshot d : snapshot.getDocuments()) {
                        BillModel bill = d.toObject(BillModel.class);
                        billList.add(bill);
                    }

                    adapter.notifyDataSetChanged(); // 🔥 NOW THIS WORKS
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, getString(R.string.error_prefix) + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}