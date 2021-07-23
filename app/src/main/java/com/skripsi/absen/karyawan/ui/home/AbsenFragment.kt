package com.skripsi.absen.karyawan.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.alfanshter.udinlelangfix.Session.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.skripsi.absen.R
import com.skripsi.absen.adapter.AbsenViewHolder
import com.skripsi.absen.databinding.FragmentAbsenBinding
import com.skripsi.absen.model.AbsenModel
import com.skripsi.absen.utils.Constant
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.support.v4.find
import org.jetbrains.anko.support.v4.intentFor


class AbsenFragment : Fragment(),AnkoLogger {
    //adapter

    companion object{
        var notesList = mutableListOf<AbsenModel>()

        @SuppressLint("StaticFieldLeak")
        var mAdapter: AbsenViewHolder? = null
    }

    //database
    private var firestoreDB: FirebaseFirestore? = null
    lateinit var auth: FirebaseAuth
    var userID: String? = null
    lateinit var sessionManager: SessionManager

    lateinit var binding: FragmentAbsenBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_absen,container,false)
        sessionManager = SessionManager(requireContext().applicationContext)
        binding.shimmerLayout.startShimmer()

        auth = FirebaseAuth.getInstance()
        userID = auth.currentUser!!.uid
        firestoreDB = FirebaseFirestore.getInstance()

        getabsen()

        return  binding.root
    }

    fun getabsen() {
        notesList.clear()
        binding.rvAbsen.layoutManager = LinearLayoutManager(requireContext().applicationContext)
        binding.rvAbsen.setHasFixedSize(true)
        (binding.rvAbsen.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser!!.uid
        val firestore = FirebaseFirestore.getInstance()
        val setiings = firestore.firestoreSettings.isPersistenceEnabled
        val docref =
            firestore.collection(Constant.absen).whereEqualTo("uid_pegawai",userID.toString()).orderBy("tanggal_absen", Query.Direction.DESCENDING)
                .limit(10).get()
                .addOnSuccessListener { task ->
                    if (task.isEmpty) {
                        binding.shimmerLayout.stopShimmer()
                        binding.shimmerLayout.visibility = View.GONE
                        binding.txtAbsen.visibility = View.VISIBLE

                    }
                    else{

                        for (doc in task.documents) {
                            if (doc.exists()) {
                                try {
                                    binding.shimmerLayout.stopShimmer()
                                    binding.shimmerLayout.visibility = View.GONE
                                    binding.rvAbsen.visibility = View.VISIBLE
                                    val note = doc.toObject(AbsenModel::class.java)
                                    note!!.id_absen = doc.id
                                    notesList.add(note)
                                    mAdapter =
                                        AbsenViewHolder(
                                            notesList,
                                            requireContext().applicationContext,
                                            firestoreDB!!
                                        )
                                    mAdapter!!.setDialog(object : AbsenViewHolder.Dialog {
                                        override fun onClick(position: Int) {

                                        }

                                    })
                                    mAdapter!!.notifyDataSetChanged()
                                    binding.rvAbsen.adapter = mAdapter

                                }catch (e :Exception){
                                    info { "dinda ${e.message.toString()}" }

                                    return@addOnSuccessListener
                                }

                            }
                            else if (!doc.exists()){
                                binding.shimmerLayout.stopShimmer()
                                binding.shimmerLayout.visibility = View.GONE
                                binding.txtAbsen.visibility = View.VISIBLE
                            }
                        }
                    }
                }


    }




}