package com.si.pendataanseserahan;

import static android.text.TextUtils.isEmpty;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Locale;

public class ProductViewAdapter extends RecyclerView.Adapter<ProductViewAdapter.ProductViewHolder> {

    private ArrayList<Product> myList;
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();

    public ProductViewAdapter(ArrayList<Product> myList) {
        this.myList = myList;
    }

    public void filterList(ArrayList<Product> filteredList) {
        myList = new ArrayList<>();
        myList.addAll(filteredList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_product, parent, false);
        return new ProductViewAdapter.ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewAdapter.ProductViewHolder holder, int position) {
        Product product = myList.get(position);
        String nama = product.getNama();
        String jenis = product.getJenis();
        int harga = product.getHarga();
        String isi = product.getIsi();
        String gambar = product.getGambar();
        String key = product.getKey();


        String hargaF = "Rp " + String.format(Locale.US,"%,d", harga).replace(",",".");

        holder.tvNama.setText(": " + nama);
        holder.tvJenis.setText(": " + jenis);
        holder.tvHarga.setText(": "+ hargaF);
        holder.tvIsi.setText(": " + isi);


        if (isEmpty(gambar)) {
            holder.ivGambar.setImageResource(R.drawable.image);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(gambar.trim()).placeholder(R.drawable.image)
                    .into(holder.ivGambar);
        }

        holder.card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final String[] action = {"Edit", "Hapus"};
                AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                alert.setItems(action, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        switch (i) {
                            case 0:

                                Intent intent = new Intent(view.getContext(), EditProductActivity.class);
                                intent.putExtra("product", product);
                                view.getContext().startActivity(intent);
                                break;
                            case 1:
                                delete(product, holder.getAdapterPosition(), view.getContext());

                                break;
                        }

                    }
                });
                alert.create();
                alert.show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView tvNama, tvHarga, tvJenis, tvIsi;
        ImageView ivGambar;
        CardView card;

        public ProductViewHolder(@NonNull View view) {
            super(view);

            tvNama = view.findViewById(R.id.tvNama);
            tvHarga = view.findViewById(R.id.tvHarga);
            tvJenis = view.findViewById(R.id.tvJenis);
            tvIsi = view.findViewById(R.id.tvIsi);
            ivGambar = view.findViewById(R.id.ivGambar);
            card = view.findViewById(R.id.card);

        }
    }

    public void delete(Product product, int position, Context context) {
        if (db != null) {
            db.child("Product").child(product.getKey())
                    .removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(context, "Data berhasil dihapus!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
