package com.example.aibiotutor;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class TicTacToeActivity extends AppCompatActivity {
    private char currentPlayer = 'X';
    private Button[][] buttons = new Button[3][3];
    private boolean gameActive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe);

        GridLayout grid = findViewById(R.id.grid);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                Button btn = new Button(this);
                btn.setTextSize(32);
                btn.setOnClickListener(this::onCellClick);
                buttons[row][col] = btn;
                grid.addView(btn, new GridLayout.LayoutParams(
                        GridLayout.spec(row), GridLayout.spec(col)));
            }
        }
    }

    private void onCellClick(View v) {
        Button btn = (Button) v;
        if (!btn.getText().toString().isEmpty() || !gameActive) return;

        btn.setText(String.valueOf(currentPlayer));
        if (checkWin()) {
            Toast.makeText(this, "Player " + currentPlayer + " wins!", Toast.LENGTH_LONG).show();
            gameActive = false;
            return;
        }
        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
    }

    private boolean checkWin() {
        for (int i = 0; i < 3; i++) {
            if (equal(buttons[i][0], buttons[i][1], buttons[i][2]) ||
                    equal(buttons[0][i], buttons[1][i], buttons[2][i])) return true;
        }
        return equal(buttons[0][0], buttons[1][1], buttons[2][2]) ||
                equal(buttons[0][2], buttons[1][1], buttons[2][0]);
    }

    private boolean equal(Button b1, Button b2, Button b3) {
        String s1 = b1.getText().toString();
        return !s1.isEmpty() && s1.equals(b2.getText().toString()) && s1.equals(b3.getText().toString());
    }
}
