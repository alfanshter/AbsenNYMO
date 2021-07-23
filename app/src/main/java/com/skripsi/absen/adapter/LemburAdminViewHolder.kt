package com.skripsi.absen.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.skripsi.absen.R
import com.skripsi.absen.model.LiburModel
import com.skripsi.absen.model.LemburModel
import com.skripsi.absen.utils.Constant
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.text.SimpleDateFormat
import java.util.*

class LemburAdminViewHolder(
    private val notesList: MutableList<LemburModel>,
    private val context: Context,
    private val firestoreDB: FirebaseFirestore

) : RecyclerView.Adapter<LemburAdminViewHolder.ViewHolder>() ,AnkoLogger{

    //database
    lateinit var firestore: FirebaseFirestore
    lateinit var auth: FirebaseAuth
    var userId: String? = null
    private var dialog: Dialog? = null


    interface Dialog {
        fun onTerima(position: Int, id_libur : String)
    }

    fun setDialog(dialog: Dialog) {
        this.dialog = dialog
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        internal var tanggal: TextView
        internal var waktu: TextView
        internal var status: TextView


        init {

            tanggal = view.findViewById(R.id.baris1)
            waktu = view.findViewById(R.id.baris2)
            status= view.findViewById(R.id.baris3)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_lembur, parent, false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = notesList[position]

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser!!.uid

        val format = SimpleDateFormat("EEE, dd MMM yyyy")

        val tanggal = Date(note.tanggal_kerja!!.toDate().time)

        holder.tanggal.text = "Tanggal : ${format.format(tanggal)}"
        holder.waktu.text = "Jam lembur : ${ note.waktu_lembur.toString()} jam"
        holder.status.text = note.status.toString()

    }

}