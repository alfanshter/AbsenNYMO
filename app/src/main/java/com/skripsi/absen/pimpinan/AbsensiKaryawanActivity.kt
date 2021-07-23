package com.skripsi.absen.pimpinan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.skripsi.absen.R
import com.skripsi.absen.pimpinan.uiabsensi.AbsensiKaryawanFragment
import com.skripsi.absen.pimpinan.uiabsensi.IzinPimpinanFragment
import com.skripsi.absen.pimpinan.uiabsensi.LemburPimpinanFragment
import com.skripsi.absen.pimpinan.uiabsensi.LiburPimpinanFragment
import kotlinx.android.synthetic.main.activity_absensi_karyawan.*

class AbsensiKaryawanActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_absensi_karyawan)

        btn_absen.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(
                R.id.fr_absen,
                AbsensiKaryawanFragment()
            ).commit()

        }

        imageButton.setOnClickListener {
            finish()
        }
    }
}