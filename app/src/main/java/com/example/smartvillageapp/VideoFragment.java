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

import java.util.ArrayList;
import java.util.List;


public class VideoFragment extends Fragment {

    RecyclerView recyclerView;
    List<VideoModel> list;
    VideoAdapter adapter;

    public VideoFragment() {
        super(R.layout.fragment_video);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.video_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        list = new ArrayList<>();
        adapter = new VideoAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);

        loadVideos();
    }

    private void loadVideos() {

        FirebaseFirestore.getInstance()
                .collection("agriculture_videos")
                .get()
                .addOnSuccessListener(q -> {

                    list.clear();

                    for (DocumentSnapshot d : q) {
                        VideoModel v = d.toObject(VideoModel.class);
                        list.add(v);
                    }

                    adapter.notifyDataSetChanged();
                });
    }
}