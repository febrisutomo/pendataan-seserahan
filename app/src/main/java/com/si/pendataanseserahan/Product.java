package com.si.pendataanseserahan;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {
    private String nama, jenis, isi,gambar, key ;

    private int harga;

    public Product(){

    }

    public Product(String nama, String jenis, int harga, String isi, String gambar){
        this.nama = nama;
        this.jenis = jenis;
        this.harga = harga;
        this.isi = isi;
        this.gambar = gambar;
    }


    protected Product(Parcel in) {
        nama = in.readString();
        jenis = in.readString();
        harga = in.readInt();
        isi = in.readString();
        gambar = in.readString();
        key = in.readString();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nama);
        dest.writeString(jenis);
        dest.writeInt(harga);
        dest.writeString(isi);
        dest.writeString(gambar);
        dest.writeString(key);
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public int getHarga() {
        return harga;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }

    public String getIsi() {
        return isi;
    }

    public void setIsi(String isi) {
        this.isi = isi;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }



}
