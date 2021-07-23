package com.skripsi.absen.pimpinan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.skripsi.absen.R
import kotlinx.android.synthetic.main.activity_data_karyawab.*

class DataKaryawabActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_karyawab)

        btn_back.setOnClickListener {
            finish()
        }
    }
}