package com.skripsi.absen.model

import com.google.firebase.Timestamp

class GajiAdminModel{
    var id_gaji : String? = null
    var id_pimpinan : String? = null
    var gaji_karyawan : Int?  =null
    var tanggal : Timestamp? = null

    constructor() {}
    constructor(
        id_gaji: String?,
        id_pimpinan: String?,
        gaji_karyawan: Int?,
        tanggal: Timestamp?
    ) {
        this.id_gaji = id_gaji
        this.id_pimpinan = id_pimpinan
        this.gaji_karyawan = gaji_karyawan
        this.tanggal = tanggal
    }


}