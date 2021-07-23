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
import java.text.SimpleDateFormat
import java.util.*

class AbsenViewHolder(
    private val notesList: MutableList<AbsenModel>,
    private val context: Context,
    private val firestoreDB: FirebaseFirestore

) : RecyclerView.Adapter<AbsenViewHolder.ViewHolder>() {

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
        internal var tanggal: TextView
        internal var masuk: TextView
        internal var pulang: TextView


        init {
            tanggal = view.findViewById(R.id.baris2)
            masuk = view.findViewById(R.id.baris3)
            pulang = view.findViewById(R.id.baris4)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_karyawan, parent, false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser!!.uid

        val note = notesList[position]
        val format = SimpleDateFormat("EEE, dd MMM yyyy")
        val format_jam = SimpleDateFormat("h:mm a")

        val tanggal = Date(note.tanggal_absen!!.toDate().time)


        holder.tanggal.text = "Tanggal : ${format.format(tanggal)}"
        holder.masuk.text = "Masuk : ${format_jam.format(tanggal)} "
        if (note.absen_pulang!=null){
            val tanggal_pulang = Date(note.absen_pulang!!.toDate().time)
            holder.pulang.text = "Pulang : ${format_jam.format(tanggal_pulang)} "
        }else{
            holder.pulang.text = "Pulang : Belum absen pulang"
        }
        holder.itemView.setOnClickListener {
            if(dialog != null){
                dialog!!.onClick(holder.layoutPosition)
            }
        }
    }

}