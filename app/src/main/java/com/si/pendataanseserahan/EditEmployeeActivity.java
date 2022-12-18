package com.si.pendataanseserahan;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class EditEmployeeActivity extends AppCompatActivity {

    EditText etNama, etAlamat, etNoHp;
    Button btnSimpan, btnGambar;
    ImageView ivGambar;
    RadioGroup rgJk;
    RadioButton rbMale, rbFemale;

    String nama, alamat, noHp, jk;

    Uri uri;
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    StorageReference storage = FirebaseStorage.getInstance().getReference();

    private static final int REQUEST_CODE_CAMERA = 1;
    private static final int REQUEST_CODE_GALLERY = 2;

    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Edit Karyawan");
        actionBar.setDisplayHomeAsUpEnabled(true);


        progress = new ProgressDialog(this);
        progress.setMessage("Harap tunggu sebentar...");
        progress.setCancelable(false);

        etNama = findViewById(R.id.etNama);
        etAlamat = findViewById(R.id.etAlamat);
        etNoHp = findViewById(R.id.etNoHp);
        rgJk = findViewById(R.id.rgJk);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);

        btnSimpan = findViewById(R.id.btnSimpan);
        btnGambar = findViewById(R.id.btnGambar);

        ivGambar = findViewById(R.id.ivGambar);

        btnGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFoto();
            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
            }
        });


        getData();
    }

    private void updateData() {
        nama = etNama.getText().toString();
        alamat = etAlamat.getText().toString();
        noHp = etNoHp.getText().toString();

        int checkedId = rgJk.getCheckedRadioButtonId();
        RadioButton checkedJk = findViewById(checkedId);

        jk = checkedJk.getText().toString();

        if (nama.isEmpty()) {
            etNama.setError("Nama tidak boleh kosong!");
            etNama.requestFocus();
        } else if (alamat.isEmpty()) {
            etAlamat.setError("Alamat tidak boleh kosong!");
            etAlamat.requestFocus();
        } else if (noHp.isEmpty()) {
            etNoHp.setError("No. HP tidak boleh kosong!");
            etNoHp.requestFocus();
        } else {
            ivGambar.setDrawingCacheEnabled(true);
            ivGambar.buildDrawingCache();

            Bitmap bitmap = ((BitmapDrawable) ivGambar.getDrawable()).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] bytes = stream.toByteArray();

            String fileName = UUID.randomUUID() + ".jpg";

            String pathImage = "gambar/" + fileName;

            String getKey = getIntent().getExtras().getString("key");

            progress.show();

            UploadTask uploadTask = storage.child(pathImage).putBytes(bytes);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    db.child("Employee").child(getKey)
                                            .setValue(new Employee(nama, jk, alamat, noHp, uri.toString().trim()))
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    progress.dismiss();
                                                    Toast.makeText(EditEmployeeActivity.this, "Data berhasil diupdate!", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }

                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progress.dismiss();
                                                    Toast.makeText(EditEmployeeActivity.this, "Data gagal diupdate!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            });
                }
            });
        }


    }

    private void getData() {
        String getNama = getIntent().getExtras().getString("nama");
        String getAlamat = getIntent().getExtras().getString("alamat");
        String getNoHp = getIntent().getExtras().getString("noHp");
        String getJk = getIntent().getExtras().getString("jk");

        String getGambar = getIntent().getExtras().getString("gambar");


        if (getGambar.isEmpty()) {
            ivGambar.setImageResource(R.drawable.image);
        } else {
            Glide.with(EditEmployeeActivity.this)
                    .load(getGambar)
                    .into(ivGambar);
        }

        etNama.setText(getNama);
        etAlamat.setText(getAlamat);
        etNoHp.setText(getNoHp);

        if(getJk.equals("Laki-laki")){
            rbMale.setChecked(true);
        }
        else{
            rbFemale.setChecked(true);
        }

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