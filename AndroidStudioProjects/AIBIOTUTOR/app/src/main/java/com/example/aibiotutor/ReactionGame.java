package com.example.aibiotutor;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;
import java.util.Random;

public class ReactionGame extends AppCompatActivity {

    private View gameBox;
    private TextView resultTextView;

    private long startTime = 0;
    private boolean canTap = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reaction_game);

        gameBox = findViewById(R.id.gameBox);
        resultTextView = findViewById(R.id.resultTextView);

        startGame();
    }

    private void startGame() {
        resultTextView.setText("Wait for green...");
        gameBox.setBackgroundColor(Color.RED);
        canTap = false;

        new Handler().postDelayed(() -> {
            gameBox.setBackgroundColor(Color.GREEN);
            startTime = System.currentTimeMillis();
            canTap = true;
        }, new Random().nextInt(3000) + 2000); // 2 to 5 seconds
    }

    public void boxTapped(View view) {
        if (!canTap) {
            resultTextView.setText("Too soon! Try again.");
            startGame();
        } else {
            long reactionTime = System.currentTimeMillis() - startTime;
            resultTextView.setText(String.format(Locale.getDefault(), "Reaction Time: %d ms", reactionTime));
            canTap = false;
        }
    }
}
