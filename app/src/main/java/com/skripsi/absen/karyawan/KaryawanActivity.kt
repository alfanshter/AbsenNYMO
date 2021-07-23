package com.skripsi.absen.karyawan

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.alfanshter.udinlelangfix.Session.SessionManager
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.skripsi.absen.R
import com.skripsi.absen.auth.LoginActivity
import com.skripsi.absen.karyawan.ui.home.AbsenFragment
import com.skripsi.absen.karyawan.ui.home.PagerAdapter
import com.skripsi.absen.model.AbsenModel
import com.skripsi.absen.model.PegawaiModel
import com.skripsi.absen.utils.Constant
import com.skripsi.absen.utils.CustomProgressDialog
import com.skripsi.absen.utils.Utilities
import kotlinx.android.synthetic.main.activity_karyawan.*
import kotlinx.android.synthetic.main.nav_header_karyawan_home.*
import org.jetbrains.anko.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor


class KaryawanActivity : AppCompatActivity(), AnkoLogger {
    //Fingerprint
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo


    lateinit var dialog: AlertDialog
    lateinit var firestore: FirebaseFirestore
    lateinit var auth: FirebaseAuth
    var userid: String? = null
    var tanggal_sekarang: Timestamp? = null
    var absen_pulang: Timestamp? = null
    var absen_masuk: Timestamp? = null
    lateinit var sessionManager: SessionManager

    //lokasi system
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    var progressDialog = CustomProgressDialog()

