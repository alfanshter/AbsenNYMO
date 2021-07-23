package com.skripsi.absen.model

import com.google.firebase.Timestamp

class IjinModel{
    var uid : String? = null
    var uid_pegawai : String? = null
    var tanggal_izin : Timestamp?  =null
    var tipe_ijin : String? = null
    var alasan : String? = null
    var status : String? = null
    var id_izin : String? = null

    constructor() {}
    constructor(
        uid: String?,
        uid_pegawai: String?,
        tanggal_izin: Timestamp?,
        tipe_ijin: String?,
        alasan: String?,
        status: String?,
        id_izin: String?
    ) {
        this.uid = uid
        this.uid_pegawai = uid_pegawai
        this.tanggal_izin = tanggal_izin
        this.tipe_ijin = tipe_ijin
        this.alasan = alasan
        this.status = status
        this.id_izin = id_izin
    }


}