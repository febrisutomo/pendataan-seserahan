package com.si.pendataanseserahan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Locale;

public class EditOrderActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    EditText etTanggal, etJumlah;
    Spinner spinPelanggan, spinProduk;

    TextView tvHarga, tvBayar;

    Button btnSimpan, btnCustomer;

    DatabaseReference db = FirebaseDatabase.getInstance().getReference();

    ArrayList<Customer> customerList;

    ArrayList<Product> productList;

    ArrayList<String> customerNameList, productNameList;

    String tanggal, jumlah, produk, pelanggan;

    int harga, bayar;

    ArrayAdapter<String> adapterPelangan, adapterProduk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Edit Pesanan");
        actionBar.setDisplayHomeAsUpEnabled(true);

        etTanggal = findViewById(R.id.etTanggal);
        spinPelanggan = findViewById(R.id.spinPelanggan);
        spinProduk = findViewById(R.id.spinProduk);
        tvHarga = findViewById(R.id.tvHarga);
        etJumlah = findViewById(R.id.etJumlah);
        tvBayar = findViewById(R.id.tvBayar);
        btnSimpan = findViewById(R.id.btnSimpan);
        btnCustomer = findViewById(R.id.btnCustomer);

        customerList = new ArrayList<>();
        customerNameList = new ArrayList<>();

        productList = new ArrayList<>();
        productNameList = new ArrayList<>();

        customerNameList.add("Pilih Pelanggan");
        productNameList.add("Pilih Produk");

        adapterPelangan = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, customerNameList);
        spinPelanggan.setAdapter(adapterPelangan);

        adapterProduk = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, productNameList);
        spinProduk.setAdapter(adapterProduk);

        getCustomers();

        getProducts();

        getData();

        etTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etTanggal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String getTanggal = etTanggal.getText().toString();
                        DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(getTanggal);
                        datePickerFragment.show(getSupportFragmentManager(), "date_picker");
                    }
                });
            }
        });

        spinProduk.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    harga = Integer.parseInt(productList.get(position - 1).getHarga());
                    String hargaF = String.format(Locale.US, "%,d", harga).replace(",", ".");
                    tvHarga.setText(hargaF);
                } else {
                    harga = 0;
                    tvHarga.setText("0");
                }

                int jumlah = Integer.parseInt(etJumlah.getText().toString());
                bayar = harga * jumlah;
                String bayarF = String.format(Locale.US, "%,d", bayar).replace(",", ".");
                tvBayar.setText(bayarF);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        etJumlah.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {


                if (s.length() > 0) {
                    int jumlah = Integer.parseInt(s.toString());
                    bayar = harga * jumlah;
                    String bayarF = String.format(Locale.US, "%,d", bayar).replace(",", ".");
                    tvBayar.setText(bayarF);
                } else {
                    tvBayar.setText("0");
                }

            }
        });

        btnCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCustomerFragment addCustomerFragment = new AddCustomerFragment();
                addCustomerFragment.show(getSupportFragmentManager(), "ADD CUSTOMER");
            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
            }
        });


    }

    private void getData() {
        String getTanggal = getIntent().getExtras().getString("tanggal");

        String getHarga = getIntent().getExtras().getString("harga");
        String getJumlah = getIntent().getExtras().getString("jumlah");

        harga = Integer.parseInt(getHarga);

        etTanggal.setText(getTanggal);
        etJumlah.setText(getJumlah);

        String hargaF = String.format(Locale.US, "%,d", Integer.parseInt(getHarga)).replace(",", ".");
        tvHarga.setText(hargaF);

        int getBayar = Integer.parseInt(getHarga) * Integer.parseInt(getJumlah);
        String bayarF = String.format(Locale.US, "%,d", getBayar).replace(",", ".");
        tvBayar.setText(bayarF);

    }

    private void updateData() {
        tanggal = etTanggal.getText().toString();
        pelanggan = spinPelanggan.getSelectedItem().toString();
        produk = spinProduk.getSelectedItem().toString();
        jumlah = etJumlah.getText().toString();

        if (tanggal.isEmpty()) {
            etTanggal.setError("Tanggal tidak boleh kosong!");
            etTanggal.requestFocus();
        } else if (jumlah.equals("0") || jumlah.equals("")) {
            etJumlah.setError("Jumlah tidak boleh kosong!");
            etJumlah.requestFocus();
        } else {

            String getKey = getIntent().getExtras().getString("key");

            db.child("Order").child(getKey)
                    .setValue(new Order(tanggal, pelanggan, produk, Integer.toString(harga), jumlah))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(), "Data berhasil diupdate!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditOrderActivity.this, "Data gagal diupdate!", Toast.LENGTH_SHORT).show();
                        }
                    });
            ;


        }

    }

    private void getProducts() {
        db.child("Product").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                adapterProduk.clear();

                for (DataSnapshot item : snapshot.getChildren()) {
                    Product product = item.getValue(Product.class);

                    product.setKey(item.getKey());
                    productList.add(product);
                }

                productNameList.add("Pilih Produk");

                for (Product product : productList) {
                    productNameList.add(product.getNama());
                }
                String getProduk = getIntent().getExtras().getString("produk");
                ArrayAdapter<String> produkAdp = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, productNameList);
                spinProduk.setAdapter(produkAdp);
                spinProduk.setSelection(produkAdp.getPosition(getProduk.trim()));


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
                customerList.clear();
                adapterPelangan.clear();
                for (DataSnapshot item : snapshot.getChildren()) {
                    Customer customer = item.getValue(Customer.class);

                    customer.setKey(item.getKey());
                    customerList.add(customer);
                }

                customerNameList.add("Pilih Pelanggan");
                for (Customer customer : customerList) {
                    customerNameList.add(customer.getNama());
                }

                String getPelanggan = getIntent().getExtras().getString("pelanggan");
                ArrayAdapter<String> pelangganAdp = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, customerNameList);
                spinPelanggan.setAdapter(pelangganAdp);
                spinPelanggan.setSelection(pelangganAdp.getPosition(getPelanggan.trim()));


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