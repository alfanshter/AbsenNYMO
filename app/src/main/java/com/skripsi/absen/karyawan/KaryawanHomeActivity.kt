package com.skripsi.absen.karyawan

import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.appcompat.app.AppCompatActivity
import com.alfanshter.jatimpark.MainPresenter
import com.skripsi.absen.R
import com.skripsi.absen.karyawan.ui.home.LemburFragment
import kotlinx.android.synthetic.main.activity_karyawan_home.*
import kotlinx.android.synthetic.main.app_bar_karyawan_home.*

class KaryawanHomeActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var mainPresenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_karyawan_home)


        val drawerToggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        ){
            override fun onDrawerClosed(view: View){
                super.onDrawerClosed(view)
                //toast("Drawer closed")
            }

            override fun onDrawerOpened(drawerView: View){
                super.onDrawerOpened(drawerView)
                //toast("Drawer opened")
            }
        }

        drawerToggle.isDrawerIndicatorEnabled = true
        drawer_layout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
        nav_view.setNavigationItemSelectedListener{
            when (it.itemId) {
                R.id.nav_home -> {
                    mainPresenter.changeFragment(supportFragmentManager,
                        LemburFragment(),R.id.nav_host_fragment_content_karyawan_home)
                }
            }
            drawer_layout.closeDrawer(Gravity.RIGHT)
            true

        }
        mainPresenter = MainPresenter()
        mainPresenter.changeFragment(supportFragmentManager,
            LemburFragment(),R.id.nav_host_fragment_content_karyawan_home)



    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.karyawan_home, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_karyawan_home)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}