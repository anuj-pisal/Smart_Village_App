package com.example.smartvillageapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.smartvillageapp.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ViewPager2 viewPager;
    RecyclerView recyclerView;
    View headerView;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        headerView = navigationView.getHeaderView(0);
        viewPager = findViewById(R.id.imageSlider);
        recyclerView = findViewById(R.id.dashboardRecycler);



        hamburgerHandler();
        sideBarMenuHandler();
        imageSlider();
        mainGrid();
    }

    public void mainGrid() {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        List<DashboardItem> list = new ArrayList<>();

        list.add(new DashboardItem(R.drawable.home, "About Village"));
        list.add(new DashboardItem(R.drawable.contacts, "Contacts"));
        list.add(new DashboardItem(R.drawable.business, "Businesses"));
        list.add(new DashboardItem(R.drawable.market_prices, "Market Prices"));
        list.add(new DashboardItem(R.drawable.notices, "Notices"));
        list.add(new DashboardItem(R.drawable.bill, "Bills"));
        list.add(new DashboardItem(R.drawable.locations, "Locations"));
        list.add(new DashboardItem(R.drawable.complaints, "Complaints"));
        list.add(new DashboardItem(R.drawable.schemes, "Schemes"));
        list.add(new DashboardItem(R.drawable.development, "Development"));
        list.add(new DashboardItem(R.drawable.agriculture, "Agricultural"));
        list.add(new DashboardItem(R.drawable.jobs, "Jobs"));

        DashboardAdapter adapter = new DashboardAdapter(this, list);
        recyclerView.setAdapter(adapter);
    }

    public void imageSlider() {
        List<Integer> images = new ArrayList<>();
        images.add(R.drawable.slide6);
        images.add(R.drawable.slide1);
        images.add(R.drawable.slide2);
        images.add(R.drawable.slide3);
        images.add(R.drawable.slide4);
        images.add(R.drawable.slide5);
        images.add(R.drawable.slide6);
        images.add(R.drawable.slide1);

        ImageSliderAdapter adapter = new ImageSliderAdapter(images);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1, false);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);

                if (state == ViewPager2.SCROLL_STATE_IDLE) {

                    if (viewPager.getCurrentItem() == 0) {
                        viewPager.setCurrentItem(images.size() - 2, false);
                    }

                    if (viewPager.getCurrentItem() == images.size() - 1) {
                        viewPager.setCurrentItem(1, false);
                    }
                }
            }
        });

    }

    public void sideBarMenuHandler() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Toast.makeText(this, "Home Clicked", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_about_app) {
                Toast.makeText(this, "About App Clicked", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_profile) {
                Toast.makeText(this, "Profile Clicked", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_logout) {
                new AlertDialog.Builder(this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(MainActivity.this, LoginPage.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }else if (id == R.id.nav_exit) {
                Toast.makeText(this, "Exit Clicked", Toast.LENGTH_SHORT).show();
                this.finishAffinity();
            }
            drawerLayout.closeDrawers();
            return true;
        });
    }

    public void hamburgerHandler() {
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.open_drawer,
                R.string.close_drawer);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // showing username and email in side drawer
        TextView nameText = headerView.findViewById(R.id.user_name_nav);
        TextView emailText = headerView.findViewById(R.id.user_email_nav);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users")
                    .document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String username = documentSnapshot.getString("username");
                            String email = documentSnapshot.getString("email");
                            nameText.setText(username);
                            emailText.setText(email);
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(MainActivity.this,
                                    "Failed to load user data",
                                    Toast.LENGTH_SHORT).show()
                    );
        }
    }
}