    companion object {
        var latitudePosisi: String? = null
        var longitudePosisi: String? = null

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_karyawan)
        progressDialog.show(this, getString(R.string.lokasi))
        progressDialog.dialog.setCanceledOnTouchOutside(false)
        sessionManager = SessionManager(this)
        auth = FirebaseAuth.getInstance()
        userid = auth.currentUser!!.uid
        firestore = FirebaseFirestore.getInstance()
        viewpager_main.adapter = PagerAdapter(supportFragmentManager)
        tabs_main.setupWithViewPager(viewpager_main)
        tanggal_sekarang = Timestamp(Date())
        checkMyLocationPermission()
        executor = ContextCompat.getMainExecutor(this@KaryawanActivity)
        val drawerToggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
            this,
            cl_utama,
            toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        ) {
            override fun onDrawerClosed(view: View) {
                super.onDrawerClosed(view)
                //toast("Drawer closed")
            }

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                //toast("Drawer opened")
            }
        }

        val drawerview = findViewById<NavigationView>(R.id.nav_view)
        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        val headerView = navigationView.getHeaderView(0)
        val nama = headerView.findViewById<View>(R.id.txt_namakaryawan) as TextView
        val notelp = headerView.findViewById<View>(R.id.no_telp) as TextView
        val alamat = headerView.findViewById<View>(R.id.alamat) as TextView
        val headerview = drawerview.getHeaderView(0)

        val docref = firestore.collection(Constant.akunpegawai).document(userid.toString()).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val data = it.result!!.toObject(PegawaiModel::class.java)
                    nama.text = data!!.nama.toString()
                    notelp.text = data.no_hp.toString()
                    alamat.text = data.alamat.toString()

                }
            }
        val buttonlogout = headerview.find<MaterialButton>(R.id.btn_logout)
        buttonlogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            sessionManager.setLogin(false)
            startActivity<LoginActivity>()
            finish()
        }

        drawerToggle.isDrawerIndicatorEnabled = true
        cl_utama.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        checkfinger()
        setPrompt()
        getbuttonabsen()

        nav_view.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.btn_logout -> {
                    toast("halo")
                }
            }
            true
        }

        btn_absenpulang.setOnClickListener {
            setabsenpulang()
        }

        btn_sudahabsen.setOnClickListener {
            toast("Absen lagi besok")
        }

    }

    fun absen() {

        val builder = AlertDialog.Builder(this@KaryawanActivity)
        builder.setTitle("Apakah anda ingin absen sekarang ? ")
        val dialogClickListener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    val datenow = Date(tanggal_sekarang!!.toDate().time)
                    val tanggal = SimpleDateFormat("dd/MM/yy")
                    val waktu = SimpleDateFormat("hh:mm a")

                    val tanggal_sekarang = tanggal.format(datenow)
                    val waktu_sekarang = waktu.format(datenow)


                    val ambildata =
                        firestore.collection(Constant.absen).whereEqualTo("uid_pegawai", userid)
                            .orderBy("tanggal_absen", Query.Direction.DESCENDING).get()
                            .addOnSuccessListener {
                                if (it.isEmpty) {
                                    setabsen()
                                } else {
                                    val data = it.documents[0]["tanggal_absen"] as Timestamp
                                    val data_absen = data.toDate().time
                                    val format_absen = SimpleDateFormat("dd/MM/yy")
                                    val tanggal_absen = format_absen.format(data_absen)
                                    if (tanggal_absen == tanggal_sekarang) {
                                        toast("anda sudah absen")
                                    } else {
                                        setabsen()
                                    }

                                }
                            }


                }
                DialogInterface.BUTTON_NEGATIVE -> {
                }
                DialogInterface.BUTTON_NEUTRAL -> {
                }
            }
        }
        // Set the alert dialog positive/yes button
        builder.setPositiveButton("YES", dialogClickListener)

        // Set the alert dialog negative/no button
        builder.setNegativeButton("NO", dialogClickListener)

        // Set the alert dialog neutral/cancel button
        builder.setNeutralButton("CANCEL", dialogClickListener)


        // Initialize the AlertDialog using builder object
        dialog = builder.create()

        // Finally, display the alert dialog
        dialog.show()
    }

    fun setabsen() {
        val key =
            FirebaseFirestore.getInstance().collection(Constant.absen).document().id
        val mylist = AbsenModel()

        mylist.uid_pegawai = userid.toString()
        mylist.tanggal_absen = Timestamp(Date())
        mylist.id_absen = key
        var docref = firestore.collection(Constant.absen).document(key).set(mylist)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    btn_absen.visibility = View.GONE

                    btn_absenpulang.visibility = View.VISIBLE
                    AbsenFragment.notesList.add(mylist)
                    if (AbsenFragment.mAdapter != null) {
                        AbsenFragment.mAdapter!!.notifyDataSetChanged()
                    }


                    toast("Absen berhasil")


                } else {
                    toast("absen gagal")
                }
            }
    }

    fun getbuttonabsen() {
        val datenow = Date(tanggal_sekarang!!.toDate().time)
        val tanggal = SimpleDateFormat("dd/MM/yy")
        val waktu = SimpleDateFormat("hh:mm a")

        val tanggal_sekarang = tanggal.format(datenow)
        val waktu_sekarang = waktu.format(datenow)



        val ambildata = firestore.collection(Constant.absen).whereEqualTo("uid_pegawai", userid)
            .orderBy("tanggal_absen", Query.Direction.DESCENDING).get()
            .addOnSuccessListener {
                if (it.isEmpty) {
                } else {
                    absen_masuk = it.documents[0]["tanggal_absen"] as Timestamp
                    val id_absen = it.documents[0]["id_absen"] as String
                    absen_pulang = it.documents[0]["absen_pulang"] as Timestamp?

                    val data_absen = absen_masuk!!.toDate().time
                    val format_absen = SimpleDateFormat("dd/MM/yy")
                    val format_jam = SimpleDateFormat("hh:mm a")
                    val tanggal_absen = format_absen.format(data_absen)
                    val jam_absen = format_jam.format(data_absen)

                    txt_sudahabsen.text = "Anda sudah absen masuk pukul $jam_absen"
                    info { "dinda $absen_pulang" }
                    if (tanggal_absen == tanggal_sekarang) {
                        btn_absen.visibility = View.GONE
                        btn_absenpulang.visibility = View.VISIBLE

                    }
                    if (tanggal_absen == tanggal_sekarang && absen_pulang != null) {
                        btn_absenpulang.visibility = View.GONE
                        btn_sudahabsen.visibility = View.VISIBLE
                        toast("sudah absen pulang")
                    }

                }
            }
    }

    fun setabsenpulang() {
        val datenow = Date(tanggal_sekarang!!.toDate().time)
        val tanggal = SimpleDateFormat("dd/MM/yy")
        val waktu = SimpleDateFormat("hh:mm a")

        val tanggal_sekarang = tanggal.format(datenow)
        val waktu_sekarang = waktu.format(datenow)

        val ambildata = firestore.collection(Constant.absen).whereEqualTo("uid_pegawai", userid)
            .orderBy("tanggal_absen", Query.Direction.DESCENDING).get()
            .addOnSuccessListener {
                if (it.isEmpty) {
                } else {
                    val data = it.documents[0]["tanggal_absen"] as Timestamp
                    val id_absen = it.documents[0]["id_absen"] as String
                    absen_pulang = it.documents[0]["absen_pulang"] as Timestamp?
                    val data_absen = data.toDate().time
                    val format_absen = SimpleDateFormat("dd/MM/yy")
                    val tanggal_absen = format_absen.format(data_absen)

                    if (tanggal_absen == tanggal_sekarang) {
                        btn_absen.visibility = View.GONE
                        btn_absenpulang.visibility = View.VISIBLE
                        val docref = firestore.collection(Constant.absen).document(id_absen).update(
                            "absen_pulang", Timestamp(
                                Date()
                            )
                        ).addOnCompleteListener {
                            if (it.isSuccessful) {
                                btn_absenpulang.visibility = View.GONE
                                btn_sudahabsen.visibility = View.VISIBLE
                                toast("Absen pulang berhasil")
                            } else {
                                toast("absen gagal")
                            }
                        }


                    } else if (tanggal_absen == tanggal_sekarang && absen_pulang != null) {
                        btn_absenpulang.visibility = View.GONE
                        btn_sudahabsen.visibility = View.VISIBLE
                        toast("sudah absen pulang")
                    }

                }
            }
    }

    private fun checkMyLocationPermission() {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    when {
                        report!!.areAllPermissionsGranted() -> {
                            init()
                        }
                        report.isAnyPermissionPermanentlyDenied -> {
                            Log.d("PERMISSIONCHECK", "ANY PERMISSION PERMANENTLY DENIED")

                        }
                        else -> {
                            checkMyLocationPermission()
                            Log.d("PERMISSIONCHECK", "ANY PERMISSION DENIED")
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    checkMyLocationPermission()
                    Log.d("PERMISSIONCHECK", "onPermissionRationaleShouldBeShown")
                    token?.continuePermissionRequest()
                }

            })
            .check()
    }

    private fun init() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.fastestInterval = 3000
        locationRequest.interval = 5000
        locationRequest.smallestDisplacement = 10f

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                try {
                    val newPos = LatLng(
                        locationResult!!.lastLocation.latitude,
                        locationResult.lastLocation.longitude
                    )
                    latitudePosisi = locationResult.lastLocation.latitude.toString()
                    longitudePosisi = locationResult.lastLocation.longitude.toString()
                    if (latitudePosisi != null || longitudePosisi != null) {
                        progressDialog.dialog.dismiss()
                        val latitude = -7.478791
                        val longitude = 112.430771

                        val loc1 = Location("")
                        loc1.latitude = latitude
                        loc1.longitude = longitude
                        val loc2 = Location("")
                        loc2.latitude = latitudePosisi!!.toDouble()
                        loc2.longitude = longitudePosisi!!.toDouble()
                        val distanceInMeters = loc1.distanceTo(loc2)
                        if (distanceInMeters < 50f) {
                            txt_lokasi.text = "Lokasi : Area nymo bakery"
                        } else {
                            txt_lokasi.text = "Lokasi : diluar area nymo bakery"
                        }

                    }


                } catch (e: Exception) {

                }

            }

        }

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        mFusedLocationProviderClient.requestLocationUpdates(
            locationRequest, locationCallback,
            Looper.myLooper()
        )

    }

    fun checkfinger() {
        if (Utilities.isBiometricHardWareAvailable(this)) {
            initBiometricPrompt(
                Constant.BIOMETRIC_AUTHENTICATION,
                Constant.BIOMETRIC_AUTHENTICATION_SUBTITLE,
                Constant.BIOMETRIC_AUTHENTICATION_DESCRIPTION,
                false
            )
        } else {
            if (Utilities.deviceHasPasswordPinLock(this)) {

                initBiometricPrompt(
                    Constant.PASSWORD_PIN_AUTHENTICATION,
                    Constant.PASSWORD_PIN_AUTHENTICATION_SUBTITLE,
                    Constant.PASSWORD_PIN_AUTHENTICATION_DESCRIPTION,
                    true
                )
            }
        }
    }

    private fun setPrompt() {
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    toast("fingerprint error")

                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    absen()

                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    toast("fingerprint tidak terdaftar")

                }
            })
    }

    private fun initBiometricPrompt(
        title: String,
        subtitle: String,
        description: String,
        setDeviceCred: Boolean
    ) {
        if (setDeviceCred) {
            promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setDescription(description)
                .setDeviceCredentialAllowed(true)
                .build()
        } else {
            promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setDescription(description)
                .setNegativeButtonText(Constant.cancel)
                .build()
        }

        btn_absen.setOnClickListener {

            val latitude = -7.478791
            val longitude = 112.430771

            val loc1 = Location("")
            loc1.latitude = latitude
            loc1.longitude = longitude
            val loc2 = Location("")
            loc2.latitude = latitudePosisi!!.toDouble()
            loc2.longitude = longitudePosisi!!.toDouble()
            val distanceInMeters = loc1.distanceTo(loc2)

            info { "dinda $latitudePosisi $longitudePosisi $distanceInMeters" }
            if (distanceInMeters < 50f) {
                biometricPrompt.authenticate(promptInfo)
            } else {
                toast("anda tidak berada pada lokasi")
            }
        }
    }


}