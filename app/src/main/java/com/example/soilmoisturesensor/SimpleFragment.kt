package com.example.soilmoisturesensor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

/**
 * Simple Fragment Class
 * @author Manpreet Sandhu
 */
class SimpleFragment: Fragment() {

    // Our companion object which we call to make a new Fragment
    companion object {
        // Holds the fragment id passed in when created
        val nameID = "messageID"
        val imgID = "imageID"

        fun newInstance(message: String, image: Int): SimpleFragment {
            // Create the fragment
            val fragment = SimpleFragment()
            // Create a bundle for our message/id
            val bundle = Bundle(2)
            // Load up the Bundle
            bundle.putString(nameID, message)
            bundle.putInt(imgID, image)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Get the id from the Bundle
        val name = arguments!!.getString(nameID)
        val picture = arguments!!.getInt(imgID)
        // Inflate the view as normal
        val view = inflater.inflate(R.layout.fragment_layout,container, false)

        // Get a reference to textView
        val nameTextView = view.findViewById<View>(R.id.textView_name) as TextView
        val picImageView = view.findViewById<View>(R.id.imageView_picture) as ImageView
        // Display the id in the TextView
        // Display the id in the TextView
        nameTextView.text = name
        if (picture == 5){
            picImageView.setImageResource(R.drawable.devicesetup5)
        }else if(picture == 6){
            picImageView.setImageResource(R.drawable.devicesetup6a)
        }else if (picture ==7){
            picImageView.setImageResource(R.drawable.devicesetup6b)
        }else if (picture == 8){
            picImageView.setImageResource(R.drawable.devicesetup6c)
        }else if (picture == 9){
            picImageView.setImageResource(R.drawable.devicesetup6d)
        }
        else if (picture == 10){
            picImageView.setImageResource(R.drawable.devicesetup6e)
        }

        // We could also handle any UI of any complexity in the usual way
        return view
    }
}