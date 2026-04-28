package com.example.smartvillageapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;


public class ExperienceFragment extends Fragment {

    RecyclerView recyclerView;
    FloatingActionButton addBtn;
    List<ExperienceModel> list;
    ExperienceAdapter adapter;

    public ExperienceFragment() {
        super(R.layout.fragment_experience);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        recyclerView = view.findViewById(R.id.exp_recycler);
        addBtn = view.findViewById(R.id.add_exp_btn);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        list = new ArrayList<>();
        adapter = new ExperienceAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);

        loadExperiences();

        // ➕ ADD NEW EXPERIENCE
        addBtn.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), AddExperienceActivity.class));
        });
    }

    private void loadExperiences() {

        FirebaseFirestore.getInstance()
                .collection("experiences")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((q, e) -> {
                    if (e != null) {
                        return;
                    }
                    if (q != null) {
                        list.clear();

                        for (DocumentSnapshot d : q) {

                            ExperienceModel model = d.toObject(ExperienceModel.class);
                            model.id = d.getId();
                            list.add(model);
                        }

                        adapter.notifyDataSetChanged();
                    }
                });
    }
}