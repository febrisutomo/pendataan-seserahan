package com.si.pendataanseserahan;

public class Employee {

    String nama, alamat, noHp, jk, gambar, key;

    public Employee(){

    }

    public Employee(String nama, String jk, String alamat, String noHp, String gambar ){
        this.nama = nama;
        this.alamat = alamat;
        this.noHp = noHp;
        this.jk = jk;
        this.gambar = gambar;
    }


    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getNoHp() {
        return noHp;
    }

    public void setNoHp(String noHp) {
        this.noHp = noHp;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getJk() {
        return jk;
    }

    public void setJk(String jk) {
        this.jk = jk;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
