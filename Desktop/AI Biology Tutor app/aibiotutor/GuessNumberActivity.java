package com.example.aibiotutor;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

public class GuessNumberActivity extends AppCompatActivity {
    private int numberToGuess;
    private EditText inputGuess;
    private TextView hintText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_number);

        inputGuess = findViewById(R.id.inputGuess);
        hintText = findViewById(R.id.hintText);
        Button checkButton = findViewById(R.id.checkButton);

        numberToGuess = new Random().nextInt(100) + 1;

        checkButton.setOnClickListener(v -> {
            String input = inputGuess.getText().toString();
            if (input.isEmpty()) return;

            int guess = Integer.parseInt(input);
            if (guess == numberToGuess) {
                Toast.makeText(this, "Correct! You guessed it!", Toast.LENGTH_SHORT).show();
                numberToGuess = new Random().nextInt(100) + 1; // new round
                hintText.setText("New number. Try again!");
            } else if (guess < numberToGuess) {
                hintText.setText("Too low. Try again!");
            } else {
                hintText.setText("Too high. Try again!");
            }
        });
    }
}
