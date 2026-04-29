package com.example.smartvillageapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;
import android.app.AlertDialog;
import android.content.Intent;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.View;

public class SchemesActivity extends BaseActivity {

    RecyclerView recycler;
    List<SchemeModel> list = new ArrayList<>();
    SchemeAdapter adapter;
    View emptyStateLayout;
    TextView emptyStateMsg;
    FloatingActionButton fabAddScheme;
    boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_schemes);

        recycler = findViewById(R.id.scheme_list);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        emptyStateLayout = findViewById(R.id.empty_state_layout);
        emptyStateMsg = findViewById(R.id.empty_state_message);
        emptyStateMsg.setText(getString(R.string.empty_schemes));

        adapter = new SchemeAdapter(this, list);
        recycler.setAdapter(adapter);

        isAdmin = getIntent().getBooleanExtra("isAdmin", false);
        fabAddScheme = findViewById(R.id.fab_add_scheme);

        if (isAdmin) {
            fabAddScheme.setVisibility(View.VISIBLE);
            fabAddScheme.setOnClickListener(v -> {
                startActivity(new Intent(SchemesActivity.this, AddSchemeActivity.class));
            });
            setupSwipeToDelete();
        }

        loadSchemes();
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
                SchemeModel scheme = list.get(position);
                
                DialogUtils.showConfirmDialog(SchemesActivity.this,
                    getString(R.string.delete_scheme_title),
                    getString(R.string.confirm_delete_scheme),
                    getString(R.string.delete),
                    new DialogUtils.DialogCallback() {
                        @Override
                        public void onPositive() {
                            FirebaseFirestore.getInstance().collection("schemes").document(scheme.id).delete()
                                .addOnSuccessListener(aVoid -> loadSchemes());
                        }

                        @Override
                        public void onNegative() {
                            adapter.notifyItemChanged(position);
                        }
                    });
            }
        }).attachToRecyclerView(recycler);
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
                    
                    if (list.isEmpty()) {
                        emptyStateLayout.setVisibility(View.VISIBLE);
                        recycler.setVisibility(View.GONE);
                    } else {
                        emptyStateLayout.setVisibility(View.GONE);
                        recycler.setVisibility(View.VISIBLE);
                    }
                });
    }
}