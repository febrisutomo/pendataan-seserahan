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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Locale;

public class OrderViewAdapter extends RecyclerView.Adapter<OrderViewAdapter.OrderViewHolder> {

    private ArrayList<Order> myList;

    DatabaseReference db = FirebaseDatabase.getInstance().getReference();

    public OrderViewAdapter(ArrayList<Order> myList) {
        this.myList = myList;
    }

    public void filterList(ArrayList<Order> filteredList) {
        myList = new ArrayList<>();
        myList.addAll(filteredList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_order, parent, false);
        return new OrderViewAdapter.OrderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewAdapter.OrderViewHolder holder, int position) {
        String tanggal = myList.get(position).getTanggal();
        String pelanggan = myList.get(position).getPelanggan();
        String produk = myList.get(position).getProduk();
        String harga = myList.get(position).getHarga();
        String jumlah = myList.get(position).getJumlah();
        String key= myList.get(position).getKey();

        String hargaF = "Rp " + String.format(Locale.US,"%,d", Integer.parseInt(harga)).replace(",",".");

        String bayar = "Rp " + String.format(Locale.US,"%,d", Integer.parseInt(harga) * Integer.parseInt(jumlah)).replace(",",".");
        
        holder.tvTanggal.setText(": " + tanggal);
        holder.tvPelanggan.setText(": " + pelanggan);
        holder.tvProduk.setText(": " + produk);
        holder.tvHarga.setText(": " + hargaF);
        holder.tvJumlah.setText(": " + jumlah);
        holder.tvBayar.setText(": " + bayar);

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
                                Bundle bundle = new Bundle();
                                bundle.putString("tanggal", tanggal);
                                bundle.putString("pelanggan", pelanggan);
                                bundle.putString("produk", produk);
                                bundle.putString("harga", harga);
                                bundle.putString("jumlah", jumlah);
                                bundle.putString("key", key);

                                Intent intent = new Intent(view.getContext(), EditOrderActivity.class);
                                intent.putExtras(bundle);
                                view.getContext().startActivity(intent);
                                break;
                            case 1:
                                delete(myList.get(holder.getAdapterPosition()), holder.getAdapterPosition(), view.getContext());

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

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView tvTanggal, tvPelanggan, tvProduk, tvHarga, tvJumlah, tvBayar;

        CardView card;

        public OrderViewHolder(@NonNull View view) {
            super(view);

            tvTanggal = view.findViewById(R.id.tvTanggal);
            tvPelanggan = view.findViewById(R.id.tvPelanggan);
            tvProduk = view.findViewById(R.id.tvProduk);
            tvHarga = view.findViewById(R.id.tvHarga);
            tvJumlah = view.findViewById(R.id.tvJumlah);
            tvBayar = view.findViewById(R.id.tvBayar);
            card = view.findViewById(R.id.card);

        }
    }

    public void delete(Order product, int position, Context context) {
        if (db != null) {
            db.child("Order").child(product.getKey())
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
