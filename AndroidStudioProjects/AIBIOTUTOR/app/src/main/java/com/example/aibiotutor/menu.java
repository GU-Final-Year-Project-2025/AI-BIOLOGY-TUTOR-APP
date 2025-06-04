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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class menu extends AppCompatActivity {

    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#03A9F4"));
        }

        setContentView(R.layout.activity_menu);

        btn = findViewById(R.id.btn_learn_respiration);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent respiration = new Intent(menu.this,com.example.aibiotutor.respiration.class);
                startActivity(respiration);
            }
        });

        Button reproduction_btn = findViewById(R.id.btn_learn_reproduction);

        reproduction_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reprodcution_intent = new Intent(menu.this, reproduction.class);
                startActivity(reprodcution_intent);
            }
        });

        Button nutrition_btn = findViewById(R.id.btn_learn_nutrition);
        nutrition_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nutrition_intent = new Intent(menu.this, nutrition.class);
                startActivity(nutrition_intent);}
        });

        Button anatomy_btn = findViewById(R.id.btn_learn_anatomy);
        anatomy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nutrition_intent = new Intent(menu.this, anatomy.class);
                startActivity(nutrition_intent);}
        });
    }
}