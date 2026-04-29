package com.example.smartvillageapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.app.AlertDialog;
import android.content.Intent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class CropFragment extends Fragment {

    RecyclerView recyclerView;
    List<CropModel> list;
    CropAdapter adapter;
    View emptyStateLayout;
    TextView emptyStateMsg;
    FloatingActionButton fabAddCrop;
    boolean isAdmin = false;

    public CropFragment() {
        super(R.layout.fragment_crop);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.crop_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        emptyStateLayout = view.findViewById(R.id.empty_state_layout);
        emptyStateMsg = view.findViewById(R.id.empty_state_message);
        emptyStateMsg.setText(getString(R.string.empty_crops));

        list = new ArrayList<>();
        adapter = new CropAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);

        if (getArguments() != null) {
            isAdmin = getArguments().getBoolean("isAdmin", false);
        }

        fabAddCrop = view.findViewById(R.id.fab_add_crop);
        if (isAdmin) {
            fabAddCrop.setVisibility(View.VISIBLE);
            fabAddCrop.setOnClickListener(v -> {
                startActivity(new Intent(getContext(), AddCropActivity.class));
            });
            setupSwipeToDelete();
        }

        loadCrops();
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
                CropModel model = list.get(position);

                DialogUtils.showConfirmDialog(getContext(),
                        getString(R.string.delete_crop_title),
                        getString(R.string.confirm_delete_crop),
                        getString(R.string.delete),
                        new DialogUtils.DialogCallback() {
                            @Override
                            public void onPositive() {
                                FirebaseFirestore.getInstance().collection("agriculture_crops").document(model.id).delete()
                                        .addOnSuccessListener(aVoid -> loadCrops());
                            }

                            @Override
                            public void onNegative() {
                                adapter.notifyItemChanged(position);
                            }
                        });
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void loadCrops() {

        FirebaseFirestore.getInstance()
                .collection("agriculture_crops")
                .get()
                .addOnSuccessListener(q -> {

                    list.clear();

                    for (DocumentSnapshot d : q) {
                        CropModel c = d.toObject(CropModel.class);
                        c.id = d.getId();
                        list.add(c);
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