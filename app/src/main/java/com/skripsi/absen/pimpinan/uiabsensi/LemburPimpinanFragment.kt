package com.skripsi.absen.pimpinan.uiabsensi

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
import com.skripsi.absen.adapter.PimpinanLemburViewHolder
import com.skripsi.absen.databinding.FragmentLemburBinding
import com.skripsi.absen.databinding.FragmentLemburPimpinanBinding
import com.skripsi.absen.model.LemburModel
import com.skripsi.absen.utils.Constant
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class LemburPimpinanFragment : Fragment(),AnkoLogger {
    //adapter
    var mAdapter: PimpinanLemburViewHolder? = null

    //database
    private var firestoreDB: FirebaseFirestore? = null
    lateinit var auth: FirebaseAuth
    var userID: String? = null

    lateinit var sessionManager: SessionManager

    lateinit var binding: FragmentLemburPimpinanBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_lembur_pimpinan, container, false)
        binding.lifecycleOwner = this

        sessionManager = SessionManager(requireContext().applicationContext)
        auth = FirebaseAuth.getInstance()
        userID = auth.currentUser!!.uid
        firestoreDB = FirebaseFirestore.getInstance()

        getlembur()

        return binding.root
    }

    fun getlembur() {
        binding.rvLemburpimpinan.layoutManager = LinearLayoutManager(requireContext().applicationContext)
        binding.rvLemburpimpinan.setHasFixedSize(true)
        (binding.rvLemburpimpinan.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser!!.uid
        val firestore = FirebaseFirestore.getInstance()
        val setiings = firestore.firestoreSettings.isPersistenceEnabled
        val docref =
            firestore.collection(Constant.lembur).orderBy("tanggal_kerja", Query.Direction.DESCENDING).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val notesList = mutableListOf<LemburModel>()
                        for (doc in task.result!!.documents) {
                            if (doc.exists()) {
                                try {
                                    binding.shimmerLayout.stopShimmer()
                                    binding.shimmerLayout.visibility = View.GONE
                                    binding.rvLemburpimpinan.visibility = View.VISIBLE
                                    val note = doc.toObject(LemburModel::class.java)
                                    note!!.id_lembur = doc.id
                                    notesList.add(note)
                                    mAdapter =
                                        PimpinanLemburViewHolder(
                                            notesList,
                                            requireContext().applicationContext,
                                            firestoreDB!!
                                        )
                                    mAdapter!!.setDialog(object : PimpinanLemburViewHolder.Dialog {
                                        override fun onClick(position: Int) {

                                        }


                                    })
                                    mAdapter!!.notifyDataSetChanged()
                                    binding.rvLemburpimpinan.adapter = mAdapter

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



}