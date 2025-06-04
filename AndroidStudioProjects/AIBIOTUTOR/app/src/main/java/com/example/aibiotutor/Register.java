package com.example.aibiotutor;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast; // For showing success/error messages

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject; // For parsing JSON response

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Register extends AppCompatActivity {

    private TextInputEditText etUsername, etEmail, etPassword, etConfirmPassword, etClass;
    private AppCompatButton btnCreate, btnBackToLogin;

    // Base URL for your PHP scripts
    // IMPORTANT: Replace with your actual IP address or domain!
    // For local testing, use your computer's IP address (e.g., 192.168.1.X)
    // NOT "localhost" or "127.0.0.1" from the Android emulator.
    private static final String BASE_URL = "http://192.168.137.1/tutorApp/";
    private static final String REGISTER_URL = BASE_URL + "register.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this); // Keep if you want edge-to-edge
        setContentView(R.layout.activity_register); // Assuming your XML is activity_register

        // Apply status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#03A9F4")); // Your desired color
        }

        // Initialize UI elements
        etUsername = findViewById(R.id.et_username);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        etClass = findViewById(R.id.et_class);
        btnCreate = findViewById(R.id.btn_create);
        btnBackToLogin = findViewById(R.id.btn_back_to_login);

        // Set OnClickListener for Create Account button
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        // Set OnClickListener for Back to Login button
        btnBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to LoginActivity (assuming you have one)
                startActivity(new Intent(Register.this, login.class));
                finish(); // Close Register activity
            }
        });
    }

    private void registerUser() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String studentClass = etClass.getText().toString().trim(); // Using studentClass to avoid conflict with Java 'class' keyword

        // Input validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create an OkHttpClient instance
        OkHttpClient client = new OkHttpClient();

        // Create JSON request body
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
            jsonObject.put("email", email);
            jsonObject.put("password", password);
            jsonObject.put("class", studentClass); // Send 'class' key to PHP
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating request data.", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);

        // Build the request
        Request request = new Request.Builder()
                .url(REGISTER_URL)
                .post(body)
                .build();

        // Execute the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(Register.this, "Network error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        boolean success = jsonResponse.getBoolean("success");
                        String message = jsonResponse.getString("message");

                        runOnUiThread(() -> {
                            if (success) {
                                Toast.makeText(Register.this, message, Toast.LENGTH_LONG).show();
                                // Optionally navigate to Login screen or home screen
                                startActivity(new Intent(Register.this, login.class)); // Go to Login
                                finish(); // Close registration activity
                            } else {
                                Toast.makeText(Register.this, "Registration failed: " + message, Toast.LENGTH_LONG).show();
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(Register.this, "Error parsing server response.", Toast.LENGTH_LONG).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(Register.this, "Server error: " + response.code(), Toast.LENGTH_LONG).show());
                }
            }
        });
    }
}