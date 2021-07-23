package com.skripsi.absen.pimpinan.uiabsensi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.alfanshter.udinlelangfix.Session.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.skripsi.absen.R
import com.skripsi.absen.adapter.PimpinanLiburViewHolder
import com.skripsi.absen.databinding.FragmentAbsensiKaryawanBinding
import com.skripsi.absen.databinding.FragmentLiburPimpinanBinding
import com.skripsi.absen.model.LiburModel
import com.skripsi.absen.utils.Constant
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info


class LiburPimpinanFragment : Fragment(),AnkoLogger {
    //adapter
    var mAdapter: PimpinanLiburViewHolder? = null

    //database
    private var firestoreDB: FirebaseFirestore? = null
    lateinit var auth: FirebaseAuth
    var userID: String? = null

    lateinit var sessionManager: SessionManager

    lateinit var binding: FragmentLiburPimpinanBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_libur_pimpinan, container, false)
        binding.lifecycleOwner = this

        sessionManager = SessionManager(requireContext().applicationContext)
        auth = FirebaseAuth.getInstance()
        userID = auth.currentUser!!.uid
        firestoreDB = FirebaseFirestore.getInstance()

        getlibur()

        return binding.root
    }

    fun getlibur() {
        binding.rvLiburpimpinan.layoutManager = LinearLayoutManager(requireContext().applicationContext)
        binding.rvLiburpimpinan.setHasFixedSize(true)
        (binding.rvLiburpimpinan.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser!!.uid
        val firestore = FirebaseFirestore.getInstance()
        val setiings = firestore.firestoreSettings.isPersistenceEnabled
        val docref =
            firestore.collection(Constant.libur).orderBy("mulai_libur", Query.Direction.DESCENDING).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val notesList = mutableListOf<LiburModel>()
                        for (doc in task.result!!.documents) {
                            if (doc.exists()) {
                                try {
                                    binding.shimmerLayout.stopShimmer()
                                    binding.shimmerLayout.visibility = View.GONE
                                    binding.rvLiburpimpinan.visibility = View.VISIBLE
                                    val note = doc.toObject(LiburModel::class.java)
                                    note!!.uid = doc.id
                                    notesList.add(note)
                                    mAdapter =
                                        PimpinanLiburViewHolder(
                                            notesList,
                                            requireContext().applicationContext,
                                            firestoreDB!!
                                        )
                                    mAdapter!!.setDialog(object : PimpinanLiburViewHolder.Dialog {
                                        override fun onClick(position: Int) {

                                        }


                                    })
                                    mAdapter!!.notifyDataSetChanged()
                                    binding.rvLiburpimpinan.adapter = mAdapter

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