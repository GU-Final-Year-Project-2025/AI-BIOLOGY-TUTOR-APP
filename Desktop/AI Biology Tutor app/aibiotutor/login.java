package com.example.aibiotutor;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast; // For showing success/error messages

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

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

public class login extends AppCompatActivity { // Renamed from 'login' for clarity

    private EditText etUsername, etPassword;
    private AppCompatButton btnLogin, btnForgotPassword, btnRegister;

    private static final String BASE_URL = "http://192.168.160.76/tutorApp/";
    private static final String LOGIN_URL = BASE_URL + "login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Assuming your XML is activity_login.xml

        // Apply status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#03A9F4")); // Your desired color
        }

        // Initialize UI elements
        etUsername = findViewById(R.id.username); // Corresponds to <EditText android:id="@+id/username" .../>
        etPassword = findViewById(R.id.password); // Corresponds to <EditText android:id="@+id/password" .../>
        btnLogin = findViewById(R.id.btn_login);
        btnForgotPassword = findViewById(R.id.btn_forgot_password);
        btnRegister = findViewById(R.id.btn_register);

        // Set OnClickListener for Login button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        // Set OnClickListener for Register button
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to your RegisterActivity
                startActivity(new Intent(login.this, Register.class));
            }
        });

        // Set OnClickListener for Forgot Password button (Optional: implement this later)
        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(login.this, "Forgot Password functionality coming soon!", Toast.LENGTH_SHORT).show();
                // You would typically navigate to a ForgotPasswordActivity here
            }
        });
    }

    private void attemptLogin() {
        String identifier = etUsername.getText().toString().trim(); // Can be username or email
        String password = etPassword.getText().toString().trim();

        // Input validation
        if (identifier.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter your username/email and password.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create an OkHttpClient instance
        OkHttpClient client = new OkHttpClient();

        // Create JSON request body
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("identifier", identifier); // Key must match PHP script
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            runOnUiThread(() -> Toast.makeText(login.this, "Error creating request data.", Toast.LENGTH_SHORT).show());
            return;
        }

        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);

        // Build the request
        Request request = new Request.Builder()
                .url(LOGIN_URL)
                .post(body)
                .build();

        // Execute the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(login.this, "Network error: " + e.getMessage(), Toast.LENGTH_LONG).show());
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
                                Toast.makeText(login.this, message, Toast.LENGTH_LONG).show();
                                // Successful login: Navigate to your main app content (e.g., DashboardActivity)
                                // Optionally parse and use user data here:
                                if (jsonResponse.has("user")) {
                                    try {
                                        JSONObject userObject = jsonResponse.getJSONObject("user");
                                        String username = userObject.getString("username");
                                        String email = userObject.getString("email");
                                        String userClass = userObject.getString("class");
                                        // Save user data to SharedPreferences or pass via Intent
                                        // for subsequent app usage.
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                startActivity(new Intent(login.this, menu.class)); // Replace MainActivity.class with your actual main activity
                                finish(); // Close Login activity
                            } else {
                                Toast.makeText(login.this, "Login failed: " + message, Toast.LENGTH_LONG).show();
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(login.this, "Error parsing server response.", Toast.LENGTH_LONG).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(login.this, "Server error: " + response.code(), Toast.LENGTH_LONG).show());
                }
            }
        });
    }
}