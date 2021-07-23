package com.skripsi.absen.model

import com.google.firebase.Timestamp

class PegawaiModel{
    var uid : String? = null
    var alamat : String? = null
    var nama : String? = null
    var no_hp : String? = null
    var password : String? = null
    var tanggal_daftar : Timestamp? = null
    var tipe_akun : String? = null
    var token_id : String? = null
    var username : String? = null
    constructor() {}
    constructor(
        uid: String?,
        alamat: String?,
        nama: String?,
        no_hp: String?,
        password: String?,
        tanggal_daftar: Timestamp?,
        tipe_akun: String?,
        token_id: String?,
        username: String?
    ) {
        this.uid = uid
        this.alamat = alamat
        this.nama = nama
        this.no_hp = no_hp
        this.password = password
        this.tanggal_daftar = tanggal_daftar
        this.tipe_akun = tipe_akun
        this.token_id = token_id
        this.username = username
    }


}