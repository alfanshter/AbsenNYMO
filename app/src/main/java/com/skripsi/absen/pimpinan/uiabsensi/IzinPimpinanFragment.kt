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
import com.skripsi.absen.adapter.PimpinanIzinViewHolder
import com.skripsi.absen.databinding.FragmentIzinPimpinanBinding
import com.skripsi.absen.model.IjinModel
import com.skripsi.absen.utils.Constant
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class IzinPimpinanFragment : Fragment(),AnkoLogger {
    //adapter
    var mAdapter: PimpinanIzinViewHolder? = null

    //database
    private var firestoreDB: FirebaseFirestore? = null
    lateinit var auth: FirebaseAuth
    var userID: String? = null

    lateinit var sessionManager: SessionManager

    lateinit var binding: FragmentIzinPimpinanBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_izin_pimpinan, container, false)
        binding.lifecycleOwner = this

        sessionManager = SessionManager(requireContext().applicationContext)
        auth = FirebaseAuth.getInstance()
        userID = auth.currentUser!!.uid
        firestoreDB = FirebaseFirestore.getInstance()

        getizin()

        return binding.root
    }

    fun getizin() {
        binding.rvIzinpimpinan.layoutManager = LinearLayoutManager(requireContext().applicationContext)
        binding.rvIzinpimpinan.setHasFixedSize(true)
        (binding.rvIzinpimpinan.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser!!.uid
        val firestore = FirebaseFirestore.getInstance()
        val setiings = firestore.firestoreSettings.isPersistenceEnabled
        val docref =
            firestore.collection(Constant.izin).orderBy("tanggal_izin", Query.Direction.DESCENDING).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val notesList = mutableListOf<IjinModel>()
                        for (doc in task.result!!.documents) {
                            if (doc.exists()) {
                                try {
                                    binding.shimmerLayout.stopShimmer()
                                    binding.shimmerLayout.visibility = View.GONE
                                    binding.rvIzinpimpinan.visibility = View.VISIBLE
                                    val note = doc.toObject(IjinModel::class.java)
                                    note!!.id_izin = doc.id
                                    notesList.add(note)
                                    mAdapter =
                                        PimpinanIzinViewHolder(
                                            notesList,
                                            requireContext().applicationContext,
                                            firestoreDB!!
                                        )
                                    mAdapter!!.setDialog(object : PimpinanIzinViewHolder.Dialog {
                                        override fun onClick(position: Int) {

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


}