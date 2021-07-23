package com.skripsi.absen.pimpinan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.skripsi.absen.R
import kotlinx.android.synthetic.main.activity_izin_karyawan_pimpinan.*

class IzinKaryawanPimpinanActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_izin_karyawan_pimpinan)

            btn_back.setOnClickListener {
                finish()
            }

    }
}