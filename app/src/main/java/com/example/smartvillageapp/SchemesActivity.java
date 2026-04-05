package com.example.smartvillageapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

public class SchemesActivity extends AppCompatActivity {

    RecyclerView recycler;
    List<SchemeModel> list = new ArrayList<>();
    SchemeAdapter adapter;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_schemes);

        recycler = findViewById(R.id.scheme_list);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SchemeAdapter(this, list);
        recycler.setAdapter(adapter);

        loadSchemes();
    }

    private void loadSchemes() {

        FirebaseFirestore.getInstance()
                .collection("schemes")
                .get()
                .addOnSuccessListener(q -> {

                    list.clear();

                    for (var d : q) {
                        SchemeModel s = d.toObject(SchemeModel.class);
                        s.id = d.getId();
                        list.add(s);
                    }

                    adapter.notifyDataSetChanged();
                });
    }
}