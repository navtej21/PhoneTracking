package com.firstapp.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginPage extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText editTextEmail, editTextPassword;
    private Button buttonLoginUp;
    private TextView signupRedirectText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Find views by their IDs
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLoginUp = findViewById(R.id.buttonLoginUp);
        signupRedirectText = findViewById(R.id.signupredirectpage);

        // Set onClickListener for Login button
        buttonLoginUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    // Validate input fields are not empty
                    Toast.makeText(LoginPage.this, "Email or password is empty", Toast.LENGTH_SHORT).show();
                } else {
                    // Attempt to log in
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginPage.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Login success, redirect to the main activity
                                        Intent intent = new Intent(LoginPage.this, MainActivity.class);
                                        startActivity(intent);
                                        finish(); // Prevents the user from coming back to the login page using the back button
                                    } else {
                                        // If login fails, display a message to the user.
                                        Toast.makeText(LoginPage.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        }); // Added missing bracket

        // Set onClickListener for Sign Up redirect text
        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Redirect to the SignUp activity
                Intent intent = new Intent(LoginPage.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }
}
