package com.skripsi.absen.pimpinan.uiabsensi

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
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
import com.skripsi.absen.adapter.PimpinanAbsenViewHolder
import com.skripsi.absen.databinding.FragmentAbsensiKaryawanBinding
import com.skripsi.absen.model.AbsenModel
import com.skripsi.absen.utils.Constant
import kotlinx.android.synthetic.main.fragment_absensi_karyawan.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class AbsensiKaryawanFragment : Fragment(), AnkoLogger {
    //adapter
    var mAdapter: PimpinanAbsenViewHolder? = null

    //database
    private var firestoreDB: FirebaseFirestore? = null
    lateinit var auth: FirebaseAuth
    var userID: String? = null
     var dialog: AlertDialog.Builder? = null

    lateinit var sessionManager: SessionManager
    var notesList = mutableListOf<AbsenModel>()

    lateinit var binding: FragmentAbsensiKaryawanBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_absensi_karyawan, container, false)
        binding.lifecycleOwner = this

        sessionManager = SessionManager(requireContext().applicationContext)
        auth = FirebaseAuth.getInstance()
        userID = auth.currentUser!!.uid
        firestoreDB = FirebaseFirestore.getInstance()

        getabsen()

        return binding.root
    }

    fun getabsen() {
        binding.rvAbsenpimpinan.layoutManager =
            LinearLayoutManager(requireContext().applicationContext)
        binding.rvAbsenpimpinan.setHasFixedSize(true)
        (binding.rvAbsenpimpinan.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser!!.uid
        val firestore = FirebaseFirestore.getInstance()
        val setiings = firestore.firestoreSettings.isPersistenceEnabled
        val docref =
            firestore.collection(Constant.absen)
                .orderBy("tanggal_absen", Query.Direction.DESCENDING).get()
                .addOnSuccessListener { task ->
                    if (task.isEmpty) {
                        binding.shimmerLayout.stopShimmer()
                        binding.shimmerLayout.visibility = View.GONE
                        binding.tidakadaabasen.visibility = View.VISIBLE

                    } else {
                        for (doc in task.documents) {
                            if (doc.exists()) {
                                try {
                                    binding.shimmerLayout.stopShimmer()
                                    binding.shimmerLayout.visibility = View.GONE
                                    binding.rvAbsenpimpinan.visibility = View.VISIBLE
                                    val note = doc.toObject(AbsenModel::class.java)
                                    note!!.uid = doc.id
                                    notesList.add(note)
                                    mAdapter =
                                        PimpinanAbsenViewHolder(
                                            notesList,
                                            requireContext().applicationContext,
                                            firestoreDB!!
                                        )
                                    mAdapter!!.setDialog(object : PimpinanAbsenViewHolder.Dialog {
                                        override fun onClick(position: Int) {

                                        }


                                    })
                                    mAdapter!!.notifyDataSetChanged()
                                    binding.rvAbsenpimpinan.adapter = mAdapter

                                } catch (e: Exception) {

                                        return@addOnSuccessListener
                                }

                            }else{
                                binding.shimmerLayout.stopShimmer()
                                binding.shimmerLayout.visibility = View.GONE
                                binding.tidakadaabasen.visibility = View.VISIBLE

                            }
                        }
                    }
                }


    }

    override fun onStart() {
        super.onStart()
        notesList.clear()
    }


}