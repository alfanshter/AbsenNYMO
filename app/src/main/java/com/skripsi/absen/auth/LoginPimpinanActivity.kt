package com.skripsi.absen.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alfanshter.udinlelangfix.Session.SessionManager
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.skripsi.absen.R
import com.skripsi.absen.pimpinan.PimpinanActivity
import com.skripsi.absen.utils.Constant
import com.skripsi.absen.utils.CustomProgressDialog
import kotlinx.android.synthetic.main.activity_login_pimpinan.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.toast

class LoginPimpinanActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore
    lateinit var sessionManager: SessionManager
    private var progressdialog = CustomProgressDialog()
    val RC_SIGN_IN: Int = 1
    lateinit var mGoogleSignInOptions: GoogleSignInOptions

    //token
    var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_pimpinan)


        sessionManager = SessionManager(this)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        btn_login.setOnClickListener {
            login_username()
        }


    }


    fun login_username() {
        progressdialog.show(this,getString(R.string.loading))
        var password = pass.text.toString().trim()
        var username = email.text.toString().trim()
        if (password.isNotEmpty()  && username.isNotEmpty()) {

            val docref = firestore.collection(Constant.pimpinan).whereEqualTo("username", username)
                .whereEqualTo("password", password)
            docref.get().addOnSuccessListener { document ->
                for (doc in document) {
                    if (doc.exists()) {
                        auth.signInWithEmailAndPassword(username, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    gettoken()
                                } else {
                                    progressdialog.dialog.dismiss()
                                    toast(task.exception.toString())

                                }
                            }
                    }else{
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
                    firestore.collection(Constant.pimpinan).document(auth.currentUser!!.uid)
                        .update("token_id", token.toString()).addOnCompleteListener {
                            if (it.isSuccessful) {
                                progressdialog.dialog.dismiss()
                                startActivity(intentFor<PimpinanActivity>().clearTask().newTask())
                                sessionManager.setLoginpimpinan(true)
                                finish()
                            }
                        }

            } else {
                return@OnCompleteListener
            }

        })
    }

}