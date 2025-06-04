package com.example.aibiotutor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class nutrition extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nutrition);


        // Toolbar back navigation
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Menu screen
                Intent intent = new Intent(nutrition.this, menu.class); // Replace MenuActivity with your actual menu class
                startActivity(intent);
                finish(); // Optional: close current activity
            }
        });

        Button testButton = findViewById(R.id.btn_tests);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(nutrition.this, Quiz.class);
                startActivity(intent);
            }
        });

        Button btn_theory = findViewById(R.id.btn_theory);
        btn_theory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(nutrition.this, NutritionTheory.class);
                startActivity(intent);
            }
        });

        Button btn_textbooks = findViewById(R.id.btn_textbooks);
        btn_textbooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent online = new Intent(nutrition.this, OnlineNutri.class );
                startActivity(online);
            }
        });

        Button btn_practicals = findViewById(R.id.btn_practicals);
        btn_practicals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent online = new Intent(nutrition.this, NutritionVideos.class );
                startActivity(online);
            }
        });
    }
}
