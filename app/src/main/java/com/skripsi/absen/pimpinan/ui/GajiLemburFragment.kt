package com.skripsi.absen.pimpinan.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.alfanshter.udinlelangfix.Session.SessionManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.skripsi.absen.MonthYearPickerDialog
import com.skripsi.absen.R
import com.skripsi.absen.adapter.PimpinanLemburViewHolder
import com.skripsi.absen.databinding.FragmentGajiLemburBinding
import com.skripsi.absen.databinding.FragmentLemburPimpinanBinding
import com.skripsi.absen.model.LemburModel
import com.skripsi.absen.utils.Constant
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.newTask
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.support.v4.toast
import java.text.SimpleDateFormat

class GajiLemburFragment : Fragment(),AnkoLogger {
    var mAdapter: PimpinanLemburViewHolder? = null

    //database
    private var firestoreDB: FirebaseFirestore? = null
    lateinit var auth: FirebaseAuth
    var userID: String? = null

    lateinit var sessionManager: SessionManager


    lateinit var binding: FragmentGajiLemburBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_gaji_lembur, container, false)
        binding.lifecycleOwner = this

        sessionManager = SessionManager(requireContext().applicationContext)
        auth = FirebaseAuth.getInstance()
        userID = auth.currentUser!!.uid
        firestoreDB = FirebaseFirestore.getInstance()

        getlembur()
        return binding.root

    }

    fun getlembur() {
        binding.rvIzinpimpinan.layoutManager = LinearLayoutManager(requireContext().applicationContext)
        binding.rvIzinpimpinan.setHasFixedSize(true)
        (binding.rvIzinpimpinan.layoutManager as LinearLayoutManager).orientation =
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
                                    binding.rvIzinpimpinan.visibility = View.VISIBLE
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
                                            startActivity(
                                                intentFor<TambahGajiLemburActivity>(
                                                    "id_lembur" to note.id_lembur
                                                ).newTask()
                                            )
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