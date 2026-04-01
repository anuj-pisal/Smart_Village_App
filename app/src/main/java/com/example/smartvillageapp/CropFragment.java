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

import java.util.ArrayList;
import java.util.List;


public class CropFragment extends Fragment {

    RecyclerView recyclerView;
    List<CropModel> list;
    CropAdapter adapter;

    public CropFragment() {
        super(R.layout.fragment_crop);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.crop_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        list = new ArrayList<>();
        adapter = new CropAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);

        loadCrops();
    }

    private void loadCrops() {

        FirebaseFirestore.getInstance()
                .collection("agriculture_crops")
                .get()
                .addOnSuccessListener(q -> {

                    list.clear();

                    for (DocumentSnapshot d : q) {
                        CropModel c = d.toObject(CropModel.class);
                        list.add(c);
                    }

                    adapter.notifyDataSetChanged();
                });
    }
}