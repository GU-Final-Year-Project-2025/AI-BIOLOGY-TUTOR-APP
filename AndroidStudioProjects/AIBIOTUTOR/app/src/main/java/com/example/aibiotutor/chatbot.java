package com.example.aibiotutor;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class chatbot extends AppCompatActivity {

    private ProgressBar mProgressBar;
    private WebView mWebView;
    private String urlToload = "https://chatgpt.com/c/6837c237-e7a4-8006-8dd8-6435cc410297";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#03A9F4"));
        }
        setContentView(R.layout.activity_chatbot);

        // Toolbar back navigation
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Menu screen
                Intent intent = new Intent(chatbot.this, menu.class); // Replace MenuActivity with your actual menu class
                startActivity(intent);
                finish(); // Optional: close current activity
            }
        });

        // Assign views
        mProgressBar = findViewById(R.id.loading_progressbar);
        mWebView = findViewById(R.id.webView);

        // Enable JavaScript
        mWebView.getSettings().setJavaScriptEnabled(true);

        // Load the URL
        mWebView.loadUrl(urlToload);

        // Set a WebViewClient
        mWebView.setWebViewClient(new WebViewClient());

        // Set a WebChromeClient to update ProgressBar and title
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                mProgressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    mProgressBar.setVisibility(ProgressBar.GONE);
                } else {
                    mProgressBar.setVisibility(ProgressBar.VISIBLE);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("AIBio Tutor");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
