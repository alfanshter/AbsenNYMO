package com.skripsi.absen.pimpinan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alfanshter.udinlelangfix.Session.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.skripsi.absen.R
import com.skripsi.absen.auth.LoginActivity
import kotlinx.android.synthetic.main.activity_pimpinan.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.startActivity

class PimpinanActivity : AppCompatActivity() {
    lateinit var sessionManager: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pimpinan)

        sessionManager = SessionManager(this)
        btn_gajikaryawan.setOnClickListener {
            startActivity<GajiKaryawanActivity>()
        }

        absenkaryawan.setOnClickListener {
            startActivity<AbsensiKaryawanActivity>()
        }

        liburkaryawan.setOnClickListener {
            startActivity<LiburKaryawanPimpinanActivity>()
        }

        lemburkaryawan.setOnClickListener {
            startActivity<LembuarKaryawanPimpinanActivity>()
        }

        izinkaryawan.setOnClickListener {
            startActivity<IzinKaryawanPimpinanActivity>()
        }

        btn_logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            sessionManager.setLoginpimpinan(false)
            startActivity(intentFor<LoginActivity>().clearTask().newTask())
        }

        btn_datakaryawan.setOnClickListener {
            startActivity<DataKaryawabActivity>()
        }


    }
}