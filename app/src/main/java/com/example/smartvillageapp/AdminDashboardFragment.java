package com.example.smartvillageapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardFragment extends Fragment {

    RecyclerView recycler;

    public AdminDashboardFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_dashboard, container, false);

        recycler = view.findViewById(R.id.admin_recycler);

        recycler.setLayoutManager(new GridLayoutManager(getContext(), 2));

        List<AdminItem> list = new ArrayList<>();

        list.add(new AdminItem("Contacts", R.drawable.contacts));
        list.add(new AdminItem("Notices", R.drawable.notices));
        list.add(new AdminItem("Bills", R.drawable.bill));
        list.add(new AdminItem("Locations", R.drawable.locations));
        list.add(new AdminItem("Complaints", R.drawable.complaints));
        list.add(new AdminItem("Schemes", R.drawable.schemes));
        list.add(new AdminItem("Developments", R.drawable.development));
        list.add(new AdminItem("Agriculture", R.drawable.agriculture));
        list.add(new AdminItem("Users", R.drawable.profile));
        list.add(new AdminItem("Logs", R.drawable.logs));

        recycler.setAdapter(new AdminDashboardAdapter(getContext(), list));

        return view;
    }
}