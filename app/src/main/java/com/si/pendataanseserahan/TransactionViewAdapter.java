package com.si.pendataanseserahan;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Locale;

public class TransactionViewAdapter extends RecyclerView.Adapter<TransactionViewAdapter.TransactionViewHolder> {

    private ArrayList<Transaction> myList;

    DatabaseReference db = FirebaseDatabase.getInstance().getReference();

    public TransactionViewAdapter(ArrayList<Transaction> myList) {
        this.myList = myList;
    }

    public void filterList(ArrayList<Transaction> filteredList) {
        myList = new ArrayList<>();
        myList.addAll(filteredList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TransactionViewAdapter.TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewAdapter.TransactionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewAdapter.TransactionViewHolder holder, int position) {
        Transaction transaction = myList.get(position);

        String hargaF = "Rp " + String.format(Locale.US, "%,d", transaction.getHarga()).replace(",", ".");

        String total = "Rp " + String.format(Locale.US, "%,d", transaction.getHarga() * transaction.getJumlah()).replace(",", ".");

        String bayar = "Rp " + String.format(Locale.US, "%,d", transaction.getBayar()).replace(",", ".");

        String kembali = "Rp " + String.format(Locale.US, "%,d", transaction.getBayar() - transaction.getHarga() * transaction.getJumlah()).replace(",", ".");

        holder.tvTanggal.setText(": " + transaction.getTanggal());
        holder.tvPelanggan.setText(": " + transaction.getPelanggan());
        holder.tvProduk.setText(": " + transaction.getProduk());
        holder.tvHarga.setText(": " + hargaF);
        holder.tvJumlah.setText(": " + Integer.toString(transaction.getJumlah()));
        holder.tvTotal.setText(": " + total);
        holder.tvBayar.setText(": " + bayar);
        holder.tvKembali.setText(": " + kembali);
        holder.tvMetode.setText(": " + transaction.getMetodeBayar());

        holder.card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final String[] action = {"Hapus"};
                AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                alert.setItems(action, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
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

    public class TransactionViewHolder extends RecyclerView.ViewHolder {

        TextView tvTanggal, tvPelanggan, tvProduk, tvHarga, tvJumlah, tvTotal, tvBayar, tvKembali, tvMetode;

        CardView card;

        public TransactionViewHolder(@NonNull View view) {
            super(view);

            tvTanggal = view.findViewById(R.id.tvTanggal);
            tvPelanggan = view.findViewById(R.id.tvPelanggan);
            tvProduk = view.findViewById(R.id.tvProduk);
            tvHarga = view.findViewById(R.id.tvHarga);
            tvJumlah = view.findViewById(R.id.tvJumlah);
            tvTotal = view.findViewById(R.id.tvTotal);
            tvBayar = view.findViewById(R.id.tvBayar);
            tvKembali = view.findViewById(R.id.tvKembali);
            tvMetode = view.findViewById(R.id.tvMetode);
            card = view.findViewById(R.id.card);

        }
    }

    public void delete(Transaction product, int position, Context context) {
        if (db != null) {
            db.child("Transaction").child(product.getKey())
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
