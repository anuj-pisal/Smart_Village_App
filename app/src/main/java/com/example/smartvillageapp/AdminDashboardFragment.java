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

        list.add(new AdminItem("Contacts"));
        list.add(new AdminItem("Notices"));
        list.add(new AdminItem("Bills"));
        list.add(new AdminItem("Locations"));
        list.add(new AdminItem("Complaints"));
        list.add(new AdminItem("Schemes"));
        list.add(new AdminItem("Developments"));
        list.add(new AdminItem("Agriculture"));
        list.add(new AdminItem("Users"));
        list.add(new AdminItem("Logs"));

        recycler.setAdapter(new AdminDashboardAdapter(getContext(), list));

        return view;
    }
}