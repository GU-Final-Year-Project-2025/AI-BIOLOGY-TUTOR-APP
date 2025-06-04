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

public class OnlineAnatomy extends AppCompatActivity {

    private ProgressBar mProgressBar;
    private WebView mWebView;
    private String urlToload = "https://med.libretexts.org/Bookshelves/Anatomy_and_Physiology";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#03A9F4"));
        }
        setContentView(R.layout.activity_online_anatomy);

        // Toolbar back navigation
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Menu screen
                Intent intent = new Intent(OnlineAnatomy.this, anatomy.class); // Replace MenuActivity with your actual menu class
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
                    getSupportActionBar().setTitle(title);
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
