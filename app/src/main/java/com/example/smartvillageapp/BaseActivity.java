package com.example.smartvillageapp;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Base Activity to handle automatic application of the selected locale.
 * All application activities should extend this to ensure language persistence.
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase));
    }
}
