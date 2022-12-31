package com.si.pendataanseserahan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TransactionActivity extends AppCompatActivity {

    TransactionViewAdapter adapter;
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    ArrayList<Transaction> transactionList;
    RecyclerView recyclerView;
    EditText etSearch;
    FloatingActionButton btnTambah;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Data Transaksi");
        actionBar.setDisplayHomeAsUpEnabled(true);

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
                startActivity(new Intent(TransactionActivity.this, AddTransactionActivity.class));
            }
        });


        recyclerView = findViewById(R.id.rvTransaction);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        tampilData();


    }

    private void tampilData() {
        db.child("Transaction").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                transactionList = new ArrayList<>();
                for (DataSnapshot item : snapshot.getChildren()){
                    Transaction transaction = item.getValue(Transaction.class);

                    transaction.setKey(item.getKey());
                    transactionList.add(transaction);
                }

                if (transactionList.size() == 0 ){
                    Toast.makeText(TransactionActivity.this, "Tidak ada data!", Toast.LENGTH_SHORT).show();
                }
                adapter = new TransactionViewAdapter(transactionList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TransactionActivity.this, "Periksa koneksi internet!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filter(String text) {
        ArrayList<Transaction> filteredList = new ArrayList<>();

        for (Transaction item : transactionList){
            if (item.getPelanggan().toLowerCase().contains(text.toLowerCase()) || item.getMetodeBayar().toLowerCase().contains(text.toLowerCase())  || item.getProduk().toLowerCase().contains(text.toLowerCase())){
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