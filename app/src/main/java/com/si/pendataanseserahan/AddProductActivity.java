package com.si.pendataanseserahan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class AddProductActivity extends AppCompatActivity {
    EditText etNama, etIsi, etHarga;
    Spinner spinJenis;
    ImageView ivGambar;
    Button btnGambar, btnSimpan;

    String nama, isi, jenis;
    int harga;

    Uri uri;
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    StorageReference storage = FirebaseStorage.getInstance().getReference();

    private static final int REQUEST_CODE_CAMERA = 1;
    private static final int REQUEST_CODE_GALLERY = 2;

    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);



        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Tambah Produk");
        actionBar.setDisplayHomeAsUpEnabled(true);

        progress = new ProgressDialog(this);
        progress.setMessage("Harap tunggu sebentar...");
        progress.setCancelable(false);

        etNama = findViewById(R.id.etNama);
        etIsi = findViewById(R.id.etIsi);
        etHarga = findViewById(R.id.etHarga);
        spinJenis = findViewById(R.id.spinJenis);
        ivGambar = findViewById(R.id.ivGambar);
        btnGambar = findViewById(R.id.btnGambar);
        btnSimpan = findViewById(R.id.btnSimpan);

        btnGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFoto();
            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nama = etNama.getText().toString();
                harga = Integer.parseInt("0" + etHarga.getText().toString());
                isi = etIsi.getText().toString();
                jenis = spinJenis.getSelectedItem().toString();

                if (nama.isEmpty()) {
                    etNama.setError("Nama tidak boleh kosong!");
                    etNama.requestFocus();
                } else if (isi.isEmpty()) {
                    etIsi.setError("Isi tidak boleh kosong!");
                    etIsi.requestFocus();
                } else if (harga == 0) {
                    etHarga.setError("Harga tidak boleh kosong!");
                    etHarga.requestFocus();
                } else {
                    storeData();
                }
            }
        });


    }

    private void storeData() {
        ivGambar.setDrawingCacheEnabled(true);
        ivGambar.buildDrawingCache();
        Bitmap bt = ((BitmapDrawable) ivGambar.getDrawable()).getBitmap();

        ByteArrayOutputStream st = new ByteArrayOutputStream();

        bt.compress(Bitmap.CompressFormat.JPEG, 100, st);
        byte[] bytes = st.toByteArray();
        String namaGambar = UUID.randomUUID() + ".jpg";

        final String path = "gambar/" + namaGambar;


        progress.show();
        UploadTask up = storage.child(path).putBytes(bytes);
        up.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                db.child("Product").push()
                                        .setValue(new Product(nama, jenis, harga, isi, uri.toString().trim()))
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progress.dismiss();
                                                Toast.makeText(AddProductActivity.this, "Data berhasil disimpan", Toast.LENGTH_LONG).show();
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progress.dismiss();
                                                Toast.makeText(AddProductActivity.this, "Data gagal disimpan", Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        });
            }
        });
    }

    private void getFoto() {
        Intent ImgIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(ImgIntent, 2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_CAMERA:
                if (resultCode == RESULT_OK) {
                    ivGambar.setVisibility(View.VISIBLE);
                    Bitmap bt = (Bitmap) data.getExtras().get("data");
                    ivGambar.setImageBitmap(bt);
                }
                break;
            case REQUEST_CODE_GALLERY:
                if (resultCode == RESULT_OK) {
                    ivGambar.setVisibility(View.VISIBLE);
                    uri = data.getData();
                    ivGambar.setImageURI(uri);
                }
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}