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

public class AdminUserManagementActivity extends BaseActivity {

    RecyclerView recyclerView;
    List<UserModel> list = new ArrayList<>();
    AdminUserManagementAdapter adapter;
    View emptyStateLayout;
    TextView emptyStateMsg;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_admin_user_management);

        recyclerView = findViewById(R.id.recycler_users_admin);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        emptyStateLayout = findViewById(R.id.empty_state_layout);
        emptyStateMsg = findViewById(R.id.empty_state_message);
        emptyStateMsg.setText(getString(R.string.empty_users));

        adapter = new AdminUserManagementAdapter(this, list);
        recyclerView.setAdapter(adapter);

        FirebaseFirestore.getInstance()
                .collection("users")
                .get()
                .addOnSuccessListener(snapshot -> {

                    list.clear();

                    for (DocumentSnapshot d : snapshot.getDocuments()) {
                        if(d.getString("username").equals("Admin") &&
                                d.getString("email").equals("admin@gmail.com")) continue;
                        list.add(new UserModel(
                                d.getId(),
                                d.getString("username"),
                                d.getString("email")
                        ));
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