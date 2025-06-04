package com.example.aibiotutor;

import android.content.Intent;
import android.graphics.Color;
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

import com.github.barteksc.pdfviewer.PDFView; // This is the PDF viewer library
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader; // Don't forget this!
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnatomyTheory extends AppCompatActivity implements TextToSpeech.OnInitListener, OnPageChangeListener {

    private PDFView pdfView; // Changed from ImageView to PDFView
    private TextToSpeech textToSpeech;
    private PDDocument document; // PDFBox document for text extraction
    private int currentPage = 0;
    private boolean isSpeaking = false;
    private File pdfFile;

    FloatingActionButton fabEdit, chatfab;
    EditText notes;

    // For pause/resume functionality
    private List<String> currentParagraphs = new ArrayList<>();
    private int currentParagraphIndex = 0;
    private boolean isTtsInitialized = false; // Flag to track TTS initialization

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#03A9F4")); // Or any color you prefer
        }

        setContentView(R.layout.activity_anatomy_theory);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Reproduction Theory"); // Set toolbar title
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        pdfView = findViewById(R.id.pdfView); // Initialize PDFView
        ImageView play = findViewById(R.id.playButton);
        ImageView pause = findViewById(R.id.pauseButton);
        ImageView stop = findViewById(R.id.stopButton);

        // Initialize TextToSpeech, passing 'this' as the OnInitListener
        textToSpeech = new TextToSpeech(this, this);

        // Crucial for PDFBox to load its resources on Android
        PDFBoxResourceLoader.init(getApplicationContext());
        // FAB AND EDIT
        fabEdit = findViewById(R.id.fabedit);
        notes = findViewById(R.id.notes);
        chatfab = findViewById(R.id.chatfab);

        // Onclicking teh fab icon
        chatfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chat = new Intent(AnatomyTheory.this, chatbot.class);
                startActivity(chat);
                finish();
            }
        });
        // Set an onclick listener for the floating action button
        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toggle the visibility of the edit text
                if (notes.getVisibility() == View.GONE) {
                    notes.setVisibility(View.VISIBLE);
                } else {
                    notes.setVisibility(View.GONE);

                    // Capture the text from the edit text and store it in the database
                    String noteText = notes.getText().toString();
                    storeNoteInDatabase(noteText);
                }
            }
        });



        // Copy PDF from assets to cache directory for PDFBox to load
        try {
            copyPdfFromAssets();
            document = PDDocument.load(pdfFile); // Load PDF with PDFBox for text extraction
            loadPdfIntoViewer(); // Load PDF into AndroidPdfViewer
        } catch (Exception e) {
            Toast.makeText(this, "Failed to load PDF", Toast.LENGTH_SHORT).show();
            Log.e("ReproductionTheory", "Error loading PDF: " + e.getMessage(), e);
            e.printStackTrace();
        }

        // Set UtteranceProgressListener
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                // No highlighting is being implemented as per request
            }

            @Override
            public void onDone(String utteranceId) {
                runOnUiThread(() -> {
                    // Check if the current utterance is the last paragraph of the page
                    if (utteranceId.startsWith("UTT_PAGE_") && currentParagraphIndex == currentParagraphs.size() - 1) {
                        // End of current page's speech
                        if (isSpeaking) { // Only advance if still in "speaking" state
                            currentPage++;
                            if (currentPage < document.getNumberOfPages()) {
                                pdfView.jumpTo(currentPage, true); // Auto-scroll to next page
                                // onPageChange will be triggered by jumpTo, which will then call prepareAndSpeakPage
                            } else {
                                // End of document
                                isSpeaking = false;
                                Toast.makeText(AnatomyTheory.this, "End of document", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else if (utteranceId.startsWith("UTT_PAGE_")) {
                        // Move to the next paragraph on the same page
                        currentParagraphIndex++;
                        if (isSpeaking) { // Only speak next paragraph if still in "speaking" state
                            speakNextParagraph();
                        }
                    }
                });
            }

            @Override
            public void onError(String utteranceId) {
                Log.e("TTS", "Error during speech for utteranceId: " + utteranceId);
                runOnUiThread(() -> Toast.makeText(AnatomyTheory.this, "Error speaking.", Toast.LENGTH_SHORT).show());
                isSpeaking = false; // Stop speaking on error
            }

            @Override
            public void onRangeStart(String utteranceId, int start, int end, int frame) {
                // No highlighting is being implemented as per request
            }
        });

        play.setOnClickListener(v -> {
            if (textToSpeech != null && isTtsInitialized) {
                if (!isSpeaking) {
                    isSpeaking = true;
                    if (currentParagraphs.isEmpty() || currentParagraphIndex >= currentParagraphs.size()) {
                        // If no text loaded for current page or finished current page, prepare and speak
                        prepareAndSpeakPage(currentPage);
                    } else {
                        // Resume from where it paused
                        speakNextParagraph();
                    }
                }
            } else {
                Toast.makeText(this, "TTS not ready or initialized", Toast.LENGTH_SHORT).show();
            }
        });

        pause.setOnClickListener(v -> {
            if (textToSpeech != null && textToSpeech.isSpeaking()) {
                textToSpeech.stop(); // Stops current utterance
                isSpeaking = false; // Set speaking state to false (paused)
                Toast.makeText(this, "Speech paused", Toast.LENGTH_SHORT).show();
            }
        });

        stop.setOnClickListener(v -> {
            if (textToSpeech != null) {
                textToSpeech.stop(); // Stops all utterances
                isSpeaking = false; // Set speaking state to false
                currentPage = 0;    // Reset page to the beginning
                currentParagraphs.clear(); // Clear paragraphs
                currentParagraphIndex = 0; // Reset paragraph index
                pdfView.jumpTo(currentPage, true); // Jump to the first page visually
                Toast.makeText(this, "Speech stopped, resetting to page 1", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void storeNoteInDatabase(String noteText) {
        // Create a URL object for the PHP script that will handle the request
        URL url = null;
        try {
            url = new URL("http://localhost/tutorApp/notes.php");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // Create a POST request to send the note text to the PHP script
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            // Convert the note text into a URL encoded string
            String urlEncodedText = URLEncoder.encode(noteText, "UTF-8");

            // Set the request body to the URL encoded note text
            connection.setDoOutput(true);
            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
            writer.write("note_text=" + urlEncodedText);
            writer.flush();
            writer.close();
            os.close();

            // Get the response from the PHP script
            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Handle the response from the PHP script
            if (response.toString().equals("Note stored successfully")) {
                Log.d("DEBUG", "Note stored successfully");
            } else {
                Log.e("ERROR", "Error storing note: " + response.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    // Implementation of TextToSpeech.OnInitListener
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Language not supported or missing data for " + Locale.US.getDisplayName());
                Toast.makeText(this, "TTS language not fully supported.", Toast.LENGTH_SHORT).show();
                textToSpeech.setLanguage(Locale.getDefault()); // Fallback
            }
            isTtsInitialized = true; // Set the flag when TTS is successfully initialized
            Log.d("TTS", "TTS Initialized Successfully!");
        } else {
            Toast.makeText(this, "TTS initialization failed", Toast.LENGTH_SHORT).show();
            Log.e("TTS", "Initialization failed: " + status);
            isTtsInitialized = false; // Ensure flag is false on failure
        }
    }

    // Implementation of OnPageChangeListener (from AndroidPdfViewer)
    @Override
    public void onPageChanged(int page, int pageCount) {
        currentPage = page; // Update current page based on PDFView's state
        if (isSpeaking) {
            // If the user manually scrolls, or if auto-advance triggered this,
            // prepare and speak the new page's content.
            prepareAndSpeakPage(currentPage);
        }
    }

    // Loads the PDF into the PDFView library
    private void loadPdfIntoViewer() {
        if (pdfFile != null && pdfFile.exists()) {
            pdfView.fromFile(pdfFile)
                    .enableSwipe(true)
                    .swipeHorizontal(false) // Vertical scrolling
                    .enableDoubletap(true)
                    .defaultPage(currentPage) // Set initial page
                    .onPageChange(this) // Set this activity as the OnPageChangeListener for auto-advancing
                    .enableAnnotationRendering(true) // Render annotations if any
                    .scrollHandle(new DefaultScrollHandle(this)) // Provides a default scroll bar
                    .spacing(10) // Space between pages in dp
                    .pageFitPolicy(FitPolicy.WIDTH) // Page will fit the width of the screen
                    .load();
        } else {
            Toast.makeText(this, "PDF file not found!", Toast.LENGTH_SHORT).show();
            Log.e("ReproductionTheory", "PDF file does not exist: " + (pdfFile != null ? pdfFile.getAbsolutePath() : "null"));
        }
    }

    // Extracts text from the current page using PDFBox and prepares it for TTS
    private void prepareAndSpeakPage(int page) {
        new Thread(() -> {
            try {
                PDFTextStripper stripper = new PDFTextStripper();
                stripper.setStartPage(page + 1); // PDFBox page numbers are 1-based
                stripper.setEndPage(page + 1);
                String currentFullPageText = stripper.getText(document); // Get the full text of the page

                runOnUiThread(() -> {
                    if (currentFullPageText != null && !currentFullPageText.trim().isEmpty()) {
                        currentParagraphs = splitTextIntoSpeakableChunks(currentFullPageText);
                        currentParagraphIndex = 0; // Start from the first paragraph of the new page
                        if (isSpeaking) { // Only speak if still in "speaking" state
                            speakNextParagraph();
                        }
                    } else {
                        Log.w("TTS", "No text found for page " + (page + 1) + ", advancing.");
                        runOnUiThread(() -> Toast.makeText(this, "No text on this page, advancing.", Toast.LENGTH_SHORT).show());
                        // Automatically advance if no text on current page
                        if (isSpeaking) {
                            currentPage++;
                            if (currentPage < document.getNumberOfPages()) {
                                pdfView.jumpTo(currentPage, true); // Auto-scroll to next page
                            } else {
                                isSpeaking = false; // End of document
                                Toast.makeText(this, "End of document", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

            } catch (Exception e) {
                Log.e("PDF_TEXT", "Error extracting text from page " + (page + 1), e);
                runOnUiThread(() -> Toast.makeText(this, "Error extracting text.", Toast.LENGTH_SHORT).show());
                isSpeaking = false; // Stop speaking on error
            }
        }).start();
    }

    // Speaks the next available paragraph chunk
    private void speakNextParagraph() {
        if (currentParagraphIndex < currentParagraphs.size()) {
            String paragraph = currentParagraphs.get(currentParagraphIndex);
            if (textToSpeech != null && isTtsInitialized) {
                // Use QUEUE_ADD to add the next chunk to the queue, ensuring continuity
                textToSpeech.speak(paragraph, TextToSpeech.QUEUE_ADD, null, "UTT_PAGE_" + currentPage + "_PARAGRAPH_" + currentParagraphIndex);
            }
        }
        // No else block needed; onDone for the last paragraph handles page advancement.
    }

    // Helper method to split text into manageable chunks (paragraphs/sentences)
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

        // Fallback: if no sensible chunks found, add the whole text as a single chunk.
        if (chunks.isEmpty() && !text.trim().isEmpty()) {
            chunks.add(text.trim());
        }

        return chunks;
    }

    // Copies the PDF from assets to the app's cache directory
    private void copyPdfFromAssets() throws Exception {
        InputStream inputStream = getAssets().open("anatomy.pdf");
        pdfFile = new File(getCacheDir(), "temp.pdf");
        FileOutputStream outputStream = new FileOutputStream(pdfFile);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }

        inputStream.close();
        outputStream.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release TTS resources
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        // Close PDFBox document
        if (document != null) {
            try {
                document.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Clean up temp PDF file if it exists
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