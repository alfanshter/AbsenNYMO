package com.skripsi.absen.pimpinan.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.skripsi.absen.MonthYearPickerDialog
import com.skripsi.absen.R
import com.skripsi.absen.databinding.FragmentGajiPokokBinding
import com.skripsi.absen.model.PegawaiModel
import com.skripsi.absen.utils.Constant
import kotlinx.android.synthetic.main.fragment_gaji_pokok.*
import org.jetbrains.anko.support.v4.toast
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class GajiPokokFragment : Fragment() {

    var tahun: Int? = null
    var bulan: Int? = null
    var namapegawai : String? = null
    lateinit var auth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore
    var userid : String? = null
    lateinit var binding: FragmentGajiPokokBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_gaji_pokok, container, false)
        binding.lifecycleOwner = this

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        userid = auth.currentUser!!.uid

        binding.edtBulan.setOnClickListener {
            tahunbulan()
        }

        binding.btnSimpan.setOnClickListener {
            tambahgaji()
        }


        return binding.root

    }

    private fun tambahgaji() {
        val gaji = binding.edtGaji.text.toString().trim()
        val key = FirebaseFirestore.getInstance().collection(Constant.gaji).document().id
        val date = SimpleDateFormat("dd-MM-yyyy").parse("01-${bulan.toString()}-${tahun.toString()}")
        val tanggal = Timestamp(date)
        if (gaji.isNotEmpty() && tahun!=null && bulan!=null){
            val usermap : HashMap<String,Any?> = HashMap()
            usermap["id_gaji"] = key
            usermap["gaji_karyawan"] = gaji.toInt()
            usermap["id_pimpinan"] = userid.toString()
            usermap["tanggal"] = tanggal
            val docref = firestore.collection(Constant.gaji).document(key).set(usermap).addOnCompleteListener {
                if (it.isSuccessful){
                    toast("gaji berhasil di input")
                    requireActivity().finish()
                }else{
                    toast("silahkan ulangi lagi")
                }
            }
        }else{
            toast("isi semua kolom")
        }
    }

    private fun tahunbulan() {
        MonthYearPickerDialog().apply {
            setListener { view, year, month, dayOfMonth ->
                tahun = year
                bulan = month + 1
                binding.edtBulan.text = "$tahun-$bulan"
            }
            show(this@GajiPokokFragment.parentFragmentManager, "MonthYearPickerDialog")
        }
    }



}