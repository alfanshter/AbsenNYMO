package com.skripsi.absen.pimpinan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.skripsi.absen.R
import kotlinx.android.synthetic.main.activity_lembuar_karyawan_pimpinan.*

class LembuarKaryawanPimpinanActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lembuar_karyawan_pimpinan)

        imageButton.setOnClickListener {
            finish()
        }
    }
}