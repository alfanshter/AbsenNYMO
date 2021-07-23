package com.skripsi.absen.admin

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import com.alfanshter.udinlelangfix.Session.SessionManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.skripsi.absen.R
import com.skripsi.absen.utils.CustomProgressDialog
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.*
import java.util.*
import kotlin.collections.HashMap

class RegisterActivity : AppCompatActivity(), AnkoLogger {
    lateinit var auth: FirebaseAuth
    private var firestore: FirebaseFirestore? = null

    //loading
    private var progressDialog = CustomProgressDialog()

    //token
    var token: String? = null
    var tipe_akun: String? = null

    //save data ke sharedpreference
    lateinit var sessionManager: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        sessionManager = SessionManager(this)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        btn_daftar.setOnClickListener {
            daftar()
        }

    }

    fun random_password(): String {
        val charPool: List<Char> = ('A'..'Z') + ('0'..'5')
//        val outputStrLength = (0..5).shuffled().first()
        val outputStrLength = (7)

        return (1..outputStrLength)
            .map { kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }

    private fun daftar() {
        progressDialog.show(this, getString(R.string.loading))

        val password = random_password().trim()
        val nama = edt_nama.text.toString().trim()
        val alamat = edt_alamat.text.toString().trim()
        val no_hp = id_nohp.text.toString().trim()

        val substring = alamat.subSequence(0, 2)
        val random = (0..3).random()

        var namaemail = nama.replace("\\s".toRegex(), "")
        val emailkaryawan = "$namaemail$substring$random@pegawai.com"
        val emailadmin = "$namaemail$substring$random@admin.com"
        val emailpimpinan = "$namaemail$substring$random@pimpinan.com"

        if (nama.isNotEmpty() && alamat.isNotEmpty() && no_hp.isNotEmpty() && tipe_akun != null) {
            if (tipe_akun.equals("pimpinan")) {
                daftar_akun(emailpimpinan.trim(), nama, alamat, no_hp,password)
            } else if (tipe_akun.equals("admin")) {
                daftar_akun(emailadmin.trim(), nama, alamat, no_hp,password)

            } else {
                daftar_akun(emailkaryawan.trim(), nama, alamat, no_hp,password)

            }
        }


    }

    private fun daftar_akun(email: String, nama: String, alamat: String, no_hp: String,password : String) {
        info { "dinda $password" }
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user_id = auth.currentUser!!.uid
                    FirebaseMessaging.getInstance().token.addOnCompleteListener(
                        OnCompleteListener { task ->
                            if (!task.isSuccessful) {
                                return@OnCompleteListener
                            }
                            // Get new FCM registration token
                            token = task.result
                            if (token != null) {
                                val userMap: MutableMap<String, Any?> =
                                    HashMap()
                                userMap["nama"] = nama
                                userMap["username"] = email
                                userMap["token_id"] = token
                                userMap["no_hp"] = no_hp
                                userMap["alamat"] = alamat
                                userMap["uid"] = user_id
                                userMap["uid"] = user_id
                                userMap["tipe_akun"] = tipe_akun.toString()
                                userMap["password"] = password.toString()
                                userMap["tanggal_daftar"] = Timestamp(Date())
                                info { "alfan $password" }
                                val req =
                                    firestore!!.collection(tipe_akun.toString())
                                        .document(auth.currentUser!!.uid)
                                        .set(userMap).addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                progressDialog.dialog.dismiss()
                                                startActivity<DetailRegisterActivity>("username" to email,
                                                "password" to password)
                                                finish()
                                            }
                                        }
                            }

                        })


                } else {
                    info { "dinda ${task.exception.toString()}" }
                    progressDialog.dialog.dismiss()
                    toast(task.exception.toString())
                }
            }


    }


    fun radioregister(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.radio_admin ->
                    if (checked) {
                        tipe_akun = "pimpinan"
                    }
                R.id.radio_staf ->
                    if (checked) {
                        tipe_akun = "admin"
                    }

                R.id.radio_karyawan ->
                    if (checked) {
                        tipe_akun = "pegawai"

                    }

            }
        }
    }
}