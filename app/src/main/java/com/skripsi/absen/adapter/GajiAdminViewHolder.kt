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
import com.skripsi.absen.model.AkunAdminModel
import com.skripsi.absen.model.GajiAdminModel
import com.skripsi.absen.model.LiburModel
import com.skripsi.absen.utils.Constant
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.text.SimpleDateFormat
import java.util.*

class GajiAdminViewHolder(
    private val notesList: MutableList<GajiAdminModel>,
    private val context: Context,
    private val firestoreDB: FirebaseFirestore

) : RecyclerView.Adapter<GajiAdminViewHolder.ViewHolder>(), AnkoLogger {

    //database
    lateinit var firestore: FirebaseFirestore
    lateinit var auth: FirebaseAuth
    var userId: String? = null
    private var dialog: Dialog? = null


    interface Dialog {
        fun onTerima(position: Int, id_libur: String)
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
        internal var gaji: TextView


        init {

            nama = view.findViewById(R.id.baris1)
            tanggal = view.findViewById(R.id.baris2)
            gaji = view.findViewById(R.id.baris3)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.lista_gaji_admin, parent, false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = notesList[position]

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser!!.uid
        firestore.collection(Constant.akunadmin).document(userId.toString()).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    try {
                        val data = it.result!!.toObject(AkunAdminModel::class.java)
                        if (data!=null){
                            holder.nama.text =
                                "${context.getString(R.string.nama)} : ${data!!.username.toString()}"
                        }else{
                            holder.nama.text =
                                "${context.getString(R.string.nama)} : admin@admin.com"

                        }

                    }catch (e :Exception){

                    }
                    val format = SimpleDateFormat("EEE, dd MMM yyyy")
                    val tanggal = Date(note.tanggal!!.toDate().time)
                    holder.tanggal.text = "Tanggal : ${format.format(tanggal)}"
                    holder.gaji.text = "Gaji ${note.gaji_karyawan.toString()}"


                }
            }
    }

}