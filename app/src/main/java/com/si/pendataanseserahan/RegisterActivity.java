package com.si.pendataanseserahan;

import static android.util.Patterns.EMAIL_ADDRESS;

import android.app.ProgressDialog;
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
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    Button btnRegister;
    TextView btnLogin;
    EditText etNama, etEmail, etPassword, etPasswordConfirm;

    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        progress = new ProgressDialog(this);
        progress.setMessage("Harap tunggu sebentar...");
        progress.setCancelable(false);

        etNama = findViewById(R.id.nama);
        etEmail = findViewById(R.id.email);
        etPassword = findViewById(R.id.password);
        etPasswordConfirm = findViewById(R.id.passwordConfirm);

        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);

        mAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void register() {
        String nama = etNama.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String passwordConfirm = etPasswordConfirm.getText().toString();

        if (nama.isEmpty()) {
            etNama.setError("Nama tidak boleh kosong.");
            etNama.requestFocus();
        }else if (email.isEmpty()) {
            etEmail.setError("Email tidak boleh kosong.");
            etEmail.requestFocus();
        } else if (!EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email tidak valid.");
            etEmail.requestFocus();
        } else if (password.isEmpty()) {
            etPassword.setError("Password tidak boleh kosong.");
            etPassword.requestFocus();
        } else if (!password.equals(passwordConfirm)) {
            etPasswordConfirm.setError("Konfirmasi password tidak sesuai.");
            etPasswordConfirm.requestFocus();
        } else {
            progress.show();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(nama).build();

                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                progress.dismiss();
                                                if (task.isSuccessful()) {
                                                    sendVerificationEmail();
                                                }
                                            }
                                        });

                            } else {
                                progress.dismiss();
                                Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }


    }

    private void sendVerificationEmail() {
        FirebaseUser user = mAuth.getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mAuth.signOut();
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Silahkan periksa email verifikasi!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Email verifikasi tidak dapat dikirim!", Toast.LENGTH_SHORT).show();
                            finish();

                        }
                    }
                });
    }


}