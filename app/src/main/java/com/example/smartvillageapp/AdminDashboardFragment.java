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

        list.add(new AdminItem(getString(R.string.contacts), R.drawable.contacts));
        list.add(new AdminItem(getString(R.string.notices), R.drawable.notices));
        list.add(new AdminItem(getString(R.string.bills), R.drawable.bill));
        list.add(new AdminItem(getString(R.string.locations), R.drawable.locations));
        list.add(new AdminItem(getString(R.string.complaints), R.drawable.complaints));
        list.add(new AdminItem(getString(R.string.schemes), R.drawable.schemes));
        list.add(new AdminItem(getString(R.string.developments), R.drawable.development));
        list.add(new AdminItem(getString(R.string.agriculture), R.drawable.agriculture));
        list.add(new AdminItem(getString(R.string.users), R.drawable.profile));
        list.add(new AdminItem(getString(R.string.logs), R.drawable.logs));

        recycler.setAdapter(new AdminDashboardAdapter(getContext(), list));

        return view;
    }
}