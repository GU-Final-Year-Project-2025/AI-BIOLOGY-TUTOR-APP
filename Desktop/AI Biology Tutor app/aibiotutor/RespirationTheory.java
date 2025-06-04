package com.example.aibiotutor;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NutritionTheory extends AppCompatActivity implements TextToSpeech.OnInitListener, OnPageChangeListener {

    // UI Components
    private PDFView pdfView;
    private TextToSpeech textToSpeech;
    private FloatingActionButton fabEdit, chatFab;
    private EditText notes;
    private ImageView playButton, pauseButton, stopButton;

    // PDF Related
    private PDDocument document;
    private File pdfFile;
    private int currentPage = 0;

    // TTS Related
    private List<String> currentParagraphs = new ArrayList<>();
    private int currentParagraphIndex = 0;
    private boolean isSpeaking = false;
    private boolean isTtsInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupWindow();
        setContentView(R.layout.activity_nutrition_theory);
        setupToolbar();
        initializeComponents();
        setupListeners();
        initializePdfViewer();
    }

    private void setupWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#03A9F4"));
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Nutrition Theory");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void initializeComponents() {
        pdfView = findViewById(R.id.pdfView);
        playButton = findViewById(R.id.playButton);
        pauseButton = findViewById(R.id.pauseButton);
        stopButton = findViewById(R.id.stopButton);
        fabEdit = findViewById(R.id.fabedit);
        chatFab = findViewById(R.id.chatfab);
        notes = findViewById(R.id.notes);
        textToSpeech = new TextToSpeech(this, this);
        PDFBoxResourceLoader.init(getApplicationContext());
    }

    private void setupListeners() {
        // Chat FAB - Opens ChatGPT in browser
        chatFab.setOnClickListener(v -> openChatGPT());

        // Notes FAB
        fabEdit.setOnClickListener(v -> toggleNotesVisibility());

        // TTS Controls
        playButton.setOnClickListener(v -> handlePlay());
        pauseButton.setOnClickListener(v -> handlePause());
        stopButton.setOnClickListener(v -> handleStop());
    }

    private void openChatGPT() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://chat.openai.com/"));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Error opening ChatGPT", Toast.LENGTH_SHORT).show();
            Log.e("ChatGPT", "Error opening browser", e);
        }
    }

    private void toggleNotesVisibility() {
        if (notes.getVisibility() == View.GONE) {
            notes.setVisibility(View.VISIBLE);
        } else {
            notes.setVisibility(View.GONE);
            storeNoteInDatabase(notes.getText().toString());
        }
    }

    private void initializePdfViewer() {
        try {
            copyPdfFromAssets();
            document = PDDocument.load(pdfFile);
            loadPdfIntoViewer();
        } catch (Exception e) {
            handlePdfError(e);
        }
    }

    private void copyPdfFromAssets() throws IOException {
        InputStream inputStream = getAssets().open("Nutrition.pdf");
        pdfFile = new File(getCacheDir(), "temp.pdf");
        try (FileOutputStream outputStream = new FileOutputStream(pdfFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        } finally {
            inputStream.close();
        }
    }

    private void loadPdfIntoViewer() {
        if (pdfFile.exists()) {
            pdfView.fromFile(pdfFile)
                    .enableSwipe(true)
                    .swipeHorizontal(false)
                    .enableDoubletap(true)
                    .defaultPage(currentPage)
                    .onPageChange(this)
                    .enableAnnotationRendering(true)
                    .scrollHandle(new DefaultScrollHandle(this))
                    .spacing(10)
                    .pageFitPolicy(FitPolicy.WIDTH)
                    .load();
        } else {
            Toast.makeText(this, "PDF file not found!", Toast.LENGTH_SHORT).show();
        }
    }

    private void handlePlay() {
        if (textToSpeech != null && isTtsInitialized) {
            if (!isSpeaking) {
                isSpeaking = true;
                if (currentParagraphs.isEmpty() || currentParagraphIndex >= currentParagraphs.size()) {
                    prepareAndSpeakPage(currentPage);
                } else {
                    speakNextParagraph();
                }
            }
        } else {
            Toast.makeText(this, "TTS not ready", Toast.LENGTH_SHORT).show();
        }
    }

    private void handlePause() {
        if (textToSpeech != null && textToSpeech.isSpeaking()) {
            textToSpeech.stop();
            isSpeaking = false;
            Toast.makeText(this, "Speech paused", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleStop() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            resetTtsState();
            pdfView.jumpTo(0, true);
            Toast.makeText(this, "Speech stopped", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetTtsState() {
        isSpeaking = false;
        currentPage = 0;
        currentParagraphs.clear();
        currentParagraphIndex = 0;
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Language not supported");
                textToSpeech.setLanguage(Locale.getDefault());
            }
            isTtsInitialized = true;
            setupTtsListener();
        } else {
            Toast.makeText(this, "TTS initialization failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupTtsListener() {
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {}

            @Override
            public void onDone(String utteranceId) {
                runOnUiThread(() -> handleTtsCompletion(utteranceId));
            }

            @Override
            public void onError(String utteranceId) {
                Log.e("TTS", "Error during speech");
                runOnUiThread(() -> {
                    isSpeaking = false;
                    Toast.makeText(NutritionTheory.this, "Error speaking", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void handleTtsCompletion(String utteranceId) {
        if (utteranceId.startsWith("UTT_PAGE_") && currentParagraphIndex == currentParagraphs.size() - 1) {
            if (isSpeaking) {
                currentPage++;
                if (currentPage < document.getNumberOfPages()) {
                    pdfView.jumpTo(currentPage, true);
                } else {
                    isSpeaking = false;
                    Toast.makeText(this, "End of document", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (utteranceId.startsWith("UTT_PAGE_")) {
            currentParagraphIndex++;
            if (isSpeaking) {
                speakNextParagraph();
            }
        }
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        currentPage = page;
        if (isSpeaking) {
            prepareAndSpeakPage(currentPage);
        }
    }

    private void prepareAndSpeakPage(int page) {
        new Thread(() -> {
            try {
                PDFTextStripper stripper = new PDFTextStripper();
                stripper.setStartPage(page + 1);
                stripper.setEndPage(page + 1);
                String pageText = stripper.getText(document);

                runOnUiThread(() -> {
                    if (pageText != null && !pageText.trim().isEmpty()) {
                        currentParagraphs = splitTextIntoSpeakableChunks(pageText);
                        currentParagraphIndex = 0;
                        if (isSpeaking) {
                            speakNextParagraph();
                        }
                    } else {
                        handleEmptyPage();
                    }
                });
            } catch (Exception e) {
                handlePdfTextExtractionError(e);
            }
        }).start();
    }

    private void handleEmptyPage() {
        Log.w("TTS", "No text found for page " + (currentPage + 1));
        Toast.makeText(this, "No text on this page", Toast.LENGTH_SHORT).show();
        if (isSpeaking) {
            currentPage++;
            if (currentPage < document.getNumberOfPages()) {
                pdfView.jumpTo(currentPage, true);
            } else {
                isSpeaking = false;
                Toast.makeText(this, "End of document", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void speakNextParagraph() {
        if (currentParagraphIndex < currentParagraphs.size()) {
            String paragraph = currentParagraphs.get(currentParagraphIndex);
            textToSpeech.speak(paragraph, TextToSpeech.QUEUE_ADD, null,
                    "UTT_PAGE_" + currentPage + "_PARAGRAPH_" + currentParagraphIndex);
        }
    }

    private List<String> splitTextIntoSpeakableChunks(String text) {
        List<String> chunks = new ArrayList<>();
        Pattern pattern = Pattern.compile("([^.!?\\n]*[.!?\\n])\\s*|([^\\n]+(?:\\n(?!\\s*\\n)[^\\n]*)*)");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String chunk = matcher.group(0).trim();
            if (!chunk.isEmpty()) {
                chunks.add(chunk);
            }
        }

        if (chunks.isEmpty() && !text.trim().isEmpty()) {
            chunks.add(text.trim());
        }

        return chunks;
    }

    private void storeNoteInDatabase(String noteText) {
        // Implement your database storage logic here
        // This could be Room, SQLite, or network call
        // Placeholder for your actual implementation
        Log.d("Notes", "Note to store: " + noteText);
    }

    private void handlePdfError(Exception e) {
        Toast.makeText(this, "Failed to load PDF", Toast.LENGTH_SHORT).show();
        Log.e("PDF Error", "Error loading PDF", e);
    }

    private void handlePdfTextExtractionError(Exception e) {
        Log.e("PDF Text", "Error extracting text", e);
        runOnUiThread(() -> {
            Toast.makeText(this, "Error extracting text", Toast.LENGTH_SHORT).show();
            isSpeaking = false;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseResources();
    }

    private void releaseResources() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        if (document != null) {
            try {
                document.close();
            } catch (IOException e) {
                Log.e("PDF Close", "Error closing document", e);
            }
        }
        if (pdfFile != null && pdfFile.exists()) {
            pdfFile.delete();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}