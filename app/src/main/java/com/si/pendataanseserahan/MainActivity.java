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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth mAuth;

    TextView tvNama;
    String nama;

    private AdView mAdView;

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

        findViewById(R.id.cardProduct).setOnClickListener(this);
        findViewById(R.id.cardCustomer).setOnClickListener(this);
        findViewById(R.id.cardOrder).setOnClickListener(this);
        findViewById(R.id.cardEmployee).setOnClickListener(this);
        findViewById(R.id.cardTransaction).setOnClickListener(this);
        findViewById(R.id.cardLogout).setOnClickListener(this);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

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
            case R.id.cardTransaction:
                startActivity(new Intent(MainActivity.this, TransactionActivity.class));
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