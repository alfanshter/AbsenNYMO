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
import com.skripsi.absen.adapter.IjinAdminViewHolder
import com.skripsi.absen.model.IjinModel
import com.skripsi.absen.utils.Constant
import kotlinx.android.synthetic.main.activity_izin.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.toast

class IzinActivity : AppCompatActivity(), AnkoLogger {

    //adapter
    var mAdapter: IjinAdminViewHolder? = null

    //database
    private var firestoreDB: FirebaseFirestore? = null
    lateinit var auth: FirebaseAuth
    var userID: String? = null

    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_izin)


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
        rv_ijin.layoutManager = LinearLayoutManager(this)
        rv_ijin.setHasFixedSize(true)
        (rv_ijin.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser!!.uid
        val firestore = FirebaseFirestore.getInstance()
        val setiings = firestore.firestoreSettings.isPersistenceEnabled
        val docref =
            firestore.collection(Constant.izin).get()
                .addOnSuccessListener { task ->
                    if (task.isEmpty) {
                        shimmerLayout.stopShimmer()
                        shimmerLayout.visibility = View.GONE
                        tidakadaizin.visibility = View.VISIBLE
                    } else {
                        val notesList = mutableListOf<IjinModel>()
                        for (doc in task.documents) {
                            if (doc.exists()) {
                                try {
                                    shimmerLayout.stopShimmer()
                                    shimmerLayout.visibility = View.GONE
                                    rv_ijin.visibility = View.VISIBLE
                                    val note = doc.toObject(IjinModel::class.java)
                                    note!!.uid = doc.id
                                    notesList.add(note)
                                    mAdapter =
                                        IjinAdminViewHolder(
                                            notesList,
                                            this,
                                            firestoreDB!!
                                        )
                                    mAdapter!!.setDialog(object : IjinAdminViewHolder.Dialog {

                                        override fun onTerima(
                                            position: Int, id_libur: String,
                                            terima: ImageButton,
                                            tolak: ImageButton
                                        ) {
                                            firestoreDB!!.collection(Constant.izin)
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
                                            firestoreDB!!.collection(Constant.izin)
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
                                    rv_ijin.adapter = mAdapter

                                } catch (e: Exception) {

                                   return@addOnSuccessListener
                                }

                            }
                        }

                    }
                }


    }
}