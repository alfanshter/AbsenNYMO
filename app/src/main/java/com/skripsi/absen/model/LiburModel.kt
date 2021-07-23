package com.skripsi.absen.model

import com.google.firebase.Timestamp

class LiburModel{
    var uid : String? = null
    var uid_pegawai : String? = null
    var id_libur : String? = null
    var selesai_libur : Timestamp?  =null
    var mulai_libur : Timestamp?  =null
    var jumlah_hari : Int? = null
    var jenis : String? = null
    var status : String? = null

    constructor() {}
    constructor(
        uid: String?,
        uid_pegawai: String?,
        id_libur: String?,
        selesai_libur: Timestamp?,
        mulai_libur: Timestamp?,
        jumlah_hari: Int?,
        jenis: String?,
        status: String?
    ) {
        this.uid = uid
        this.uid_pegawai = uid_pegawai
        this.id_libur = id_libur
        this.selesai_libur = selesai_libur
        this.mulai_libur = mulai_libur
        this.jumlah_hari = jumlah_hari
        this.jenis = jenis
        this.status = status
    }

}