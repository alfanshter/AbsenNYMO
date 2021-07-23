package com.skripsi.absen.admin

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.alfanshter.udinlelangfix.Session.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.skripsi.absen.R
import com.skripsi.absen.adapter.LemburAdminViewHolder
import com.skripsi.absen.model.LemburModel
import com.skripsi.absen.utils.Constant
import kotlinx.android.synthetic.main.activity_lembur.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class LemburActivity : AppCompatActivity(),AnkoLogger {

    //adapter
    var mAdapter: LemburAdminViewHolder? = null

    //database
    private var firestoreDB: FirebaseFirestore? = null
    lateinit var auth: FirebaseAuth
    var userID: String? = null

    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lembur)


        sessionManager = SessionManager(this)
        auth = FirebaseAuth.getInstance()
        userID = auth.currentUser!!.uid
        firestoreDB = FirebaseFirestore.getInstance()

        imageButton.setOnClickListener {
            finish()
        }


        getlembur()

        btn_tambah.setOnClickListener {
            startActivity<TambahLemburActivity>()
        }
    }

    fun getlembur() {
        rv_lembur.layoutManager = LinearLayoutManager(this)
        rv_lembur.setHasFixedSize(true)
        (rv_lembur.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser!!.uid
        val firestore = FirebaseFirestore.getInstance()
        val setiings = firestore.firestoreSettings.isPersistenceEnabled
        val docref =
            firestore.collection(Constant.lembur).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val notesList = mutableListOf<LemburModel>()
                        for (doc in task.result!!.documents) {
                            if (doc.exists()) {
                                try {
                                    shimmerLayout.stopShimmer()
                                    shimmerLayout.visibility = View.GONE
                                    rv_lembur.visibility = View.VISIBLE
                                    val note = doc.toObject(LemburModel::class.java)
                                    info { "dinda $note" }
                                    note!!.id_lembur = doc.id
                                    notesList.add(note)
                                    mAdapter =
                                        LemburAdminViewHolder(
                                            notesList,
                                            this,
                                            firestoreDB!!
                                        )
                                    mAdapter!!.setDialog(object : LemburAdminViewHolder.Dialog {

                                        override fun onTerima(position: Int, id_libur: String) {
                                            firestoreDB!!.collection(Constant.libur)
                                                .document(id_libur)
                                                .update("status", "Anda sedang libur")
                                                .addOnCompleteListener {
                                                    if (it.isSuccessful) {
                                                        toast("Berhasil di terima")
                                                    } else {
                                                        toast("kesalahan jaringan")
                                                    }
                                                }
                                        }


                                    })
                                    mAdapter!!.notifyDataSetChanged()
                                    rv_lembur.adapter = mAdapter

                                } catch (e: Exception) {
                                    info { "dinda ${e.message}" }
                                    return@addOnCompleteListener
                                }

                            }
                        }

                    } else {
                    }
                }


    }
}