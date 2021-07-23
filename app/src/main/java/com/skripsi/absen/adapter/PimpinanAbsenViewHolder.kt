package com.skripsi.absen.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.skripsi.absen.R
import com.skripsi.absen.model.AbsenModel
import com.skripsi.absen.model.PegawaiModel
import com.skripsi.absen.utils.Constant
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.text.SimpleDateFormat
import java.util.*

class PimpinanAbsenViewHolder(
    private val notesList: MutableList<AbsenModel>,
    private val context: Context,
    private val firestoreDB: FirebaseFirestore

) : RecyclerView.Adapter<PimpinanAbsenViewHolder.ViewHolder>(),AnkoLogger {

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
        internal var nama: TextView
        internal var masuk: TextView
        internal var pulang: TextView


        init {

            hari = view.findViewById(R.id.hari)
            tahun = view.findViewById(R.id.tahunbulan)
            tanggal = view.findViewById(R.id.tanggal)


            nama = view.findViewById(R.id.baris1)
            masuk = view.findViewById(R.id.baris3)
            pulang = view.findViewById(R.id.baris4)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_pimpinangaji, parent, false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser!!.uid
//
        val note = notesList[position]
        info { "dinda ${note.uid_pegawai.toString()}" }
        val ref = firestore.collection(Constant.akunpegawai).document(note.uid_pegawai.toString()).get().addOnCompleteListener {
            if (it.isSuccessful){

                try {
                    val data = it.result!!.toObject(PegawaiModel::class.java)
                    val hariformat = SimpleDateFormat("EEEE")
                    val tanggalformat = SimpleDateFormat("dd")
                    val tahunformat = SimpleDateFormat("MMM yyyy")

                    val format = SimpleDateFormat("EEE, dd MMM yyyy")
                    val format_jam = SimpleDateFormat("h:mm a")
//
                    val hari = Date(note.tanggal_absen!!.toDate().time)
                    val  tanggal = Date(note.tanggal_absen!!.toDate().time)
                    val  tahun = Date(note.tanggal_absen!!.toDate().time)

                    val  masuk = Date(note.tanggal_absen!!.toDate().time)


//
//
                    holder.hari.text = "    ${hariformat.format(hari)}"
                    holder.tanggal.text = "    ${tanggalformat.format(tanggal)}"
                    holder.tahun.text = "    ${tahunformat.format(tahun)}"

                holder.nama.text = "Nama : ${data!!.nama.toString()}"
                    holder.masuk.text = "Masuk : ${format.format(masuk)}"
                    if (note.absen_pulang!=null){
                        val  pulang = Date(note.absen_pulang!!.toDate().time)
                        holder.pulang.text = "Pulang : ${format.format(pulang)}"
                    }else{
                        holder.pulang.text = "Pulang : -"

                    }

                }catch (e :Exception){
                    return@addOnCompleteListener
                }

            }
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