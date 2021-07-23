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
import com.skripsi.absen.adapter.IjinViewHolder
import com.skripsi.absen.databinding.FragmentIzinBinding
import com.skripsi.absen.karyawan.IzinAddActivity
import com.skripsi.absen.model.IjinModel
import com.skripsi.absen.utils.Constant
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.support.v4.startActivity


class IzinFragment : Fragment(),AnkoLogger {
    //adapter
    private var mAdapter: IjinViewHolder? = null

    //database
    private var firestoreDB: FirebaseFirestore? = null
    lateinit var auth: FirebaseAuth
    var userID: String? = null

    lateinit var sessionManager: SessionManager

    lateinit var binding: FragmentIzinBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_izin, container, false)
        sessionManager = SessionManager(requireContext().applicationContext)
        binding.shimmerLayout.startShimmer()

        auth = FirebaseAuth.getInstance()
        userID = auth.currentUser!!.uid
        firestoreDB = FirebaseFirestore.getInstance()


        binding.btnIzin.setOnClickListener {
            startActivity<IzinAddActivity>()
        }
        getizin()
        return binding.root

    }


    fun getizin() {
        binding.rvIzin.layoutManager = LinearLayoutManager(requireContext().applicationContext)
        binding.rvIzin.setHasFixedSize(true)
        (binding.rvIzin.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser!!.uid
        val firestore = FirebaseFirestore.getInstance()
        val setiings = firestore.firestoreSettings.isPersistenceEnabled
        info { "dinda ${userID.toString()}" }
        val docref =
            firestore.collection(Constant.izin).whereEqualTo("uid_pegawai", userID.toString())
                .orderBy("tanggal_izin", Query.Direction.DESCENDING)
                .limit(10).get()
                .addOnSuccessListener { task ->
                    if (task.isEmpty) {
                        binding.shimmerLayout.stopShimmer()
                        binding.shimmerLayout.visibility = View.GONE
                        binding.txtIjin.visibility = View.VISIBLE

                    } else {
                        val notesList = mutableListOf<IjinModel>()
                        info { "dinda ${task.documents}" }
                        for (doc in task.documents) {
                            if (doc.exists()) {
                                try {
                                    binding.shimmerLayout.stopShimmer()
                                    binding.shimmerLayout.visibility = View.GONE
                                    binding.rvIzin.visibility = View.VISIBLE
                                    val note = doc.toObject(IjinModel::class.java)
                                    note!!.uid = doc.id
                                    notesList.add(note)
                                    mAdapter =
                                        IjinViewHolder(
                                            notesList,
                                            requireContext().applicationContext,
                                            firestoreDB!!
                                        )
                                    mAdapter!!.setDialog(object : IjinViewHolder.Dialog {
                                        override fun onClick(position: Int) {

                                        }

                                    })
                                    mAdapter!!.notifyDataSetChanged()
                                    binding.rvIzin.adapter = mAdapter

                                } catch (e: Exception) {

                                                return@addOnSuccessListener
                                }

                            }
                        }
                    }
                }


    }

}