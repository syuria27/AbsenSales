package com.syuria.android.absensales.model;

/**
 * Created by HP on 30/11/2016.
 */

public class Absen {
    String kode_absen;
    String tanggal;
    String jam;
    String lokasi;

    public String getKode_absen() {
        return kode_absen;
    }

    public void setKode_absen(String kode_absen) {
        this.kode_absen = kode_absen;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getJam() {
        return jam;
    }

    public void setJam(String jam) {
        this.jam = jam;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }
}
