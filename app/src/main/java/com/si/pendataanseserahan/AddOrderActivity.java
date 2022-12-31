package com.si.pendataanseserahan;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AddOrderActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    EditText etTanggal, etJumlah;
    Spinner spinPelanggan, spinProduk;

    TextView tvHarga, tvTotal;

    Button btnSimpan, btnCustomer;

    DatabaseReference db = FirebaseDatabase.getInstance().getReference();

    ArrayList<Customer> customerList;

    ArrayList<Product> productList;

    ArrayList<String> customerNameList, productNameList;

    String id, tanggal, produk, pelanggan;

    int harga, jumlah, total;

    ArrayAdapter<String> adapterProduk, adapterPelangan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Tambah Pesanan");
        actionBar.setDisplayHomeAsUpEnabled(true);

        etTanggal = findViewById(R.id.etTanggal);
        spinPelanggan = findViewById(R.id.spinPelanggan);
        spinProduk = findViewById(R.id.spinProduk);
        tvHarga = findViewById(R.id.tvHarga);
        etJumlah = findViewById(R.id.etJumlah);
        tvTotal = findViewById(R.id.tvTotal);
        btnSimpan = findViewById(R.id.btnSimpan);
        btnCustomer = findViewById(R.id.btnCustomer);

        customerList = new ArrayList<>();
        customerNameList = new ArrayList<>();

        productList = new ArrayList<>();
        productNameList = new ArrayList<>();

        productNameList.add("Pilih Produk");
        customerNameList.add("Pilih Pelanggan");

        adapterPelangan = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, customerNameList);
        spinPelanggan.setAdapter(adapterPelangan);

        adapterProduk = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, productNameList);
        spinProduk.setAdapter(adapterProduk);

        etJumlah.setText("1");

        getCustomers();

        getProducts();

        Calendar calendar = Calendar.getInstance();

        String date =  new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
        etTanggal.setText(date);

        etTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getTanggal = etTanggal.getText().toString();

                DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(getTanggal);
                datePickerFragment.show(getSupportFragmentManager(), "date_picker");
            }
        });

        spinProduk.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    harga = productList.get(position - 1).getHarga();
                    String hargaF = String.format(Locale.US, "%,d", harga).replace(",", ".");
                    tvHarga.setText(hargaF);
                } else {
                    harga = 0;
                    tvHarga.setText("0");
                }

                jumlah = Integer.parseInt(etJumlah.getText().toString());
                total = harga * jumlah;
                String totalF = String.format(Locale.US, "%,d", total).replace(",", ".");
                tvTotal.setText(totalF);
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
                    total = harga * jumlah;
                    String totalF = String.format(Locale.US, "%,d", total).replace(",", ".");
                    tvTotal.setText(totalF);
                } else {
                    tvTotal.setText("0");
                }
            }
        });


        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeData();
            }
        });


        btnCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCustomerFragment addCustomerFragment = new AddCustomerFragment();
                addCustomerFragment.show(getSupportFragmentManager(), "ADD CUSTOMER");
            }
        });


    }


    private void storeData() {
        tanggal = etTanggal.getText().toString();

        int idPelanggan = spinPelanggan.getSelectedItemPosition() - 1;

        int idProduk = spinProduk.getSelectedItemPosition() - 1;

        jumlah = Integer.parseInt("0" + etJumlah.getText().toString());

        String d[] = tanggal.split("/");

        id = d[2] + d[1] + d[0] +  (int)(Math.random()*(9999-1000+1)+1000);

        if (tanggal.isEmpty()) {
            etTanggal.setError("Tanggal tidak boleh kosong!");
            etTanggal.requestFocus();
        } else if (idPelanggan == -1) {
            ((TextView) spinPelanggan.getSelectedView()).setError("Harap pilih pelanggan!");
        }else if (idProduk == -1) {
            ((TextView) spinProduk.getSelectedView()).setError("Harap pilih produk!");
        }else if (jumlah == 0) {
            etJumlah.setError("Jumlah tidak boleh kosong!");
            etJumlah.requestFocus();
        } else {

            pelanggan = customerList.get(idPelanggan).getKey();
            produk = productList.get(idProduk).getKey();

            db.child("Order").push()
                    .setValue(new Order(id, tanggal, pelanggan, produk, harga, jumlah))
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

    private void getProducts() {
        db.child("Product").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                adapterProduk.clear();

                productNameList.add("Pilih Produk");

                for (DataSnapshot item : snapshot.getChildren()) {
                    Product product = item.getValue(Product.class);

                    product.setKey(item.getKey());
                    productList.add(product);

                    productNameList.add(product.getNama());
                }


                adapterProduk.notifyDataSetChanged();


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

                customerNameList.add("Pilih Pelanggan");

                for (DataSnapshot item : snapshot.getChildren()) {
                    Customer customer = item.getValue(Customer.class);

                    customer.setKey(item.getKey());
                    customerList.add(customer);
                    customerNameList.add(customer.getNama());
                }

                adapterPelangan.notifyDataSetChanged();


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
        String date =  new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
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