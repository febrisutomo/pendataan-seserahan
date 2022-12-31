package com.si.pendataanseserahan;

import android.os.Parcel;
import android.os.Parcelable;

public class Transaction implements Parcelable {

    private String id, tanggal, pelanggan, produk, metodeBayar, key;
    private int harga, jumlah, bayar;

    public Transaction (){

    }

    public Transaction( String id, String tanggal, String pelanggan, String produk, int harga, int jumlah, int bayar, String metodeBayar){
        this.id = id;
        this.tanggal = tanggal;
        this.pelanggan = pelanggan;
        this.produk = produk;
        this.harga = harga;
        this.jumlah = jumlah;
        this.bayar = bayar;
        this.metodeBayar = metodeBayar;
    }

    protected Transaction(Parcel in) {
        id = in.readString();
        tanggal = in.readString();
        pelanggan = in.readString();
        produk = in.readString();
        key = in.readString();
        harga = in.readInt();
        jumlah = in.readInt();
        bayar = in.readInt();
        metodeBayar = in.readString();
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
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

    public int getBayar() {
        return bayar;
    }

    public void setBayar(int bayar) {
        this.bayar = bayar;
    }

    public String getMetodeBayar() {
        return metodeBayar;
    }

    public void setMetodeBayar(String metodeBayar) {
        this.metodeBayar = metodeBayar;
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
        dest.writeInt(bayar);
        dest.writeString(metodeBayar);
    }
}
