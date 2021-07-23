package com.skripsi.absen.pimpinan

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.skripsi.absen.R
import com.skripsi.absen.pimpinan.ui.GajiAdminFragment
import com.skripsi.absen.pimpinan.ui.GajiLemburFragment
import com.skripsi.absen.pimpinan.ui.GajiPokokFragment
import com.skripsi.absen.pimpinan.uiabsensi.LiburPimpinanFragment
import kotlinx.android.synthetic.main.activity_gaji_karyawan.*

class GajiKaryawanActivity : AppCompatActivity() {
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gaji_karyawan)

        fr_gaji.requireView().visibility = View.VISIBLE
        fr_admin.requireView().visibility = View.GONE
        fr_pokok.requireView().visibility = View.GONE

        btn_gajilembur.setTextColor(R.color.abuabu)
        btn_gajipokok.setTextColor(R.color.merah)
        btn_gajiadmin.setTextColor(R.color.merah)

        btn_gajilembur.setOnClickListener {
            btn_gajilembur.setTextColor(R.color.abuabu)
            btn_gajipokok.setTextColor(R.color.merah)
            btn_gajiadmin.setTextColor(R.color.merah)


            fr_gaji.requireView().visibility = View.VISIBLE
            fr_admin.requireView().visibility = View.GONE
            fr_pokok.requireView().visibility = View.GONE

        }

        btn_gajipokok.setOnClickListener {
            btn_gajilembur.setTextColor(R.color.merah)
            btn_gajipokok.setTextColor(R.color.abuabu)
            btn_gajiadmin.setTextColor(R.color.merah)

            fr_gaji.requireView().visibility = View.GONE
            fr_admin.requireView().visibility = View.GONE
            fr_pokok.requireView().visibility = View.VISIBLE

        }

        btn_gajiadmin.setOnClickListener {
            btn_gajilembur.setTextColor(R.color.merah)
            btn_gajipokok.setTextColor(R.color.merah)
            btn_gajiadmin.setTextColor(R.color.abuabu)
            fr_gaji.requireView().visibility = View.GONE
            fr_admin.requireView().visibility = View.VISIBLE
            fr_pokok.requireView().visibility = View.GONE


        }

        imageButton.setOnClickListener {
            finish()
        }

    }
}