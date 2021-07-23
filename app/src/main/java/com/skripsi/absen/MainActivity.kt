package com.skripsi.absen

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.skripsi.absen.auth.LoginActivity
import com.skripsi.absen.utils.Constant
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask

class MainActivity : AppCompatActivity() {
    lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapaktif()

    }

    fun loading() {
        handler = Handler()
        handler.postDelayed({
            startActivity(intentFor<LoginActivity>().clearTask().newTask())

            finish()
        }, 3000)
    }

    private fun checklokasi() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Aplikasi butuh akses GPS, aktifkan GPS sekarang ?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                val enableGpsIntent =
                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivityForResult(enableGpsIntent, Constant.PERMISSIONS_REQUEST_ENABLE_GPS)
            }
        val alert = builder.create()
        alert.show()
    }

    fun mapaktif(): Boolean {
        val manager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            checklokasi()
            return true
        } else {
            loading()
        }
        return true
    }

    override fun onRestart() {
        super.onRestart()
        mapaktif()
    }


}