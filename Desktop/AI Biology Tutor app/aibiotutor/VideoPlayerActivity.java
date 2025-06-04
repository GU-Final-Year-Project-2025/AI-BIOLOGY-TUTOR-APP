package com.example.aibiotutor;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;
import java.util.List;

public class VideoPlayerActivity extends AppCompatActivity {

    PlayerView playerView;
    ExoPlayer player;
    List<Integer> videoResIds;
    int currentIndex = 0;

    FloatingActionButton chatfab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        playerView = findViewById(R.id.videoView);

        videoResIds = Arrays.asList(R.raw.reproduction, R.raw.reproductionplants, R.raw.reproduction);
        currentIndex = getIntent().getIntExtra("index", 0);

        initializePlayer();

        findViewById(R.id.btnNext).setOnClickListener(v -> {
            if (currentIndex < videoResIds.size() - 1) {
                currentIndex++;
                playVideo(videoResIds.get(currentIndex));
            }
        });



        findViewById(R.id.btnPrevious).setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                playVideo(videoResIds.get(currentIndex));
            }
        });

        findViewById(R.id.btnRewind).setOnClickListener(v -> player.seekBack());
        findViewById(R.id.btnForward).setOnClickListener(v -> player.seekForward());
        findViewById(R.id.btnFullscreen).setOnClickListener(v -> toggleFullscreen());
    }

    private void initializePlayer() {
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);
        playVideo(videoResIds.get(currentIndex));
    }

    private void playVideo(int resId) {
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + resId);
        player.setMediaItem(MediaItem.fromUri(videoUri));
        player.prepare();
        player.play();
    }

    private void toggleFullscreen() {
        int orientation = getResources().getConfiguration().orientation;
        setRequestedOrientation(orientation == Configuration.ORIENTATION_PORTRAIT ?
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE :
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onStop() {
        super.onStop();
        player.release();
    }
}
