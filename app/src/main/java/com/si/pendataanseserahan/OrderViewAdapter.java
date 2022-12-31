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

import java.io.Serializable;
import java.text.BreakIterator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class OrderViewAdapter extends RecyclerView.Adapter<OrderViewAdapter.OrderViewHolder> {

    private ArrayList<Order> orderList;
    private ArrayList<Product> productList;
    private ArrayList<Customer> customerList;

    DatabaseReference db = FirebaseDatabase.getInstance().getReference();

    String namaPelanggan, namaProduk, harga, total;


    public OrderViewAdapter(ArrayList<Order> orderList, ArrayList<Customer> customerList, ArrayList<Product> productList) {
        this.orderList = orderList;
        this.productList = productList;
        this.customerList = customerList;
    }

    public void filterList(ArrayList<Order> filteredList) {
        orderList = new ArrayList<>();
        orderList.addAll(filteredList);
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
        Order order = orderList.get(position);
        namaPelanggan = " ";
        namaProduk = " ";

        for (Customer customer : customerList){
            if (customer.getKey().equals(order.getPelanggan())){
                namaPelanggan = customer.getNama();
            }
        }

        for (Product product : productList){
            if (product.getKey().equals(order.getProduk())){
                namaProduk = product.getNama();
            }
        }

        harga = "Rp " + String.format(Locale.US, "%,d", order.getHarga()).replace(",", ".");

        total = "Rp " + String.format(Locale.US, "%,d", order.getHarga() * order.getJumlah()).replace(",", ".");

        holder.tvId.setText(": " + order.getId());
        holder.tvTanggal.setText(": " + order.getTanggal());
        holder.tvPelanggan.setText(": " + namaPelanggan);
        holder.tvProduk.setText(": " + namaProduk);
        holder.tvHarga.setText(": " + harga);
        holder.tvJumlah.setText(": " + order.getJumlah());
        holder.tvTotal.setText(": " + total);

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
                                Intent intent1 = new Intent(view.getContext(), EditOrderActivity.class);
                                intent1.putExtra("order", order);;
                                view.getContext().startActivity(intent1);
                                break;
                            case 1:
                                delete(order, holder.getAdapterPosition(), view.getContext());

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
        return orderList.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView tvTanggal, tvPelanggan, tvProduk, tvHarga, tvJumlah, tvTotal, tvId;

        CardView card;

        public OrderViewHolder(@NonNull View view) {
            super(view);

            tvId = view.findViewById(R.id.tvId);
            tvTanggal = view.findViewById(R.id.tvTanggal);
            tvPelanggan = view.findViewById(R.id.tvPelanggan);
            tvProduk = view.findViewById(R.id.tvProduk);
            tvHarga = view.findViewById(R.id.tvHarga);
            tvJumlah = view.findViewById(R.id.tvJumlah);
            tvTotal = view.findViewById(R.id.tvTotal);
            card = view.findViewById(R.id.card);

        }
    }

    public void delete(Order order, int position, Context context) {
        if (db != null) {
            db.child("Order").child(order.getKey())
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
