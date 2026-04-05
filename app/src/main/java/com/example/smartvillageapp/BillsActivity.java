package com.example.smartvillageapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

public class BillsActivity extends AppCompatActivity {

    RecyclerView recycler;
    List<BillModel> list = new ArrayList<>();
    BillAdapter adapter;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_bills);

        recycler = findViewById(R.id.bill_list);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BillAdapter(this, list);
        recycler.setAdapter(adapter);

        loadBills();
    }

    private void loadBills() {

        String userId = FirebaseAuth.getInstance().getUid();

        FirebaseFirestore.getInstance()
                .collection("bills")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(q -> {

                    list.clear();

                    for (var d : q) {
                        BillModel b = d.toObject(BillModel.class);
                        b.id = d.getId();
                        list.add(b);
                    }

                    adapter.notifyDataSetChanged();
                });
    }
}