package com.skripsi.absen.karyawan.ui.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import java.util.*


class PagerAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {

     private var fragmentList= ArrayList<Fragment>()
     private var titleList = ArrayList<String>()

    override fun getItem(position: Int): Fragment {
        return when (position){
            0-> {
                AbsenFragment()
            }
            1->{
                LemburFragment()
            }

            2->{
                IzinFragment()
            }


            3->{
                LiburFragment()
            }

            4->{
                GajiFragment()
            }
            else->
                IzinFragment()

        }

    }

    override fun getCount(): Int {
        return 5
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position){
            0->"Absen"
            1->"Lembur"
            2->"Izin"
            3->"Libur"
            4->"Gaji"
            else ->"Absen"
        }
    }

    fun addFragment(
        fragment: Fragment?,
        title: String?
    ) {
        fragmentList.add(fragment!!)
        titleList.add(title!!)
    }

}