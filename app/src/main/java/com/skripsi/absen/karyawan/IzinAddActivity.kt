package com.skripsi.absen.karyawan

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.skripsi.absen.R
import com.skripsi.absen.utils.Constant
import com.skripsi.absen.utils.CustomProgressDialog
import kotlinx.android.synthetic.main.activity_izin_add.*
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class IzinAddActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    private var firestore: FirebaseFirestore? = null
    var useid : String? = null
    //loading
    private var progressDialog = CustomProgressDialog()
    var cal = Calendar.getInstance()
    var tanggal_izin: Timestamp? = null
    var izin : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_izin_add)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        useid = auth.currentUser!!.uid
        edt_ijin.setOnClickListener {
            opendate()
        }

        btn_izin.setOnClickListener {
            val alasan  = edt_alasan.text.toString().trim()

                if (tanggal_izin!=null && alasan.isNotEmpty()  && izin!=null){
                    set_izin(alasan)
                }else{
                    toast(getString(R.string.field))
                }
        }

        imageButton.setOnClickListener {
            finish()
        }
    }

    fun set_izin(alasan : String) {
        val key =
            FirebaseFirestore.getInstance().collection(Constant.izin).document().id

        val usermap : HashMap<String,Any?> = HashMap()
        usermap["tanggal_izin"] = tanggal_izin
        usermap["uid_pegawai"] = useid.toString()
        usermap["alasan"] = alasan
        usermap["status"] = "belum diverifikasi"
        usermap["tipe_ijin"] = izin.toString()
        usermap["id_izin"] = key

        val docref = firestore!!.collection(Constant.izin).document(key).set(usermap).addOnCompleteListener {
            if (it.isSuccessful){
                finish()
                toast("Izin sudah diajukan")
            }else{
                toast("coba lagi")
            }
        }
    }


    fun opendate() {
        val cal = Calendar.getInstance()
        val timeSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val kalender = SimpleDateFormat("dd/MM/yy hh:mm a").format(cal.time)
            edt_ijin.setText(kalender)
            tanggal_izin = Timestamp(cal.time)
        }

        DatePickerDialog(
            this,
            timeSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()

    }

    fun radioizin(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.radio_izin ->
                    if (checked) {
                        izin = "izin"
                    }
                R.id.radio_sakit ->
                    if (checked) {
                        izin = "sakit"
                    }

                R.id.radio_lain ->
                    if (checked) {
                        izin = "lain-lain"
                    }

            }
        }
    }
}