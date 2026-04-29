package com.example.smartvillageapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.widget.RadioGroup;

public class BillsUserListActivity extends BaseActivity {

    RecyclerView recycler;
    List<UserModel> list = new ArrayList<>();
    List<UserModel> fullList = new ArrayList<>();
    UserAdapter adapter;
    RadioGroup filterGroup;
    Set<String> overdueUserIds = new HashSet<>();

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_bills_user_list);

        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new UserAdapter(this, list);
        recycler.setAdapter(adapter);

        filterGroup = findViewById(R.id.user_filter_group);

        filterGroup.setOnCheckedChangeListener((group, checkedId) -> {
            updateRadioStyles(checkedId);   // ✅ highlight selected
            filterUsers(checkedId);
        });

        updateRadioStyles(filterGroup.getCheckedRadioButtonId());
        loadData();
    }

    private void updateRadioStyles(int checkedId) {

        for (int i = 0; i < filterGroup.getChildCount(); i++) {

            android.view.View child = filterGroup.getChildAt(i);

            if (child instanceof android.widget.RadioButton) {

                android.widget.RadioButton rb = (android.widget.RadioButton) child;

                if (rb.getId() == checkedId) {
                    rb.setBackgroundResource(R.drawable.bg_lang_selected);
                    rb.setTextColor(getResources().getColor(android.R.color.white));
                } else {
                    rb.setBackground(null);
                    rb.setTextColor(getResources().getColor(android.R.color.white));
                }
            }
        }
    }
    private void filterUsers(int checkedId) {
        list.clear();
        for (UserModel user : fullList) {
            if (checkedId == R.id.filter_overdue) {
                if (overdueUserIds.contains(user.id)) {
                    list.add(user);
                }
            } else {
                list.add(user);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void loadData() {
        FirebaseFirestore.getInstance()
                .collection("users")
                .get()
                .addOnSuccessListener(snapshot -> {
                    fullList.clear();
                    for (DocumentSnapshot d : snapshot) {
                        UserModel u = new UserModel(
                                d.getId(),
                                d.getString("username"),
                                d.getString("email")
                        );
                        list.add(u);
                        fullList.add(u);
                    }

                    adapter.notifyDataSetChanged();
                    fetchOverdueBills();
                });
    }

    private void fetchOverdueBills() {
        FirebaseFirestore.getInstance().collection("bills")
                .whereEqualTo("status", "unpaid")
                .get()
                .addOnSuccessListener(snapshot -> {
                    SimpleDateFormat format = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());
                    long currentTime = System.currentTimeMillis();

                    for (DocumentSnapshot d : snapshot) {
                        String dueDateStr = d.getString("dueDate");
                        if (dueDateStr != null) {
                            try {
                                Date date = format.parse(dueDateStr);
                                if (date != null && currentTime > date.getTime()) {
                                    overdueUserIds.add(d.getString("userId"));
                                }
                            } catch (ParseException ignored) {}
                        }
                    }
                    if (filterGroup != null) {
                        filterUsers(filterGroup.getCheckedRadioButtonId());
                    }
                });
    }
}