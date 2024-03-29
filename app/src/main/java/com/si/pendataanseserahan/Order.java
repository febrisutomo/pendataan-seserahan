package com.si.pendataanseserahan;

import android.os.Parcel;
import android.os.Parcelable;

public class Order implements Parcelable {

    private String id, tanggal, pelanggan, produk, key;
    private int harga, jumlah;

    public Order (){

    }

    public Order( String id, String tanggal, String pelanggan, String produk, int harga, int jumlah){
        this.id = id;
        this.tanggal = tanggal;
        this.pelanggan = pelanggan;
        this.produk = produk;
        this.harga = harga;
        this.jumlah = jumlah;
    }

    protected Order(Parcel in) {
        id = in.readString();
        tanggal = in.readString();
        pelanggan = in.readString();
        produk = in.readString();
        key = in.readString();
        harga = in.readInt();
        jumlah = in.readInt();
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setPelanggan(String pelanggan) {
        this.pelanggan = pelanggan;
    }

    public String getPelanggan() {
        return pelanggan;
    }

    public void setProduk(String produk) {
        this.produk = produk;
    }

    public String getProduk() {
        return produk;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }

    public int getHarga() {
        return harga;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(tanggal);
        dest.writeString(pelanggan);
        dest.writeString(produk);
        dest.writeString(key);
        dest.writeInt(harga);
        dest.writeInt(jumlah);
    }
}
