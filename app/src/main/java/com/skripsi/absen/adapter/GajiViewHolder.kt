package com.skripsi.absen.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.skripsi.absen.R
import com.skripsi.absen.model.GajiAdminModel
import com.skripsi.absen.model.LemburModel
import com.skripsi.absen.utils.Constant
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class GajiViewHolder(
    private val notesList: MutableList<GajiAdminModel>,
    private val context: Context,
    private val firestoreDB: FirebaseFirestore

) : RecyclerView.Adapter<GajiViewHolder.ViewHolder>(), AnkoLogger {

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
        internal var bulan: TextView
        internal var gaji: TextView
        internal var bonus: TextView
        internal var jumlah: TextView


        init {

            bulan = view.findViewById(R.id.baris1)
            gaji = view.findViewById(R.id.baris2)
            bonus = view.findViewById(R.id.baris3)
            jumlah = view.findViewById(R.id.baris4)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_gaji, parent, false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser!!.uid

        val note = notesList[position]
        val format = SimpleDateFormat("MMM yyyy")
        val date = SimpleDateFormat("dd-MM-yyyy").parse("1-07-2021")
        val dateakhir = SimpleDateFormat("dd-MM-yyyy").parse("30-07-2021")
        val tanggal = Timestamp(date)
        val tanggalakhir = Timestamp(dateakhir)
        info { "dinda $tanggal" }

        val bulan = Date(note.tanggal!!.toDate().time)
        val bulanlembur = Timestamp(bulan)
        val docref =
            firestore.collection(Constant.lembur).whereGreaterThan("tanggal_kerja", tanggal)
                .whereLessThan("tanggal_kerja", tanggalakhir).get().addOnSuccessListener {
                if (it.isEmpty) {
                    info { "dinda halo" }
                } else {
                    var sum = 0
                    for (ref in it.documents) {
                        info { "dinda ok" }

                        val data = ref.toObject(LemburModel::class.java)
                         var bonus = data!!.bonus
                        sum += bonus!!


                    }
                    val jumlah = sum + note.gaji_karyawan!!
                    val dec = DecimalFormat("#,###.##")
                    val jumlahgaji = dec.format(jumlah)
                    holder.bonus.text = "Gaji Lembur :${sum.toString()}"
                    holder.jumlah.text = "Total Rp. $jumlahgaji "

                }
            }

        holder.bulan.text = "Bulan : ${format.format(bulan)}"
        holder.gaji.text = "Gaji: Rp. ${note.gaji_karyawan.toString()}"

        holder.itemView.setOnClickListener {
            if (dialog != null) {
                dialog!!.onClick(holder.layoutPosition)
            }
        }
    }

}