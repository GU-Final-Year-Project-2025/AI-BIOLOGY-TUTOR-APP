package com.example.aibiotutor;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class splash extends AppCompatActivity {
    private static final long SPLASH_SCREEN_DELAY = 4000; // 5 seconds in milliseconds


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Use a Handler to delay the start of LoginActivity
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(splash.this, getstarted.class);
            startActivity(intent);
            finish(); // Finish the splash screen activity so it can't be returned to
        }, SPLASH_SCREEN_DELAY);
    }
}