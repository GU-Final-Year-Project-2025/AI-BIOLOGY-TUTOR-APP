package com.example.aibiotutor;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class reproduction extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#03A9F4"));
        }
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reproduction);

        // Toolbar back navigation
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Menu screen
                Intent intent = new Intent(reproduction.this, menu.class); // Replace MenuActivity with your actual menu class
                startActivity(intent);
                finish(); // Optional: close current activity
            }
        });

        Button testButton= findViewById(R.id.btn_tests);

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(reproduction.this, Quiz.class);
                startActivity(intent);
            }
        });

        Button btn_theory = findViewById(R.id.btn_theory);
        btn_theory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(reproduction.this, ReproductionTheory.class);
                startActivity(intent);
            }
        });

        // online resources
        Button btn_textbooks = findViewById(R.id.btn_textbooks);
        btn_textbooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent online = new Intent(reproduction.this, OnlineRepro.class );
                startActivity(online);
            }
        });

        Button btn_practicals = findViewById(R.id.btn_practicals);
        btn_practicals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent online = new Intent(reproduction.this, ReproductionVideos.class );
                startActivity(online);
            }
        });

    }
}