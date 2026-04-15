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

public class BillsUserListActivity extends AppCompatActivity {

    RecyclerView recycler;
    List<UserModel> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_bills_user_list);

        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        FirebaseFirestore.getInstance()
                .collection("users")
                .get()
                .addOnSuccessListener(snapshot -> {

                    for (DocumentSnapshot d : snapshot) {
                        list.add(new UserModel(
                                d.getId(),
                                d.getString("username"),
                                d.getString("email")
                        ));
                    }

                    recycler.setAdapter(new UserAdapter(this, list));
                });
    }
}