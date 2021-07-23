package com.skripsi.absen.admin

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.skripsi.absen.R
import com.skripsi.absen.karyawan.ui.home.LiburFragment
import com.skripsi.absen.utils.Constant
import com.skripsi.absen.utils.CustomProgressDialog
import kotlinx.android.synthetic.main.activity_tambah_lembur.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.joda.time.DateTime
import java.text.SimpleDateFormat
import java.util.*

class TambahLemburActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    private var firestore: FirebaseFirestore? = null
    var useid: String? = null
    var tanggal_lembur: Timestamp? = null

    //loading
    private var progressDialog = CustomProgressDialog()
    var selesai_libur: Timestamp? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_lembur)


        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        useid = auth.currentUser!!.uid
        edt_lembur.setOnClickListener {
            opendate()
        }

        btn_lembur.setOnClickListener {
            val jumlah_lembur = edt_jam.text.toString().trim()

            set_izin(jumlah_lembur,"harian")
        }

        imageButton.setOnClickListener {
            finish()
        }
    }


    fun set_izin(jumlah_lembur: String, jenis : String) {


        val key =
            FirebaseFirestore.getInstance().collection(Constant.lembur).document().id

        val usermap: HashMap<String, Any?> = HashMap()
        usermap["id_lembur"] = key
        usermap["bonus"] = 0
        usermap["tanggal_kerja"] = tanggal_lembur
        usermap["waktu_lembur"] = jumlah_lembur.toInt()
        usermap["status"] = "Anda wajib lembur"

        val docref = firestore!!.collection(Constant.lembur).document(key).set(usermap)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    startActivity<LemburActivity>()
                    finish()
                    toast("lembur sudah ditambahkan")
                } else {
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
            val tambah =
                edt_lembur.setText(kalender)
            tanggal_lembur = Timestamp(cal.time)

        }

        DatePickerDialog(
            this,
            timeSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()

    }


}