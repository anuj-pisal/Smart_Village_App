package com.example.smartvillageapp;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.app.AlertDialog;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;


public class VideoFragment extends Fragment {

    RecyclerView recyclerView;
    List<VideoModel> list;
    VideoAdapter adapter;
    View emptyStateLayout;
    TextView emptyStateMsg;
    FloatingActionButton fabAddVideo;
    boolean isAdmin = false;

    public VideoFragment() {
        super(R.layout.fragment_video);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.video_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        emptyStateLayout = view.findViewById(R.id.empty_state_layout);
        emptyStateMsg = view.findViewById(R.id.empty_state_message);
        emptyStateMsg.setText("No videos available");

        list = new ArrayList<>();
        adapter = new VideoAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);

        if (getArguments() != null) {
            isAdmin = getArguments().getBoolean("isAdmin", false);
        }

        fabAddVideo = view.findViewById(R.id.fab_add_video);
        if (isAdmin) {
            fabAddVideo.setVisibility(View.VISIBLE);
            fabAddVideo.setOnClickListener(v -> {
                startActivity(new Intent(getContext(), AddVideoActivity.class));
            });
            setupSwipeToDelete();
        }

        loadVideos();
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
                VideoModel model = list.get(position);

                DialogUtils.showConfirmDialog(getContext(),
                        "Delete Video",
                        "Are you sure you want to delete this video?",
                        "Delete",
                        new DialogUtils.DialogCallback() {
                            @Override
                            public void onPositive() {
                                FirebaseFirestore.getInstance().collection("agriculture_videos").document(model.id).delete()
                                        .addOnSuccessListener(aVoid -> loadVideos());
                            }

                            @Override
                            public void onNegative() {
                                adapter.notifyItemChanged(position);
                            }
                        });
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void loadVideos() {

        FirebaseFirestore.getInstance()
                .collection("agriculture_videos")
                .get()
                .addOnSuccessListener(q -> {

                    list.clear();

                    for (DocumentSnapshot d : q) {
                        VideoModel v = d.toObject(VideoModel.class);
                        v.id = d.getId();
                        list.add(v);
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