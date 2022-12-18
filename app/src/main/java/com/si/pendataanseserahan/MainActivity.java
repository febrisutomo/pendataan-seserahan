package com.si.pendataanseserahan;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth mAuth;

    CardView cardProduct, cardCustomer, cardOrder, cardLogout, cardEmployee;
    TextView tvNama;
    String nama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mAuth = FirebaseAuth.getInstance();

        nama = "User";

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            nama = user.getDisplayName();
        }

        tvNama = findViewById(R.id.tvNama);

        tvNama.setText(nama);

        cardProduct = findViewById(R.id.cardProduct);
        cardCustomer = findViewById(R.id.cardCustomer);
        cardOrder = findViewById(R.id.cardOrder);
        cardEmployee = findViewById(R.id.cardEmployee);
        cardLogout = findViewById(R.id.cardLogout);

        cardProduct.setOnClickListener(this);
        cardCustomer.setOnClickListener(this);
        cardCustomer.setOnClickListener(this);
        cardEmployee.setOnClickListener(this);
        cardOrder.setOnClickListener(this);
        cardLogout.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cardProduct:
                startActivity(new Intent(MainActivity.this, ProductActivity.class));
                break;
            case R.id.cardCustomer:
                startActivity(new Intent(MainActivity.this, CustomerActivity.class));
                break;
            case R.id.cardOrder:
                startActivity(new Intent(MainActivity.this, OrderActivity.class));
                break;
            case R.id.cardEmployee:
                startActivity(new Intent(MainActivity.this, EmployeeActivity.class));
                break;
            case R.id.cardLogout:
                mAuth.signOut();
                Toast.makeText(MainActivity.this, "Anda berhasil logout!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                break;
            default:
                break;
        }
    }
}