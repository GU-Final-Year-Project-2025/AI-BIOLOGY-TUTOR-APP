package com.example.aibiotutor;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class forgotpassword extends AppCompatActivity { // Use consistent naming: ForgotPasswordActivity

    private TextInputEditText etUsernameOrEmail, etNewPassword, etConfirmPassword;
    private AppCompatButton btnSubmit, btnBackToLogin;

    // IMPORTANT: Replace with your actual IP address or domain!
    private static final String BASE_URL = "http://192.168.160.76/tutorApp/";
    private static final String FORGOT_PASSWORD_URL = BASE_URL + "forgot_password.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword); // Ensure this matches your XML file name

        // Apply status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#03A9F4")); // Your desired color
        }

        // Initialize UI elements
        etUsernameOrEmail = findViewById(R.id.et_username); // Using et_username for email input as per your XML
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnSubmit = findViewById(R.id.btn_submit);
        btnBackToLogin = findViewById(R.id.btn_back_to_login);

        // Set OnClickListener for Submit button
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

        // Set OnClickListener for Back to Login button
        btnBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to LoginActivity
                startActivity(new Intent(forgotpassword.this, login.class));
                finish(); // Close ForgotPassword activity
            }
        });
    }

    private void resetPassword() {
        String email = etUsernameOrEmail.getText().toString().trim(); // Getting email from et_username field
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Input validation
        if (email.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "New passwords do not match.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create an OkHttpClient instance
        OkHttpClient client = new OkHttpClient();

        // Create JSON request body
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("new_password", newPassword);
            jsonObject.put("confirm_password", confirmPassword);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating request data.", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);

        // Build the request
        Request request = new Request.Builder()
                .url(FORGOT_PASSWORD_URL)
                .post(body)
                .build();

        // Execute the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(forgotpassword.this, "Network error: " + e.getMessage(), Toast.LENGTH_LONG).show());
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
                                Toast.makeText(forgotpassword.this, message, Toast.LENGTH_LONG).show();
                                // Password updated successfully, navigate back to Login
                                startActivity(new Intent(forgotpassword.this, login.class));
                                finish();
                            } else {
                                Toast.makeText(forgotpassword.this, "Password reset failed: " + message, Toast.LENGTH_LONG).show();
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(forgotpassword.this, "Error parsing server response.", Toast.LENGTH_LONG).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(forgotpassword.this, "Server error: " + response.code(), Toast.LENGTH_LONG).show());
                }
            }
        });
    }
}