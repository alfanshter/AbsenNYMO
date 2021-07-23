package com.skripsi.absen.karyawan.ui.home

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
import com.skripsi.absen.adapter.LemburViewHolder
import com.skripsi.absen.databinding.FragmentLemburBinding
import com.skripsi.absen.model.LemburModel
import com.skripsi.absen.utils.Constant
import org.jetbrains.anko.info

class LemburFragment : Fragment() {
    lateinit var binding : FragmentLemburBinding
    //adapter
    companion object {
        var mAdapter: LemburViewHolder? = null
    }

    //database
    private var firestoreDB: FirebaseFirestore? = null
    lateinit var auth: FirebaseAuth
    var userID: String? = null

    lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_lembur,container,false)
        binding.lifecycleOwner = this
        sessionManager = SessionManager(requireContext().applicationContext)
        binding.shimmerLayout.startShimmer()

        auth = FirebaseAuth.getInstance()
        userID = auth.currentUser!!.uid
        firestoreDB = FirebaseFirestore.getInstance()

        getlembur()
        return binding.root

    }

    fun getlembur() {
        binding.rvLembur.layoutManager = LinearLayoutManager(requireContext().applicationContext)
        binding.rvLembur.setHasFixedSize(true)
        (binding.rvLembur.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser!!.uid
        val firestore = FirebaseFirestore.getInstance()
        val setiings = firestore.firestoreSettings.isPersistenceEnabled
        val docref =
            firestore.collection(Constant.lembur).orderBy("tanggal_kerja",Query.Direction.DESCENDING).get()
                .addOnSuccessListener { task ->
                    if (task.isEmpty) {
                        binding.shimmerLayout.stopShimmer()
                        binding.shimmerLayout.visibility = View.GONE
                        binding.txtLembur.visibility = View.VISIBLE

                    } else {
                        val notesList = mutableListOf<LemburModel>()
                        for (doc in task.documents) {
                            if (doc.exists()) {
                                try {
                                    binding.shimmerLayout.stopShimmer()
                                    binding.shimmerLayout.visibility = View.GONE
                                    binding.rvLembur.visibility = View.VISIBLE
                                    val note = doc.toObject(LemburModel::class.java)
                                    note!!.id_lembur = doc.id
                                    notesList.add(note)
                                    mAdapter =
                                        LemburViewHolder(
                                            notesList,
                                            requireContext().applicationContext,
                                            firestoreDB!!
                                        )
                                    mAdapter!!.setDialog(object : LemburViewHolder.Dialog {
                                        override fun onClick(position: Int) {

                                        }

                                    })
                                    mAdapter!!.notifyDataSetChanged()
                                    binding.rvLembur.adapter = mAdapter

                                } catch (e: Exception) {

                                    return@addOnSuccessListener
                                }

                            }
                        }


                    }
                }


    }



}