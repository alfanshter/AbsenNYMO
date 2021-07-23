package com.skripsi.absen.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alfanshter.udinlelangfix.Session.SessionManager
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.skripsi.absen.R
import com.skripsi.absen.admin.AdminActivity
import com.skripsi.absen.utils.Constant
import com.skripsi.absen.utils.CustomProgressDialog
import kotlinx.android.synthetic.main.activity_login_admin.*
import org.jetbrains.anko.*

class LoginAdminActivity : AppCompatActivity(), AnkoLogger {
    //database
    private lateinit var auth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore
    lateinit var sessionManager: SessionManager
    private var progressdialog = CustomProgressDialog()
    val RC_SIGN_IN: Int = 1
    lateinit var mGoogleSignInOptions: GoogleSignInOptions

    //token
    var token: String? = null
//    keterangan tipe akun
//    customer = 1
//    warung =0

    var tipe_akun = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_admin)

        sessionManager = SessionManager(this)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        btn_login.setOnClickListener {
            login_username()
        }
    }

    fun login_username() {
        progressdialog.show(this, getString(R.string.loading))
        var password = pass.text.toString().trim()
        var email = email.text.toString().trim()
        if (password.isNotEmpty() && email.isNotEmpty()) {


            val docref = firestore.collection(Constant.akunadmin).whereEqualTo("username", email)
                .whereEqualTo("password", password)
            docref.get().addOnSuccessListener { document ->
                for (doc in document) {
                    if (doc.exists()) {
                        info { "dinda ok" }

                        auth.signInWithEmailAndPassword(Constant.emailadmin, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    gettoken()
                                } else {
                                    progressdialog.dialog.dismiss()
                                    toast("gagal login")

                                }
                            }
                    } else {
                        progressdialog.dialog.dismiss()
                        toast("Password Salah")

                    }

                }

                if (document.isEmpty) {
                    progressdialog.dialog.dismiss()
                    toast("Password Salah")

                }
            }


/*            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        gettoken()
                    } else {
                        progressdialog.dismiss()
                        toast("gagal login")
                    }
                }*/


        } else {
            progressdialog.dialog.dismiss()
            toast("Masukkan semua isi kolom")
        }
    }

    private fun gettoken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            // Get new FCM registration token
            token = task.result
            if (token != null) {
                val req =
                    firestore.collection(Constant.akunadmin).document(auth.currentUser!!.uid)
                        .update("token_id", token.toString()).addOnCompleteListener {
                            if (it.isSuccessful) {
                                sessionManager.setLoginadmin(true)
                                startActivity(intentFor<AdminActivity>().clearTask().newTask())
                                progressdialog.dialog.dismiss()
                                finish()
                            }
                        }

            } else {
                return@OnCompleteListener
            }

        })
    }

}