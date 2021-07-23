package com.skripsi.absen.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.skripsi.absen.R
import kotlinx.android.synthetic.main.activity_detail_register.*

class DetailRegisterActivity : AppCompatActivity() {
    var nama : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_register)
        val username = intent.getStringExtra("username")
        val password = intent.getStringExtra("password")


        txt_username.text = username.toString()
        txt_password.text = password.toString()

        imageButton.setOnClickListener {
            finish()
        }

    }
}