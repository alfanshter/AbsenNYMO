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
import com.skripsi.absen.model.LemburModel
import java.text.SimpleDateFormat
import java.util.*

class LemburViewHolder(
    private val notesList: MutableList<LemburModel>,
    private val context: Context,
    private val firestoreDB: FirebaseFirestore

) : RecyclerView.Adapter<LemburViewHolder.ViewHolder>() {

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
        internal var waktulembur: TextView
        internal var status: TextView
        internal var jenis: TextView


        init {

            tanggal = view.findViewById(R.id.baris1)
            waktulembur = view.findViewById(R.id.baris2)
            status = view.findViewById(R.id.baris3)
            jenis = view.findViewById(R.id.baris4)

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

        val tanggal = Date(note.tanggal_kerja!!.toDate().time)

        holder.tanggal.text = "Lembur : ${format.format(tanggal)}"
        holder.waktulembur.text = "Jam lembur: ${note.waktu_lembur.toString()} jam"
        holder.status.text = "status :${note.status.toString()}"
        holder.jenis.visibility = View.INVISIBLE
        holder.itemView.setOnClickListener {
            if(dialog != null){
                dialog!!.onClick(holder.layoutPosition)
            }
        }
    }

}