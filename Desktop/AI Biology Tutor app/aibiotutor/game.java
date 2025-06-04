package com.example.aibiotutor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.MediaController;

import androidx.appcompat.app.AppCompatActivity;



public class game extends AppCompatActivity {
    private ImageButton ticTacToeBtn, reactionGameBtn, guessNumberBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game); // make sure this XML filename matches

        ticTacToeBtn = findViewById(R.id.ticTacToeIcon);
        reactionGameBtn = findViewById(R.id.reactionGameIcon);
        guessNumberBtn = findViewById(R.id.guessNumberIcon);

        ticTacToeBtn.setOnClickListener(v ->
                startActivity(new Intent(game.this, TicTacToeActivity.class)));



//        guessNumberBtn.setOnClickListener(v ->
//                startActivity(new Intent(MainActivity.this, GuessNumberActivity.class)));
//        VideoView videoView = findViewById(R.id.tutorialVideo);
//        String path = "android.resource://" + getPackageName() + "/" + R.raw.tutorial;
//        Uri uri = Uri.parse(path);
//        videoView.setVideoURI(uri);
//        videoView.setMediaController(new MediaController(this));
//        videoView.requestFocus();
//        videoView.start();

    }
}
