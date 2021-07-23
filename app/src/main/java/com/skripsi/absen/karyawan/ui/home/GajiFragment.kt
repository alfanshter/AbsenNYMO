package com.skripsi.absen.karyawan.ui.home

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
import com.skripsi.absen.adapter.GajiViewHolder
import com.skripsi.absen.databinding.FragmentGajiBinding
import com.skripsi.absen.model.GajiAdminModel
import com.skripsi.absen.utils.Constant

class GajiFragment : Fragment() {
    lateinit var binding : FragmentGajiBinding
    //adapter
    companion object {
        var mAdapter: GajiViewHolder? = null
    }

    //database
    private var firestoreDB: FirebaseFirestore? = null
    lateinit var auth: FirebaseAuth
    var userID: String? = null

    lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_gaji,container,false)
        binding.lifecycleOwner = this
        sessionManager = SessionManager(requireContext().applicationContext)
        binding.shimmerLayout.startShimmer()

        auth = FirebaseAuth.getInstance()
        userID = auth.currentUser!!.uid
        firestoreDB = FirebaseFirestore.getInstance()

        getgaji()
        return binding.root
    }

    fun getgaji() {
        binding.rvLembur.layoutManager = LinearLayoutManager(requireContext().applicationContext)
        binding.rvLembur.setHasFixedSize(true)
        (binding.rvLembur.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser!!.uid
        val firestore = FirebaseFirestore.getInstance()
        val setiings = firestore.firestoreSettings.isPersistenceEnabled
        val docref =
            firestore.collection(Constant.gaji).orderBy("tanggal", Query.Direction.DESCENDING).get()
                .addOnSuccessListener { task ->
                    if (task.isEmpty) {
                        binding.shimmerLayout.stopShimmer()
                        binding.shimmerLayout.visibility = View.GONE
                        binding.txtLembur.visibility = View.VISIBLE

                    } else {
                        val notesList = mutableListOf<GajiAdminModel>()
                        for (doc in task.documents) {
                            if (doc.exists()) {
                                try {
                                    binding.shimmerLayout.stopShimmer()
                                    binding.shimmerLayout.visibility = View.GONE
                                    binding.rvLembur.visibility = View.VISIBLE
                                    val note = doc.toObject(GajiAdminModel::class.java)
                                    note!!.id_gaji = doc.id
                                    notesList.add(note)
                                    mAdapter =
                                        GajiViewHolder(
                                            notesList,
                                            requireContext().applicationContext,
                                            firestoreDB!!
                                        )
                                    mAdapter!!.setDialog(object : GajiViewHolder.Dialog {
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