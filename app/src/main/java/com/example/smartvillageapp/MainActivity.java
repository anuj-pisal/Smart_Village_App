package com.example.smartvillageapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.view.GravityCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.bumptech.glide.Glide;


public class MainActivity extends BaseActivity implements ProfileFragment.OnProfileUpdatedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    View headerView;

    @Override
    public void onProfileUpdated() {
        loadUserData();
    }

    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        headerView = navigationView.getHeaderView(0);

        hamburgerHandler();
        sideBarMenuHandler();
        loadUserData();

        // 🔥 Prefetch Market Prices silently in background
        MarketPrefetcher.fetchAndCache();

        // Load HomeFragment by default
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }
    }

    private void sideBarMenuHandler() {

        navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home) {

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment())
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

                Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_logout);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCancelable(true);

                Button btnCancel = dialog.findViewById(R.id.btn_cancel);
                Button btnLogout = dialog.findViewById(R.id.btn_logout);

                btnCancel.setOnClickListener(v -> dialog.dismiss());

                btnLogout.setOnClickListener(v -> {
                    dialog.dismiss();
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(MainActivity.this, LoginPage.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                });

                dialog.show();

                if (dialog.getWindow() != null) {
                    dialog.getWindow().setLayout(
                            (int)(getResources().getDisplayMetrics().widthPixels * 0.8),
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                }

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
                R.string.close_drawer);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void loadUserData() {

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

                            // Load profile image safely
                            if (imageUrl != null && !imageUrl.isEmpty()) {

                                Glide.with(MainActivity.this)
                                        .load(imageUrl)
                                        .circleCrop() // makes it round
                                        .into(imageView);

                            } else {

                                // Optional: default image
                                imageView.setImageResource(R.drawable.profile_vec);
                            }
                        }
                    });
        }
    }

}
