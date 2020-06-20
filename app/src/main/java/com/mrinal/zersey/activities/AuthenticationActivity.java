package com.mrinal.zersey.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.mrinal.zersey.R;

import static com.mrinal.zersey.helpers.Dialogs.showErrorDialog;
import static com.mrinal.zersey.helpers.Dialogs.showProgressDialog;
import static com.mrinal.zersey.helpers.InputValidator.validateEmail;
import static com.mrinal.zersey.helpers.InputValidator.validateName;
import static com.mrinal.zersey.helpers.InputValidator.validatePassword;

@SuppressWarnings({"ConstantConditions"})
public class AuthenticationActivity extends AppCompatActivity {
    EditText loginEmailEt;
    EditText loginPasswordEt;
    EditText nameEt;
    EditText signUpEmailEt;
    EditText signUpPasswordEt;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null)
            startHomeActivity();
        else {
            loginEmailEt = findViewById(R.id.login_email_et);
            loginPasswordEt = findViewById(R.id.login_password_et);
            nameEt = findViewById(R.id.name_et);
            signUpEmailEt = findViewById(R.id.sign_up_email_et);
            signUpPasswordEt = findViewById(R.id.sign_up_password_et);
        }
    }

    public void login(View view) {
        if (validateEmail(loginEmailEt) && validatePassword(loginPasswordEt)) {
            showProgress("Logging you in...");
            firebaseAuth.signInWithEmailAndPassword(loginEmailEt.getText().toString().trim(), loginPasswordEt.getText().toString().trim())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                showToast("Logged In Successful!");
                                startHomeActivity();
                            } else
                                showError("Login Failed!", task.getException().getMessage());
                        }
                    });
        }
    }

    public void signUp(View view) {
        if (validateName(nameEt) && validateEmail(signUpEmailEt) && validatePassword(signUpPasswordEt)) {
            showProgress("Creating account...");
            firebaseAuth.createUserWithEmailAndPassword(signUpEmailEt.getText().toString().trim(), signUpPasswordEt.getText().toString().trim())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                showToast("Registration Successful!");
                                updateName();
                                startHomeActivity();
                            } else
                                showError("Registration Failed!", task.getException().getMessage());
                        }
                    });
        }
    }

    private void updateName() {
        String name = nameEt.getText().toString().trim();
        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(name).build();
        firebaseAuth.getCurrentUser().updateProfile(userProfileChangeRequest);
    }

    private void startHomeActivity() {
        startActivity(new Intent(this, HomeActivity.class));
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showProgress(String message) {
        progressDialog = showProgressDialog(this, progressDialog, message);
    }

    private void showError(String title, String message) {
        showErrorDialog(this, progressDialog, title, message + R.string.try_later);
    }

}