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
import android.content.Intent;
import android.app.AlertDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.recyclerview.widget.ItemTouchHelper;

public class DevelopmentActivity extends BaseActivity {

    RecyclerView recyclerView;
    List<DevelopmentModel> list;
    DevelopmentAdapter adapter;
    View emptyStateLayout;
    TextView emptyStateMsg;
    FloatingActionButton fabAddDev;
    boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_development);

        recyclerView = findViewById(R.id.dev_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        emptyStateLayout = findViewById(R.id.empty_state_layout);
        emptyStateMsg = findViewById(R.id.empty_state_message);
        emptyStateMsg.setText("No development projects to show");

        list = new ArrayList<>();
        adapter = new DevelopmentAdapter(this, list);
        recyclerView.setAdapter(adapter);

        isAdmin = getIntent().getBooleanExtra("isAdmin", false);
        fabAddDev = findViewById(R.id.fab_add_dev);

        if (isAdmin) {
            fabAddDev.setVisibility(View.VISIBLE);
            fabAddDev.setOnClickListener(v -> {
                startActivity(new Intent(DevelopmentActivity.this, AddDevelopmentActivity.class));
            });
            setupSwipeToDelete();
        }

        loadData();
    }

    private void setupSwipeToDelete() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                DevelopmentModel model = list.get(position);

                DialogUtils.showConfirmDialog(DevelopmentActivity.this,
                        "Delete Development Project",
                        "Are you sure you want to delete this project?",
                        "Delete",
                        new DialogUtils.DialogCallback() {
                            @Override
                            public void onPositive() {
                                FirebaseFirestore.getInstance().collection("developments").document(model.id).delete()
                                        .addOnSuccessListener(aVoid -> loadData());
                            }

                            @Override
                            public void onNegative() {
                                adapter.notifyItemChanged(position);
                            }
                        });
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void loadData() {

        FirebaseFirestore.getInstance()
                .collection("developments")
                .get()
                .addOnSuccessListener(q -> {

                    list.clear();

                    for (DocumentSnapshot d : q) {
                        DevelopmentModel m = d.toObject(DevelopmentModel.class);
                        m.id = d.getId();
                        list.add(m);
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