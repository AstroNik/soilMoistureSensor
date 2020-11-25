package com.example.soilmoisturesensor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_device_setup.*
/**
 * DeviceSetup activity which is basically the DeviceSetup Page of the Application
 * @author Manpreet Sandhu
 */
class DeviceSetup : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_setup)


        backButton.setOnClickListener{
            finish()
        }
        
        val pageAdapter = SimpleFragmentPagerAdapter(
            supportFragmentManager)
        // Add 4 new Fragments to the list
        pageAdapter.addFragment(SimpleFragment.newInstance("Ensure that you have created an account",1),"  1  ")
        pageAdapter.addFragment(SimpleFragment.newInstance("Place the battery inside the device\n " +
                "\n a. Make sure it is an 18650 Battery \n" +
                "\n b. Make sure the battery is correlation with the positive and negative sides of the device",2),"  2  ")
        pageAdapter.addFragment(SimpleFragment.newInstance("Place the device in the soil of the plant you want to be able to retrieve the data from",3),"  3  ")
        pageAdapter.addFragment(SimpleFragment.newInstance("Turn the device on with the switch on the top left of the device itself",4),"  4  ")
        pageAdapter.addFragment(SimpleFragment.newInstance("Go to your network settings, find and connect to the network ‘ECOders Sensor",5),"  5  ")
        pageAdapter.addFragment(SimpleFragment.newInstance("Once connected it will direct you to a website where you will place your WiFi credentials and device data"+
                "\n\n Once connected to ‘ECOders Sensor’ this page will be displayed. Select the ‘Configure WiFi’ button",6),"  6  ")
        pageAdapter.addFragment(SimpleFragment.newInstance("All the networks in range will be displayed, select the one that you want to connect to, and input in the password.",7),"  7  ")
        pageAdapter.addFragment(SimpleFragment.newInstance("Below, there will also be a place for you to input in the email address that you have used during sign up and create a name for the device",8),"  8  ")
        pageAdapter.addFragment(SimpleFragment.newInstance("Sample input data below.",9),"  9  ")
        pageAdapter.addFragment(SimpleFragment.newInstance("Select the ‘Save’ button once done",10)," 10  ")




        val tabs = findViewById<View>(R.id.tabs) as TabLayout
        val pager = findViewById<View>(R.id.viewPaper) as ViewPager
        pager.adapter = pageAdapter
        tabs!!.setupWithViewPager(pager)

    }

    private inner class SimpleFragmentPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager)
    // A constructor to receive a fragment manager and a List
        {
            private val mFragmentList: ArrayList<Fragment> = ArrayList()
            private val mFragmentTitleList: ArrayList<String> = ArrayList()

            override fun getItem(position: Int): Fragment {
                return mFragmentList.get(position)
            }

            override fun getCount(): Int {
                return mFragmentList.size
            }

            fun addFragment(fragment: Fragment, title: String) {
                mFragmentList.add(fragment)
                mFragmentTitleList.add(title)
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return mFragmentTitleList.get(position)
            }
    }


}