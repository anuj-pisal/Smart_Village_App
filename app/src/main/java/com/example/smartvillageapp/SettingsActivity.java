package com.example.smartvillageapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private LinearLayout langEnglish, langMarathi;
    private ImageView checkEnglish, checkMarathi;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ImageView btnBack = findViewById(R.id.btn_back);
        langEnglish = findViewById(R.id.lang_english);
        langMarathi = findViewById(R.id.lang_marathi);
        checkEnglish = findViewById(R.id.check_english);
        checkMarathi = findViewById(R.id.check_marathi);

        // Show current selection
        updateSelection(LocaleHelper.getLanguage(this));

        btnBack.setOnClickListener(v -> finish());

        langEnglish.setOnClickListener(v -> changeLanguage("en"));
        langMarathi.setOnClickListener(v -> changeLanguage("mr"));
    }

    private void changeLanguage(String langCode) {
        String currentLang = LocaleHelper.getLanguage(this);

        if (currentLang.equals(langCode)) return;

        LocaleHelper.setLocale(this, langCode);
        updateSelection(langCode);

        String langName = langCode.equals("mr") ? "मराठी" : "English";
        Toast.makeText(this, getString(R.string.language_changed, langName), Toast.LENGTH_SHORT).show();

        // Restart the app to apply language everywhere
        Intent intent;
        if ("admin".equals(UserSession.role)) {
            intent = new Intent(this, AdminMainActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void updateSelection(String langCode) {
        if ("mr".equals(langCode)) {
            langMarathi.setBackgroundResource(R.drawable.bg_lang_selected);
            langEnglish.setBackgroundResource(R.drawable.bg_lang_unselected);
            checkMarathi.setVisibility(View.VISIBLE);
            checkEnglish.setVisibility(View.GONE);
        } else {
            langEnglish.setBackgroundResource(R.drawable.bg_lang_selected);
            langMarathi.setBackgroundResource(R.drawable.bg_lang_unselected);
            checkEnglish.setVisibility(View.VISIBLE);
            checkMarathi.setVisibility(View.GONE);
        }
    }
}
