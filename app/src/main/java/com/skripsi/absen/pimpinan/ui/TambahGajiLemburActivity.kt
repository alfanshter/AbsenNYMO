package com.skripsi.absen.pimpinan.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.skripsi.absen.R
import com.skripsi.absen.utils.Constant
import com.skripsi.absen.utils.CustomProgressDialog
import kotlinx.android.synthetic.main.activity_tambah_gaji_lembur.*
import org.jetbrains.anko.toast

class TambahGajiLemburActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    private var firestore: FirebaseFirestore? = null
    var useid: String? = null
    private var progressDialog = CustomProgressDialog()

    var id_lembur: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_gaji_lembur)

        val bundle: Bundle? = intent.extras
        id_lembur = bundle!!.getString("id_lembur")

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        useid = auth.currentUser!!.uid


        btn_simpan.setOnClickListener {
            val gaji = edt_gaji.text.toString().trim()
            if (gaji.isNotEmpty()) {
                progressDialog.show(this, getString(R.string.loading))
                val docref = firestore!!.collection(Constant.lembur).document(id_lembur.toString()).update("bonus",gaji.toInt()).addOnCompleteListener {
                    if (it.isSuccessful){
                        progressDialog.dialog.dismiss()
                        toast("Gaji berhasil ditambahkan")
                        finish()
                    }else{
                        progressDialog.dialog.dismiss()
                        toast("Koneksi jaringan")

                    }
                }
            } else {
                toast("jangan kosongi kolom")
            }

        }


    }
}