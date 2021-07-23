package com.skripsi.absen.pimpinan.uiabsensi

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.alfanshter.udinlelangfix.Session.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.skripsi.absen.R
import com.skripsi.absen.adapter.PimpinanDataViewHolder
import com.skripsi.absen.databinding.FragmentDataKaryawanPimpinanBinding
import com.skripsi.absen.model.AbsenModel
import com.skripsi.absen.model.PegawaiModel
import com.skripsi.absen.utils.Constant
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.*

class DataKaryawanPimpinanFragment : Fragment(), AnkoLogger {
    //adapter
    var mAdapter: PimpinanDataViewHolder? = null

    //database
    private var firestoreDB: FirebaseFirestore? = null
    lateinit var auth: FirebaseAuth
    var userID: String? = null

    lateinit var sessionManager: SessionManager

    lateinit var binding: FragmentDataKaryawanPimpinanBinding

    var notesList = mutableListOf<PegawaiModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_data_karyawan_pimpinan,
                container,
                false
            )
        binding.lifecycleOwner = this

        sessionManager = SessionManager(requireContext().applicationContext)
        auth = FirebaseAuth.getInstance()
        userID = auth.currentUser!!.uid
        firestoreDB = FirebaseFirestore.getInstance()

        getdata()

        return binding.root
    }

    fun getdata() {
        binding.rvIzinpimpinan.layoutManager =
            LinearLayoutManager(requireContext().applicationContext)
        binding.rvIzinpimpinan.setHasFixedSize(true)
        (binding.rvIzinpimpinan.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser!!.uid
        val firestore = FirebaseFirestore.getInstance()
        val setiings = firestore.firestoreSettings.isPersistenceEnabled
        val docref =
            firestore.collection(Constant.akunpegawai).orderBy("nama", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        notesList = mutableListOf<PegawaiModel>()
                        for (doc in task.result!!.documents) {
                            if (doc.exists()) {
                                try {
                                    binding.shimmerLayout.stopShimmer()
                                    binding.shimmerLayout.visibility = View.GONE
                                    binding.rvIzinpimpinan.visibility = View.VISIBLE
                                    val note = doc.toObject(PegawaiModel::class.java)
                                    note!!.tipe_akun = doc.id
                                    notesList.add(note)
                                    mAdapter =
                                        PimpinanDataViewHolder(
                                            notesList,
                                            requireContext().applicationContext,
                                            firestoreDB!!
                                        )
                                    mAdapter!!.setDialog(object : PimpinanDataViewHolder.Dialog {
                                        override fun onClick(position: Int, uid_pegawai: String) {
                                            val builder =
                                                AlertDialog.Builder(requireContext())
                                            builder.setTitle("Apakah anda ingin menghapus")
//builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

                                            builder.setPositiveButton(R.string.hapus) { dialog, which ->
                                                val docref =
                                                    firestoreDB!!.collection(Constant.akunpegawai)
                                                        .document(uid_pegawai).delete()
                                                        .addOnCompleteListener {
                                                            val ref = firestoreDB!!.collection(
                                                                Constant.absen
                                                            ).whereEqualTo(
                                                                "uid_pegawai",
                                                                uid_pegawai
                                                            ).get().addOnSuccessListener {
                                                                if (it.isEmpty) {

                                                                } else {
                                                                    for (hapus in it.documents) {
                                                                        val datahapus =
                                                                            hapus.toObject(
                                                                                AbsenModel::class.java
                                                                            )
                                                                        var idabsen =
                                                                            datahapus!!.id_absen.toString()
                                                                        val hapuref =
                                                                            firestoreDB!!.collection(
                                                                                Constant.absen
                                                                            ).document(idabsen)
                                                                                .delete()
                                                                                .addOnCompleteListener {
                                                                                    if (it.isSuccessful) {
                                                                                        onStart()
                                                                                    }
                                                                                }

                                                                    }
                                                                }

                                                            }
                                                        }
                                            }

                                            builder.setNegativeButton(R.string.batal) { dialog, which ->
                                            }

                                            builder.show()
                                        }


                                    })
                                    mAdapter!!.notifyDataSetChanged()
                                    binding.rvIzinpimpinan.adapter = mAdapter

                                } catch (e: Exception) {

                                    return@addOnCompleteListener
                                }

                            }
                        }

                    } else {
                        info { "dinda ${task.exception}" }
                    }
                }


    }

    override fun onStart() {
        super.onStart()
        notesList.clear()
        getdata()
    }


}