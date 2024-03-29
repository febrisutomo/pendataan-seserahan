package com.si.pendataanseserahan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProductActivity extends AppCompatActivity {

    ProductViewAdapter adapter;
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    ArrayList<Product> productList;
    RecyclerView recyclerView;
    EditText etSearch;
    FloatingActionButton btnTambah;

    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Data Produk");
        actionBar.setDisplayHomeAsUpEnabled(true);

        progress = new ProgressDialog(this);
        progress.setMessage("Harap tunggu sebentar...");
        progress.setCancelable(false);

        etSearch = findViewById(R.id.etSearch);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });

        btnTambah = findViewById(R.id.btnTambah);

        btnTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProductActivity.this, AddProductActivity.class));
            }
        });


        recyclerView = findViewById(R.id.rvProduk);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        tampilData();


    }

    private void tampilData() {
        progress.show();
        db.child("Product").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList = new ArrayList<>();
                for (DataSnapshot item : snapshot.getChildren()){
                    Product product= item.getValue(Product.class);

                    product.setKey(item.getKey());
                    productList.add(product);
                }

                adapter = new ProductViewAdapter(productList);
                recyclerView.setAdapter(adapter);
                progress.dismiss();

                if (productList.size() == 0){
                    Toast.makeText(ProductActivity.this, "Tidak ada data!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progress.dismiss();
                Toast.makeText(ProductActivity.this, "Periksa koneksi internet!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filter(String text) {
        ArrayList<Product> filteredList = new ArrayList<>();

        for (Product item : productList){
            if (item.getNama().toLowerCase().contains(text.toLowerCase()) || item.getJenis().toLowerCase().contains(text.toLowerCase()) || item.getIsi().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
        }

        adapter.filterList(filteredList);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}