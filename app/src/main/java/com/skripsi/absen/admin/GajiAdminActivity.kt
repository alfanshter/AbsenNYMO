package com.skripsi.absen.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.alfanshter.udinlelangfix.Session.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.skripsi.absen.R
import com.skripsi.absen.adapter.GajiAdminViewHolder
import com.skripsi.absen.model.GajiAdminModel
import com.skripsi.absen.utils.Constant
import kotlinx.android.synthetic.main.activity_gaji_admin.*
import kotlinx.android.synthetic.main.activity_libur_karyawan.*
import kotlinx.android.synthetic.main.activity_libur_karyawan.imageButton
import kotlinx.android.synthetic.main.activity_libur_karyawan.rv_libur
import kotlinx.android.synthetic.main.activity_libur_karyawan.shimmerLayout
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.toast

class GajiAdminActivity : AppCompatActivity(),AnkoLogger {

    //adapter
    var mAdapter: GajiAdminViewHolder? = null

    //database
    private var firestoreDB: FirebaseFirestore? = null
    lateinit var auth: FirebaseAuth
    var userID: String? = null

    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gaji_admin)

        sessionManager = SessionManager(this)
        auth = FirebaseAuth.getInstance()
        userID = auth.currentUser!!.uid
        firestoreDB = FirebaseFirestore.getInstance()

        imageButton.setOnClickListener {
            finish()
        }


        getgaji()
    }

    fun getgaji() {
        rv_gaji.layoutManager = LinearLayoutManager(this)
        rv_gaji.setHasFixedSize(true)
        (rv_gaji.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser!!.uid
        val firestore = FirebaseFirestore.getInstance()
        val setiings = firestore.firestoreSettings.isPersistenceEnabled
        val docref =
            firestore.collection(Constant.gajiadmin).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val notesList = mutableListOf<GajiAdminModel>()
                        for (doc in task.result!!.documents) {
                            if (doc.exists()) {
                                try {
                                    shimmerLayout.stopShimmer()
                                    shimmerLayout.visibility = View.GONE
                                    rv_gaji.visibility = View.VISIBLE
                                    val note = doc.toObject(GajiAdminModel::class.java)
                                    note!!.id_gaji = doc.id
                                    notesList.add(note)
                                    mAdapter =
                                        GajiAdminViewHolder(
                                            notesList,
                                            this,
                                            firestoreDB!!
                                        )

                                    mAdapter!!.notifyDataSetChanged()
                                    rv_gaji.adapter = mAdapter

                                } catch (e: Exception) {
                                    info { "dinda ${e.message}" }

                                    return@addOnCompleteListener
                                }

                            }
                        }

                    } else {
                        info { "dinda ${task.exception}" }
                    }
                }


    }
}