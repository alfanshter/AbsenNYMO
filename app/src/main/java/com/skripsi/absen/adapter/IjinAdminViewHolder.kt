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
import com.skripsi.absen.model.IjinModel
import com.skripsi.absen.model.LiburModel
import com.skripsi.absen.model.PegawaiModel
import com.skripsi.absen.utils.Constant
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.text.SimpleDateFormat
import java.util.*

class IjinAdminViewHolder(
    private val notesList: MutableList<IjinModel>,
    private val context: Context,
    private val firestoreDB: FirebaseFirestore

) : RecyclerView.Adapter<IjinAdminViewHolder.ViewHolder>() ,AnkoLogger{

    //database
    lateinit var firestore: FirebaseFirestore
    lateinit var auth: FirebaseAuth
    var userId: String? = null
    private var dialog: Dialog? = null


    interface Dialog {
        fun onTerima(position: Int, id_libur : String,terima : ImageButton,tolak : ImageButton)
        fun onTolak(position: Int, id_libur : String,terima : ImageButton,tolak : ImageButton)
    }

    fun setDialog(dialog: Dialog) {
        this.dialog = dialog
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        internal var nama: TextView
        internal var tanggal: TextView
        internal var tipeijin: TextView
        internal var alasan: TextView
        internal var terima: ImageButton
        internal var tolak: ImageButton


        init {

            nama = view.findViewById(R.id.baris1)
            tanggal = view.findViewById(R.id.baris2)
            tipeijin = view.findViewById(R.id.baris4)
            alasan = view.findViewById(R.id.baris3)
            terima = view.findViewById(R.id.btn_terima)
            tolak = view.findViewById(R.id.btn_tolak)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listadmin_libur, parent, false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = notesList[position]

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser!!.uid
        firestore.collection(Constant.akunpegawai).document(note.uid_pegawai.toString()).get().addOnCompleteListener {
            if (it.isSuccessful){

                val data = it.result!!.toObject(PegawaiModel::class.java)
                holder.nama.text = "${context.getString(R.string.nama)} : ${data!!.nama.toString()}"

                val format = SimpleDateFormat("EEE, dd MMM yyyy")

                val tanggal = Date(note.tanggal_izin!!.toDate().time)

//        holder.mulai_libur.text = "Mulai libur : ${format.format(tanggal)}"
                holder.tanggal.text = "Tanggal :${format.format(tanggal)}"
                holder.tipeijin.text = "Tipe Ijin :${note.tipe_ijin.toString()}"
                holder.alasan.text = "Alasan : ${note.alasan.toString()}"

                if (note.status == Constant.diterima){
                    holder.terima.setBackgroundResource(R.drawable.bt_terimadone)
                    holder.tolak.setBackgroundResource(R.drawable.bt_tolak)

                }
                if (note.status.equals(Constant.ditolak)){
                    holder.tolak.setBackgroundResource(R.drawable.bt_tolakdone)
                    holder.terima.setBackgroundResource(R.drawable.bt_terima)
                }


                holder.terima.setOnClickListener {
                    if(dialog != null){
                        dialog!!.onTerima(holder.layoutPosition,note.id_izin.toString(),holder.terima,holder.tolak)
                    }

                }

                holder.tolak.setOnClickListener {
                    if(dialog != null){
                        dialog!!.onTolak(holder.layoutPosition,note.id_izin.toString(),holder.terima,holder.tolak)
                    }

                }


            }
        }
    }

}