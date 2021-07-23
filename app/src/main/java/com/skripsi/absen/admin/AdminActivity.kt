package com.skripsi.absen.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alfanshter.udinlelangfix.Session.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.skripsi.absen.R
import com.skripsi.absen.auth.LoginActivity
import kotlinx.android.synthetic.main.activity_admin.*
import org.jetbrains.anko.startActivity

class AdminActivity : AppCompatActivity() {
    lateinit var sessionManager: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        sessionManager = SessionManager(this)

        btn_registrasi.setOnClickListener {
            startActivity<RegisterActivity>()
        }

        btn_lembur.setOnClickListener {
            startActivity<LemburActivity>()

        }


        btn_libur.setOnClickListener {
            startActivity<LiburKaryawanActivity>()

        }

        btn_logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            sessionManager.setLoginadmin(false)
            startActivity<LoginActivity>()
            finish()
        }

        btn_ijin.setOnClickListener {
            startActivity<IzinActivity>()

        }

        btn_gajiadmin.setOnClickListener {
            startActivity<GajiAdminActivity>()

        }
    }
}