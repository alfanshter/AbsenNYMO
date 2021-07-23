package com.skripsi.absen.karyawan

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.skripsi.absen.R
import com.skripsi.absen.utils.Constant
import com.skripsi.absen.utils.CustomProgressDialog
import kotlinx.android.synthetic.main.activity_libur.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast
import org.joda.time.DateTime
import org.joda.time.Days
import java.text.SimpleDateFormat
import java.util.*

class LiburActivity : AppCompatActivity(), AnkoLogger {
    lateinit var auth: FirebaseAuth
    private var firestore: FirebaseFirestore? = null
    var useid: String? = null
    var tanggal_masuk: DateTime? = null
    var tanggal_selesai: DateTime? = null

    //loading
    private var progressDialog = CustomProgressDialog()
    var mulai_libur: Timestamp? = null
    var selesai_libur: Timestamp? = null
    var status: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_libur)


        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        useid = auth.currentUser!!.uid
        edt_libur.setOnClickListener {
            status = "mulai"
            opendate()
        }

        edt_selesailibur.setOnClickListener {
            status = "selesai"
            opendate()
        }

        imageButton.setOnClickListener {
            finish()
        }

        btn_libur.setOnClickListener {
            if (tanggal_masuk != null && tanggal_selesai!=null) {
                var jumlah_hari = Days.daysBetween(tanggal_masuk,tanggal_selesai).days

                if (jumlah_hari < 7) set_izin(jumlah_hari, "harian")
                if (jumlah_hari in 8..29) set_izin(jumlah_hari, "mingguan")
                if (jumlah_hari > 29) set_izin(jumlah_hari, "bulanan")
            } else {
                toast(getString(R.string.field))
            }
        }


    }


    fun set_izin(jumlah_hari: Int, jenis: String) {


        val sls_libur: DateTime = tanggal_masuk!!.plusDays(jumlah_hari.toInt())
        selesai_libur = Timestamp(sls_libur.toDate())
        val key =
            FirebaseFirestore.getInstance().collection(Constant.izin).document().id

        val usermap: HashMap<String, Any?> = HashMap()
        usermap["mulai_libur"] = mulai_libur
        usermap["selesai_libur"] = selesai_libur
        usermap["uid_pegawai"] = useid.toString()
        usermap["jumlah_hari"] = jumlah_hari + 1
        usermap["jenis"] = jenis
        usermap["status"] = "Belum di verifikasi"
        usermap["id_libur"] = key

        val docref = firestore!!.collection(Constant.libur).document(key).set(usermap)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    finish()
                    toast("libur sudah diajukan")
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
            if (status.equals("mulai")){
                val tambah =
                    edt_libur.setText(kalender)
                tanggal_masuk = DateTime(cal.time)
                mulai_libur = Timestamp(cal.time)
            }
            if (status.equals("selesai")){
                val tambah =
                    edt_selesailibur.setText(kalender)
                tanggal_selesai = DateTime(cal.time)
                selesai_libur = Timestamp(cal.time)

            }

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