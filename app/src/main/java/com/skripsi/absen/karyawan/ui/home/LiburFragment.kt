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
import com.skripsi.absen.R
import com.skripsi.absen.adapter.LiburViewHolder
import com.skripsi.absen.databinding.FragmentLiburBinding
import com.skripsi.absen.karyawan.LiburActivity
import com.skripsi.absen.model.LiburModel
import com.skripsi.absen.utils.Constant
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.support.v4.startActivity


class LiburFragment : Fragment(), AnkoLogger {
    //adapter
    companion object {
         var mAdapter: LiburViewHolder? = null
    }

    //database
    private var firestoreDB: FirebaseFirestore? = null
    lateinit var auth: FirebaseAuth
    var userID: String? = null

    lateinit var sessionManager: SessionManager

    lateinit var binding: FragmentLiburBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_libur, container, false)
        sessionManager = SessionManager(requireContext().applicationContext)
        binding.shimmerLayout.startShimmer()

        auth = FirebaseAuth.getInstance()
        userID = auth.currentUser!!.uid
        firestoreDB = FirebaseFirestore.getInstance()


        binding.btnLibur.setOnClickListener {
            startActivity<LiburActivity>()
        }
        getlibur()
        return binding.root

    }

    fun getlibur() {
        binding.rvLibur.layoutManager = LinearLayoutManager(requireContext().applicationContext)
        binding.rvLibur.setHasFixedSize(true)
        (binding.rvLibur.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser!!.uid
        val firestore = FirebaseFirestore.getInstance()
        val setiings = firestore.firestoreSettings.isPersistenceEnabled
        info { "dinda ${userID.toString()}" }
        val docref =
            firestore.collection(Constant.libur).whereEqualTo("uid_pegawai", userID.toString())
                .limit(10).get()
                .addOnSuccessListener { task ->
                    if (task.isEmpty) {
                        binding.shimmerLayout.stopShimmer()
                        binding.shimmerLayout.visibility = View.GONE
                        binding.txtLibur.visibility = View.VISIBLE

                    } else {
                        val notesList = mutableListOf<LiburModel>()
                        for (doc in task.documents) {
                            if (doc.exists()) {
                                try {
                                    binding.shimmerLayout.stopShimmer()
                                    binding.shimmerLayout.visibility = View.GONE
                                    binding.rvLibur.visibility = View.VISIBLE
                                    val note = doc.toObject(LiburModel::class.java)
                                    note!!.uid = doc.id
                                    notesList.add(note)
                                    mAdapter =
                                        LiburViewHolder(
                                            notesList,
                                            requireContext().applicationContext,
                                            firestoreDB!!
                                        )
                                    mAdapter!!.setDialog(object : LiburViewHolder.Dialog {
                                        override fun onClick(position: Int) {

                                        }

                                    })
                                    mAdapter!!.notifyDataSetChanged()
                                    binding.rvLibur.adapter = mAdapter

                                } catch (e: Exception) {

                                        return@addOnSuccessListener
                                }

                            }
                        }


                    }
                }


    }

}