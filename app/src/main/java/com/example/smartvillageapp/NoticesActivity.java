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
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import android.view.View;
import android.widget.TextView;

public class NoticesActivity extends BaseActivity {

    RecyclerView recyclerView;
    List<NoticeModel> imageNoticesList;
    List<NoticeModel> textNoticesList;
    NoticeAdapter imageAdapter;
    TextNoticeAdapter textAdapter;
    View emptyStateLayout;
    TextView emptyStateMsg;
    TabLayout tabLayout;
    int currentTabPosition = 0;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_notices);

        recyclerView = findViewById(R.id.notice_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        emptyStateLayout = findViewById(R.id.empty_state_layout);
        emptyStateMsg = findViewById(R.id.empty_state_message);
        emptyStateMsg.setText(getString(R.string.empty_notices));

        tabLayout = findViewById(R.id.tab_layout);

        imageNoticesList = new ArrayList<>();
        textNoticesList = new ArrayList<>();
        
        imageAdapter = new NoticeAdapter(this, imageNoticesList);
        textAdapter = new TextNoticeAdapter(this, textNoticesList);

        recyclerView.setAdapter(imageAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTabPosition = tab.getPosition();
                updateRecyclerView();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        loadNotices();
    }

    private void updateRecyclerView() {
        if (currentTabPosition == 0) {
            recyclerView.setAdapter(imageAdapter);
            if (imageNoticesList.isEmpty()) {
                emptyStateLayout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                emptyStateLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        } else {
            recyclerView.setAdapter(textAdapter);
            if (textNoticesList.isEmpty()) {
                emptyStateLayout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                emptyStateLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void loadNotices() {

        FirebaseFirestore.getInstance()
                .collection("notices")
                .get()
                .addOnSuccessListener(q -> {

                    imageNoticesList.clear();
                    textNoticesList.clear();

                    long currentTime = System.currentTimeMillis();

                    for (DocumentSnapshot d : q) {

                        NoticeModel n = d.toObject(NoticeModel.class);

                        boolean isValid = false;
                        if (n.expiryTimestamp > 0) {
                            if (currentTime < n.expiryTimestamp) {
                                isValid = true;
                            }
                        } else {
                            // Backward compatibility
                            long diff = currentTime - n.timestamp;
                            if (diff <= 48L * 60 * 60 * 1000) {
                                isValid = true;
                            }
                        }

                        if (isValid) {
                            if ("text".equals(n.type)) {
                                textNoticesList.add(n);
                            } else {
                                imageNoticesList.add(n);
                            }
                        }
                    }

                    imageAdapter.notifyDataSetChanged();
                    textAdapter.notifyDataSetChanged();
                    updateRecyclerView();
                });
    }
}