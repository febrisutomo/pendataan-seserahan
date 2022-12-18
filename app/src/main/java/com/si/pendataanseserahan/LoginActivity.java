package com.si.pendataanseserahan;

import static android.util.Patterns.EMAIL_ADDRESS;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser user;

    Button btnLogin;
    TextView btnRegister;
    EditText etEmail, etPassword;

    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mAuth = FirebaseAuth.getInstance();

        user = mAuth.getCurrentUser();


        if (user != null && user.isEmailVerified()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        etEmail = findViewById(R.id.email);
        etPassword = findViewById(R.id.password);

        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);


        progress = new ProgressDialog(this);
        progress.setMessage("Harap tunggu sebentar...");
        progress.setCancelable(false);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void login() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (email.isEmpty()) {
            etEmail.setError("Email tidak boleh kosong.");
            etEmail.requestFocus();
        } else if (!EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email tidak valid.");
            etEmail.requestFocus();
        } else if (password.isEmpty()) {
            etPassword.setError("Password tidak boleh kosong.");
            etPassword.requestFocus();
        } else {
            progress.show();
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progress.dismiss();
                    if (task.isSuccessful()) {
                        checkIfEmailVerified();
                    } else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage()
                                , Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void checkIfEmailVerified()
    {
        user = mAuth.getCurrentUser();

        if (user.isEmailVerified())
        {
            Toast.makeText(LoginActivity.this, "Login berhasil", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
        else
        {
            showDialog();
        }
    }

    private void showDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        alertDialogBuilder.setTitle("Email belum diverifikasi!");

        alertDialogBuilder
                .setMessage("Kirim ulang email verifikasi?")
                .setCancelable(false)
                .setPositiveButton("Ya",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        sendVerificationEmail();
                    }
                })
                .setNegativeButton("Tidak",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    private void sendVerificationEmail() {

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mAuth.signOut();
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Email verifikasi berhasil dikirim!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Email verifikasi tidak dapat dikirim!", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
}