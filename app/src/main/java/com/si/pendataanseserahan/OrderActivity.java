package com.si.pendataanseserahan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderActivity extends AppCompatActivity {

    OrderViewAdapter adapter;
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    ArrayList<Order> orderList;
    ArrayList<Customer> customerList;
    ArrayList<Product> productList;
    
    RecyclerView recyclerView;
    EditText etSearch;
    FloatingActionButton btnTambah;

    String namaPelanggan, namaProduk;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Data Pesanan");
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
                startActivity(new Intent(OrderActivity.this, AddOrderActivity.class));
            }
        });


        recyclerView = findViewById(R.id.rvOrder);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        
        getCustomers();
        getProducts();

        showData();


    }

    private void getProducts() {
        db.child("Product").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList = new ArrayList<>();

                for (DataSnapshot item : snapshot.getChildren()) {
                    Product product = item.getValue(Product.class);

                    product.setKey(item.getKey());
                    productList.add(product);
                }
                

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getCustomers() {
        db.child("Customer").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                customerList = new ArrayList<>();

                for (DataSnapshot item : snapshot.getChildren()) {
                    Customer customer = item.getValue(Customer.class);

                    customer.setKey(item.getKey());
                    customerList.add(customer);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showData() {
        db.child("Order").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList = new ArrayList<>();
                for (DataSnapshot item : snapshot.getChildren()){
                    Order order= item.getValue(Order.class);

                    order.setKey(item.getKey());
                    orderList.add(order);
                }

               
                adapter = new OrderViewAdapter(orderList, customerList, productList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void filter(String text) {
        ArrayList<Order> filteredList = new ArrayList<>();



        for (Order order : orderList){
            namaPelanggan= "";
            namaProduk = "";
            for (Customer customer : customerList) {
                if (customer.getKey().equals(order.getPelanggan())) {
                    namaPelanggan = customer.getNama();
                }
            }

            for (Product product : productList) {
                if (product.getKey().equals(order.getProduk())) {
                    namaProduk = product.getNama();
                }
            }
            if (namaPelanggan.toLowerCase().contains(text.toLowerCase()) || namaProduk.toLowerCase().contains(text.toLowerCase()) || order.getTanggal().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(order);
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