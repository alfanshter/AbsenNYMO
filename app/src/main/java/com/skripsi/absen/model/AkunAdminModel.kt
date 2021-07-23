package com.skripsi.absen.model

import com.google.firebase.Timestamp

class AkunAdminModel{
    var username : String? = null
    var nama : String? = null
    var password : String? = null
    var uid_admin : String? = null
    constructor() {}
    constructor(username: String?, nama: String?, password: String?, uid_admin: String?) {
        this.username = username
        this.nama = nama
        this.password = password
        this.uid_admin = uid_admin
    }


}