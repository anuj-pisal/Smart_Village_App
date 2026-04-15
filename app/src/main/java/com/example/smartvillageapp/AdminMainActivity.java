package com.example.smartvillageapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.view.GravityCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.bumptech.glide.Glide;

public class AdminMainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    View headerView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        headerView = navigationView.getHeaderView(0);

        hamburgerHandler();
        sideBarMenuHandler();
        loadAdminData();

        // 🔥 Load Admin Dashboard by default
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AdminDashboardFragment())
                    .commit();
        }
    }

    private void sideBarMenuHandler() {

        navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home) {

                // 🔥 Load Admin Dashboard (NOT HomeFragment)
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new AdminDashboardFragment())
                        .commit();

            } else if (id == R.id.nav_about_app) {

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new AboutFragment())
                        .commit();

            } else if (id == R.id.nav_profile) {

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ProfileFragment())
                        .commit();

            } else if (id == R.id.nav_logout) {

                new AlertDialog.Builder(this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(AdminMainActivity.this, LoginPage.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        })
                        .setNegativeButton("Cancel", null)
                        .show();

            } else if (id == R.id.nav_exit) {
                finishAffinity();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void hamburgerHandler() {

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.open_drawer,
                R.string.close_drawer
        );

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    // 🔥 Same as user, but renamed for clarity
    private void loadAdminData() {

        TextView nameText = headerView.findViewById(R.id.user_name_nav);
        TextView emailText = headerView.findViewById(R.id.user_email_nav);
        ImageView imageView = headerView.findViewById(R.id.user_image_nav);

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
                            String imageUrl = documentSnapshot.getString("profileImage");

                            nameText.setText(username);
                            emailText.setText(currentUser.getEmail());

                            if (imageUrl != null && !imageUrl.isEmpty()) {

                                Glide.with(AdminMainActivity.this)
                                        .load(imageUrl)
                                        .circleCrop()
                                        .into(imageView);

                            } else {
                                imageView.setImageResource(R.drawable.profile_vec);
                            }
                        }
                    });
        }
    }
}