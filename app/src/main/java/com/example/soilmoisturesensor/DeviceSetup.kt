package com.example.soilmoisturesensor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
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
        
        // Initialize a list of three fragments
        val fragmentList = ArrayList<Fragment>()

        // Add 4 new Fragments to the list
        fragmentList.add(SimpleFragment.newInstance("Step1: Ensure that you have created an account",1))
        fragmentList.add(SimpleFragment.newInstance("Step2: Place the battery inside the device\n " +
                "\n a. Make sure it is an 18650 Battery \n" +
                "\n b. Make sure the battery is correlation with the positive and negative sides of the device",2))
        fragmentList.add(SimpleFragment.newInstance("Step3: Place the device in the soil of the plant you want to be able to retrieve the data from",3))
        fragmentList.add(SimpleFragment.newInstance("Step4: Turn the device on with the switch on the top left of the device itself",4))
        fragmentList.add(SimpleFragment.newInstance("Step5: Go to your network settings, find and connect to the network ‘ECOders Sensor",5))
        fragmentList.add(SimpleFragment.newInstance("Step6: Once connected it will direct you to a website where you will place your WiFi credentials and device data"+
                "\n\n a. Once connected to ‘ECOders Sensor’ this page will be displayed. Select the ‘Configure WiFi’ button",6))
        fragmentList.add(SimpleFragment.newInstance("b. All the networks in range will be displayed, select the one that you want to connect to, and input in the password.",7))
        fragmentList.add(SimpleFragment.newInstance("c. Below, there will also be a place for you to input in the email address that you have used during sign up and create a name for the device",8))
        fragmentList.add(SimpleFragment.newInstance("d. Sample input data below.",9))
        fragmentList.add(SimpleFragment.newInstance("e. Select the ‘Save’ button once done",10))


        val pageAdapter = SimpleFragmentPagerAdapter(
            supportFragmentManager, fragmentList)

        val pager = findViewById<View>(R.id.viewPaper) as ViewPager
        pager.adapter = pageAdapter

    }

    private inner class SimpleFragmentPagerAdapter
    // A constructor to receive a fragment manager and a List
        (fm: FragmentManager,
        // An ArrayList to hold our fragments
         private val fragments: ArrayList<Fragment>)
        : FragmentStatePagerAdapter(fm) {

        // Just two methods to override to get the current
        // position of the adapter and the size of the List
        override fun getItem(position: Int): Fragment {
            return this.fragments[position]
        }

        override fun getCount(): Int {
            return this.fragments.size
        }
    }


}