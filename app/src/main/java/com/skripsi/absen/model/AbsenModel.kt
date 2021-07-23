package com.skripsi.absen.model

import com.google.firebase.Timestamp

class AbsenModel{
    var uid_pegawai : String? = null
    var uid : String? = null
    var tanggal_absen : Timestamp?  =null
    var id_absen : String? = null
    var status : String? = null
    var absen_pulang : Timestamp? = null

    constructor() {}
    constructor(
        uid_pegawai: String?,
        uid: String?,
        tanggal_absen: Timestamp?,
        id_absen: String?,
        status: String?,
        absen_pulang: Timestamp?
    ) {
        this.uid_pegawai = uid_pegawai
        this.uid = uid
        this.tanggal_absen = tanggal_absen
        this.id_absen = id_absen
        this.status = status
        this.absen_pulang = absen_pulang
    }



}