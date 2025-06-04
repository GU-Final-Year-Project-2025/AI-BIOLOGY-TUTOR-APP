package com.example.aibiotutor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class respiration extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_respiration);

        // Toolbar back navigation
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Menu screen
                Intent intent = new Intent(respiration.this, menu.class); // Replace MenuActivity with your actual menu class
                startActivity(intent);
                finish(); // Optional: close current activity
            }
        });

        // Test button functionality
        Button testButton = findViewById(R.id.btn_tests);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reprodcution_intent = new Intent(respiration.this, Quiz.class);
                startActivity(reprodcution_intent);
            }
        });

        // Theory Button
        Button btn_theory = findViewById(R.id.btn_theory);
        btn_theory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent respiration_theory = new Intent(respiration.this, RespirationTheory.class);
                startActivity(respiration_theory);
            }
        });

        Button btn_textbooks = findViewById(R.id.btn_textbooks);
        btn_textbooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent online = new Intent(respiration.this, OnlineRespira.class );
                startActivity(online);
            }
        });

        Button btn_practicals = findViewById(R.id.btn_practicals);
        btn_practicals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent online = new Intent(respiration.this, RespirationVideos.class );
                startActivity(online);
            }
        });
    }
}
