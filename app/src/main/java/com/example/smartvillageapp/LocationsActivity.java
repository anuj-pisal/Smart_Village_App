package com.example.smartvillageapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.recyclerview.widget.ItemTouchHelper;

public class LocationsActivity extends BaseActivity {

    RecyclerView recyclerView;
    LocationAdapter adapter;
    List<LocationModel> list;
    View emptyStateLayout;
    TextView emptyStateMsg;
    FloatingActionButton fabAddLocation;
    boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);

        recyclerView = findViewById(R.id.location_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        emptyStateLayout = findViewById(R.id.empty_state_layout);
        emptyStateMsg = findViewById(R.id.empty_state_message);
        emptyStateMsg.setText(getString(R.string.empty_locations));

        list = new ArrayList<>();
        adapter = new LocationAdapter(this, list);
        recyclerView.setAdapter(adapter);

        isAdmin = getIntent().getBooleanExtra("isAdmin", false);
        fabAddLocation = findViewById(R.id.fab_add_location);

        if (isAdmin) {
            fabAddLocation.setVisibility(View.VISIBLE);
            fabAddLocation.setOnClickListener(v -> {
                startActivity(new Intent(LocationsActivity.this, AddLocationActivity.class));
            });
            setupSwipeToDelete();
        }

        loadLocations();
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
                LocationModel location = list.get(position);
                
                DialogUtils.showConfirmDialog(LocationsActivity.this,
                    getString(R.string.delete_location_title),
                    getString(R.string.confirm_delete_location),
                    getString(R.string.delete),
                    new DialogUtils.DialogCallback() {
                        @Override
                        public void onPositive() {
                            FirebaseFirestore.getInstance().collection("locations").document(location.id).delete()
                                .addOnSuccessListener(aVoid -> loadLocations());
                        }

                        @Override
                        public void onNegative() {
                            adapter.notifyItemChanged(position);
                        }
                    });
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void loadLocations() {

        FirebaseFirestore.getInstance()
                .collection("locations")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    list.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        LocationModel model = doc.toObject(LocationModel.class);
                        model.id = doc.getId();
                        list.add(model);
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