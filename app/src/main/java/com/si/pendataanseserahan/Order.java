package com.si.pendataanseserahan;

public class Order {

    String tanggal, pelanggan, produk, harga, jumlah, key;

    public Order (){

    }

    public Order(String tanggal, String pelanggan, String produk, String harga, String jumlah){
        this.tanggal = tanggal;
        this.pelanggan = pelanggan;
        this.produk = produk;
        this.harga = harga;
        this.jumlah = jumlah;
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

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getHarga() {
        return harga;
    }

    public void setJumlah(String jumlah) {
        this.jumlah = jumlah;
    }

    public String getJumlah() {
        return jumlah;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
