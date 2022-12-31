package com.si.pendataanseserahan;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AddTransactionActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    Order order;
    EditText etTanggal, etBayar, etPelanggan, etProduk, etHarga, etJumlah, etTotal, etKembali;
    Spinner spinPesanan, spinMetode;

    Button btnSimpan;

    DatabaseReference db = FirebaseDatabase.getInstance().getReference();

    ArrayList<Order> orderList;

    ArrayList<String> orderIdList;

    ArrayList<Product> productList;
    ArrayList<Customer> customerList;

    String tanggal, produk, namaProduk, pelanggan, namaPelanggan, metodeBayar;

    int harga, jumlah, bayar;

    ArrayAdapter<String> orderAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        order = new Order();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Buat Transaksi");
        actionBar.setDisplayHomeAsUpEnabled(true);

        spinPesanan = findViewById(R.id.spinPesanan);
        spinMetode = findViewById(R.id.spinMetode);
        etTanggal = findViewById(R.id.etTanggal);
        etPelanggan = findViewById(R.id.etPelanggan);
        etProduk = findViewById(R.id.etProduk);
        etHarga = findViewById(R.id.etHarga);
        etJumlah = findViewById(R.id.etJumlah);
        etTotal = findViewById(R.id.etTotal);
        etBayar = findViewById(R.id.etBayar);
        etKembali = findViewById(R.id.etKembali);
        btnSimpan = findViewById(R.id.btnSimpan);

        orderList = new ArrayList<>();
        orderIdList = new ArrayList<>();

        orderIdList.add("Pilih Pesanan");

        orderAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, orderIdList);
        spinPesanan.setAdapter(orderAdapter);


        getCustomers();
        getProducts();

        getOrders();

        Calendar calendar = Calendar.getInstance();

        String date = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
        etTanggal.setText(date);

        etTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getTanggal = etTanggal.getText().toString();

                DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(getTanggal);
                datePickerFragment.show(getSupportFragmentManager(), "date_picker");
            }
        });

        spinPesanan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                namaProduk = "";
                namaPelanggan = "";
                jumlah = 1;

                if (position != 0) {
                    order = orderList.get(position - 1);

                    for (Product product : productList) {
                        if (product.getKey().equals(order.getProduk())) {
                            namaProduk = product.getNama();
                        }
                    }

                    for (Customer customer : customerList) {
                        if (customer.getKey().equals(order.getPelanggan())) {
                            namaPelanggan = customer.getNama();
                        }
                    }

                    etPelanggan.setText(namaPelanggan);
                    etProduk.setText(namaProduk);
                    etHarga.setText(Integer.toString(order.getHarga()));
                    etJumlah.setText(Integer.toString(order.getJumlah()));
                    etTotal.setText(Integer.toString(order.getHarga() * order.getJumlah()));
                    etBayar.requestFocus();
                } else {
                    etPelanggan.setText("");
                    etProduk.setText("");
                    etHarga.setText("0");
                    etJumlah.setText("0");
                    etTotal.setText("0");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        etBayar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {


                if (s.length() > 0) {
                    int total = Integer.parseInt("0" + etTotal.getText().toString());
                    int bayar= Integer.parseInt(s.toString());

                    etKembali.setText(Integer.toString(bayar - total ));

                } else {
                    etKembali.setText("0");
                }
            }
        });


        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeData();
            }
        });


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


    private void storeData() {
        tanggal = etTanggal.getText().toString();

        harga = Integer.parseInt("0"+ etHarga.getText().toString());
        pelanggan = etPelanggan.getText().toString();
        produk = etProduk.getText().toString();
        jumlah = Integer.parseInt("0" + etJumlah.getText().toString());
        bayar = Integer.parseInt("0" + etBayar.getText().toString());
        metodeBayar = spinMetode.getSelectedItem().toString();

        if (tanggal.isEmpty()) {
            etTanggal.setError("Tanggal tidak boleh kosong!");
            etTanggal.requestFocus();
        } else if (bayar == 0) {
            etBayar.setError("Bayar tidak boleh kosong!");
            etBayar.requestFocus();
        } else {

            db.child("Transaction").push()
                    .setValue(new Transaction(order.getId(), tanggal, pelanggan, produk, harga, jumlah, bayar, metodeBayar))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(), "Data berhasil ditambahkan!", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Data gagal ditambahkan!", Toast.LENGTH_LONG).show();
                        }
                    });
        }

    }

    private void getOrders() {
        db.child("Order").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();
                orderAdapter.clear();

                orderIdList.add("Pilih Pesanan");

                for (DataSnapshot item : snapshot.getChildren()) {
                    Order order = item.getValue(Order.class);

                    order.setKey(item.getKey());
                    orderList.add(order);
                    orderIdList.add(order.getId());
                }

                orderAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String date = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
        etTanggal.setText(date);
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