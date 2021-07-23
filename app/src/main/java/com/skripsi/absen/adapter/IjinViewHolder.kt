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
import com.skripsi.absen.model.IjinModel
import java.text.SimpleDateFormat
import java.util.*

class IjinViewHolder(
    private val notesList: MutableList<IjinModel>,
    private val context: Context,
    private val firestoreDB: FirebaseFirestore

) : RecyclerView.Adapter<IjinViewHolder.ViewHolder>() {

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
        internal var status: TextView
        internal var tanggal: TextView
        internal var alasan: TextView
        internal var tipe_ijin: TextView


        init {

            status = view.findViewById(R.id.baris1)
            tanggal = view.findViewById(R.id.baris2)
            alasan = view.findViewById(R.id.baris3)
            tipe_ijin = view.findViewById(R.id.baris4)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_ijin, parent, false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser!!.uid

        val note = notesList[position]
        val format = SimpleDateFormat("EEE, dd MMM yyyy")

        val tanggal = Date(note.tanggal_izin!!.toDate().time)

        holder.tanggal.text = "Tanggal izin : ${format.format(tanggal)}"
        holder.alasan.text = "Alasan :${note.alasan.toString()}"
        holder.tipe_ijin.text = "tipe ijin :${note.tipe_ijin.toString()}"

        if (note.status!=null){
            holder.status.text = "Status : ${note.status.toString()}"
        }else{
            holder.status.text = "Status : Belum di verifikasi"

        }
        holder.itemView.setOnClickListener {
            if(dialog != null){
                dialog!!.onClick(holder.layoutPosition)
            }
        }
    }

}