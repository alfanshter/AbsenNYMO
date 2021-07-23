package com.skripsi.absen.model

import com.google.firebase.Timestamp

class LemburModel{
    var id_lembur : String? = null
    var status : String? = null
    var bonus : Int? = null
    var tanggal_kerja : Timestamp?  =null
    var waktu_lembur : Int?  =null
    constructor() {}
    constructor(
        id_lembur: String?,
        status: String?,
        bonus: Int?,
        tanggal_kerja: Timestamp?,
        waktu_lembur: Int?
    ) {
        this.id_lembur = id_lembur
        this.status = status
        this.bonus = bonus
        this.tanggal_kerja = tanggal_kerja
        this.waktu_lembur = waktu_lembur
    }

}