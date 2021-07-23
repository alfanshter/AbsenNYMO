package com.skripsi.absen.auth

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alfanshter.udinlelangfix.Session.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.skripsi.absen.R
import com.skripsi.absen.admin.AdminActivity
import com.skripsi.absen.karyawan.KaryawanActivity
import com.skripsi.absen.pimpinan.PimpinanActivity
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.*

class LoginActivity : AppCompatActivity(),AnkoLogger {
    lateinit var sessionManager: SessionManager
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sessionManager = SessionManager(this)
        auth = FirebaseAuth.getInstance()


        btn_pegawai.setOnClickListener {
            startActivity<LoginAdminActivity>()

        }

        btn_loginpegawai.setOnClickListener {
            startActivity<LoginPegawaiActivity>()
        }

        btn_pimpinan.setOnClickListener {
            startActivity<LoginPimpinanActivity>()

        }
    }

    override fun onStart() {
        super.onStart()
        sessionManager = SessionManager(this)
        if (sessionManager.getLogin()!!) {
            startActivity(intentFor<KaryawanActivity>().clearTask().newTask())
            finish()
        } else if (sessionManager.getLoginadmin()==true) {
            startActivity(intentFor<AdminActivity>().clearTask().newTask())
            finish()
        }
        else if (sessionManager.getLoginpimpinan()==true) {
            startActivity(intentFor<PimpinanActivity>().clearTask().newTask())
            finish()
        }
    }
}