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

import java.text.BreakIterator;
import java.util.ArrayList;

public class CustomerViewAdapter extends RecyclerView.Adapter<CustomerViewAdapter.CustomerViewHolder> {

    private ArrayList<Customer> myList;
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();

    public CustomerViewAdapter(ArrayList<Customer> myList) {
        this.myList = myList;
    }

    public void filterList(ArrayList<Customer> filteredList) {
        myList = new ArrayList<>();
        myList.addAll(filteredList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CustomerViewAdapter.CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_customer, parent, false);
        return new CustomerViewAdapter.CustomerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewAdapter.CustomerViewHolder holder, int position) {
        String nama = myList.get(position).getNama();
        String alamat = myList.get(position).getAlamat();
        String noHp = myList.get(position).getNoHp();
        String jk = myList.get(position).getJk();
        String gambar = myList.get(position).getGambar();
        String key = myList.get(position).getKey();


        holder.tvNama.setText(": " + nama);
        holder.tvAlamat.setText(": " + alamat);
        holder.tvJk.setText(": " + jk);
        holder.tvNoHp.setText(": " + noHp);

        if (isEmpty(gambar)) {
            holder.ivGambar.setImageResource(R.drawable.user);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(gambar.trim()).placeholder(R.drawable.user)
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
                                Bundle bundle = new Bundle();
                                bundle.putString("nama", nama);
                                bundle.putString("alamat", alamat);
                                bundle.putString("jk", jk);
                                bundle.putString("noHp", noHp);
                                bundle.putString("gambar", gambar);
                                bundle.putString("key", key);

                                Intent intent = new Intent(view.getContext(), EditCustomerActivity.class);
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

    public class CustomerViewHolder extends RecyclerView.ViewHolder {

        TextView tvNama, tvAlamat, tvJk, tvNoHp;
        ImageView ivGambar;
        CardView card;

        public CustomerViewHolder(@NonNull View view) {
            super(view);

            tvNama = view.findViewById(R.id.tvNama);
            tvJk = view.findViewById(R.id.tvJk);
            tvAlamat = view.findViewById(R.id.tvAlamat);
            tvNoHp = view.findViewById(R.id.tvNoHp);
            ivGambar = view.findViewById(R.id.ivGambar);
            card = view.findViewById(R.id.card);

        }
    }

    public void delete(Customer product, int position, Context context) {
        if (db != null) {
            db.child("Customer").child(product.getKey())
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
