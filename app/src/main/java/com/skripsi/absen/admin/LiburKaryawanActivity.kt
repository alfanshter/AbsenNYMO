package com.skripsi.absen.admin

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.alfanshter.udinlelangfix.Session.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.skripsi.absen.R
import com.skripsi.absen.adapter.LiburAdminViewHolder
import com.skripsi.absen.model.LiburModel
import com.skripsi.absen.utils.Constant
import kotlinx.android.synthetic.main.activity_libur_karyawan.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.toast

class LiburKaryawanActivity : AppCompatActivity(), AnkoLogger {

    //adapter
    var mAdapter: LiburAdminViewHolder? = null

    //database
    private var firestoreDB: FirebaseFirestore? = null
    lateinit var auth: FirebaseAuth
    var userID: String? = null

    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_libur_karyawan)

        sessionManager = SessionManager(this)
        auth = FirebaseAuth.getInstance()
        userID = auth.currentUser!!.uid
        firestoreDB = FirebaseFirestore.getInstance()

        imageButton.setOnClickListener {
            finish()
        }


        getlibur()

    }

    fun getlibur() {
        rv_libur.layoutManager = LinearLayoutManager(this)
        rv_libur.setHasFixedSize(true)
        (rv_libur.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser!!.uid
        val firestore = FirebaseFirestore.getInstance()
        val setiings = firestore.firestoreSettings.isPersistenceEnabled
        val docref =
            firestore.collection(Constant.libur).get()
                .addOnSuccessListener { task ->
                    if (task.isEmpty) {
                        shimmerLayout.stopShimmer()
                        shimmerLayout.visibility = View.GONE
                        tidakadalibur.visibility = View.VISIBLE
                    } else {
                        val notesList = mutableListOf<LiburModel>()
                        for (doc in task.documents) {
                            if (doc.exists()) {
                                try {
                                    shimmerLayout.stopShimmer()
                                    shimmerLayout.visibility = View.GONE
                                    rv_libur.visibility = View.VISIBLE
                                    val note = doc.toObject(LiburModel::class.java)
                                    note!!.uid = doc.id
                                    notesList.add(note)
                                    mAdapter =
                                        LiburAdminViewHolder(
                                            notesList,
                                            this,
                                            firestoreDB!!
                                        )
                                    mAdapter!!.setDialog(object : LiburAdminViewHolder.Dialog {

                                        override fun onTerima(
                                            position: Int, id_libur: String,
                                            terima: ImageButton,
                                            tolak: ImageButton
                                        ) {
                                            firestoreDB!!.collection(Constant.libur)
                                                .document(id_libur)
                                                .update("status", Constant.diterima)
                                                .addOnCompleteListener {
                                                    if (it.isSuccessful) {
                                                        terima.setBackgroundResource(R.drawable.bt_terimadone)
                                                        tolak.setBackgroundResource(R.drawable.bt_tolak)
                                                        toast("Berhasil di terima")
                                                    } else {
                                                        toast("kesalahan jaringan")
                                                    }
                                                }
                                        }

                                        override fun onTolak(
                                            position: Int, id_libur: String,
                                            terima: ImageButton,
                                            tolak: ImageButton
                                        ) {
                                            firestoreDB!!.collection(Constant.libur)
                                                .document(id_libur)
                                                .update("status", Constant.ditolak)
                                                .addOnCompleteListener {
                                                    if (it.isSuccessful) {
                                                        tolak.setBackgroundResource(R.drawable.bt_tolakdone)
                                                        terima.setBackgroundResource(R.drawable.bt_terima)
                                                        toast("Berhasil di tolak")
                                                    } else {
                                                        toast("kesalahan jaringan")
                                                    }
                                                }

                                        }


                                    })
                                    mAdapter!!.notifyDataSetChanged()
                                    rv_libur.adapter = mAdapter

                                } catch (e: Exception) {

                                    return@addOnSuccessListener
                                }

                            }
                        }

                    }
                }


    }
}