package com.example.smartvillageapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminComplaintActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<AdminComplaintModel> list = new ArrayList<>();
    AdminComplaintAdapter adapter;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_admin_complaint);

        recyclerView = findViewById(R.id.recycler_complaints);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AdminComplaintAdapter(this, list);
        recyclerView.setAdapter(adapter);

        FirebaseFirestore.getInstance()
                .collection("complaints")
                .get()
                .addOnSuccessListener(snapshot -> {

                    list.clear();

                    for (DocumentSnapshot d : snapshot.getDocuments()) {

                        AdminComplaintModel c = d.toObject(AdminComplaintModel.class);

                        if(!c.status.equals("resolved")) {
                            c.userId = d.getString("userId");
                            c.docId = d.getId(); // 🔥 VERY IMPORTANT

                            list.add(c);
                        }

                    }

                    adapter.notifyDataSetChanged();
                });
    }
}