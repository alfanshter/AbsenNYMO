package com.skripsi.absen.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.skripsi.absen.R
import com.skripsi.absen.model.LemburModel
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.text.SimpleDateFormat
import java.util.*

class PimpinanLemburViewHolder(
    private val notesList: MutableList<LemburModel>,
    private val context: Context,
    private val firestoreDB: FirebaseFirestore

) : RecyclerView.Adapter<PimpinanLemburViewHolder.ViewHolder>(), AnkoLogger {

    //database
    lateinit var firestore: FirebaseFirestore
    lateinit var auth: FirebaseAuth
    var userId: String? = null
    private var dialog: Dialog? = null


    interface Dialog {
        fun onClick(position: Int)
    }

    fun setDialog(dialog: Dialog) {
        this.dialog = dialog
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        internal var hari: TextView
        internal var tanggal: TextView
        internal var tahun: TextView

        internal var waktu: TextView
        internal var kalender: TextView
        internal var status: TextView


        init {

            hari = view.findViewById(R.id.hari)
            tahun = view.findViewById(R.id.tahunbulan)
            tanggal = view.findViewById(R.id.tanggal)


            kalender = view.findViewById(R.id.baris1)
            waktu = view.findViewById(R.id.baris2)
            status = view.findViewById(R.id.baris3)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_pimpinanlibur, parent, false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser!!.uid
//
        try {
            val note = notesList[position]

            val hariformat = SimpleDateFormat("EEEE")
            val tanggalformat = SimpleDateFormat("dd")
            val tahunformat = SimpleDateFormat("MMM yyyy")

            val format = SimpleDateFormat("EEE, dd MMM yyyy")
            val format_jam = SimpleDateFormat("h:mm a")
//
            val hari = Date(note.tanggal_kerja!!.toDate().time)
            val tanggal = Date(note.tanggal_kerja!!.toDate().time)
            val tahun = Date(note.tanggal_kerja!!.toDate().time)

            val masuk = Date(note.tanggal_kerja!!.toDate().time)


//
//
            holder.hari.text = "    ${hariformat.format(hari)}"
            holder.tanggal.text = "    ${tanggalformat.format(tanggal)}"
            holder.tahun.text = "    ${tahunformat.format(tahun)}"

            holder.kalender.text = "Tanggal : ${format.format(masuk)}"
            holder.waktu.text = "Waktu : ${note.waktu_lembur.toString()} jam"
            holder.status.text = "Status : ${note.status.toString()}"

            holder.itemView.setOnClickListener {
                if (dialog!=null){
                    dialog!!.onClick(holder.layoutPosition)
                }
            }

        } catch (e: Exception) {
            info { "dinda ${e.message}" }
        }


//        holder.tanggal.text = "Tanggal : ${format.format(tanggal)}"
//        holder.masuk.text = "Masuk : ${format_jam.format(tanggal)} "
//        holder.status.text = "Status : ${note.status.toString()}"
//        if (note.absen_pulang!=null){
//            val tanggal_pulang = Date(note.absen_pulang!!.toDate().time)
//            holder.pulang.text = "Pulang : ${format.format(tanggal_pulang)} "
//        }else{
//            holder.pulang.text = "Pulang : Belum absen pulang"
//        }
//        holder.itemView.setOnClickListener {
//            if(dialog != null){
//                dialog!!.onClick(holder.layoutPosition)
//            }
//        }
    }

}