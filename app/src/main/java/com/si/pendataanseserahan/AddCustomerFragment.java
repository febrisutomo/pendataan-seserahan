package com.si.pendataanseserahan;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class AddCustomerFragment extends DialogFragment {
    EditText etNama, etAlamat, etNoHp;
    RadioGroup rgJk;
    RadioButton rbMale, rbFemale;
    String nama, alamat, noHp, jk;
    Button btnGambar;
    ImageView ivGambar;

    View view;
    Context ctx;

    DatabaseReference db = FirebaseDatabase.getInstance().getReference();

    Uri uri;
    StorageReference storage = FirebaseStorage.getInstance().getReference();

    private static final int REQUEST_CODE_CAMERA = 1;
    private static final int REQUEST_CODE_GALLERY = 2;

    ProgressDialog progress;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        ctx = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        view = inflater.inflate(R.layout.fragment_add_customer, null);

        etNama = view.findViewById(R.id.etNama);
        etAlamat = view.findViewById(R.id.etAlamat);
        etNoHp = view.findViewById(R.id.etNoHp);
        rgJk = view.findViewById(R.id.rgJk);
        rbMale = view.findViewById(R.id.rbMale);
        rbFemale = view.findViewById(R.id.rbFemale);

        ivGambar = view.findViewById(R.id.ivGambar);
        btnGambar = view.findViewById(R.id.btnGambar);


        progress = new ProgressDialog(ctx);
        progress.setMessage("Harap tunggu sebentar...");
        progress.setCancelable(false);

        btnGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFoto();
            }
        });

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        storeData();
                        AddCustomerFragment.this.getDialog().cancel();
                    }
                })
                .setNegativeButton("Kembali", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddCustomerFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    private void storeData() {
        nama = etNama.getText().toString();
        alamat = etAlamat.getText().toString();
        noHp = etNoHp.getText().toString();

        int checkedId = rgJk.getCheckedRadioButtonId();
        RadioButton checkedJk = view.findViewById(checkedId);

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
                                    db.child("Customer").push()
                                            .setValue(new Customer(nama, jk, alamat, noHp, uri.toString().trim()))
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(ctx, "Data berhasil ditambahkan!", Toast.LENGTH_LONG).show();
                                                    progress.dismiss();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(ctx, "Data gagal ditambahkan!", Toast.LENGTH_LONG).show();
                                                    progress.dismiss();
                                                }
                                            });

                                }
                            });
                }
            });


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

}