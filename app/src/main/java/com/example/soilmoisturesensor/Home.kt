package com.example.soilmoisturesensor

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

// Initialize Firebase Auth
var muid = ""
var mtoken = ""
private val TAG = "";

class Home : AppCompatActivity() {



    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        mAuth = FirebaseAuth.getInstance()



        SignOut.setOnClickListener{

            mAuth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            Toast.makeText(this, "Successfully Log out",Toast.LENGTH_LONG).show()
        }


        //Dashboard
        val mUser = FirebaseAuth.getInstance().currentUser
        mUser!!.getIdToken(true)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val idToken = task.result!!.token
                    //TODO: Send User's Name/LastName Email and idToken
                    muid = mUser.uid
                    mtoken = idToken.toString()
                   // postRequestToGetDashboardData();
//


                    //intent.putExtra("")
                    //startActivity(intent)

                } else {
                    Log.d("ERROR Creating Token", task.exception.toString());
                }
            }

        imageButtonEndPoint.setOnClickListener{

            var intent = Intent(this@Home, PlantDBViewEndpoint::class.java)
//            startActivity(Intent(this, PlantDBViewEndpoint::class.java))
            intent.putExtra("UID",muid)
            intent.putExtra("token",mtoken)
            startActivity(intent)

        }


    }

    private fun postRequestToGetDashboardData() {
        val r = JSONObject()
        r.put("uid", muid)
        r.put("token", mtoken)


        //#call to async class
        SendJsonDataToServer().execute(r.toString());
    }

    inner class SendJsonDataToServer :
        AsyncTask<String?, String?, String?>(){

        override fun doInBackground(vararg params: String?): String? {
            var JsonResponse: String? = null
            val JsonDATA = params[0]!!
            var urlConnection: HttpURLConnection? = null
            var reader: BufferedReader? = null

            try {
                val url = URL("https://www.ecoders.ca/getSensorData");
                urlConnection = url.openConnection() as HttpURLConnection;
                urlConnection.setDoOutput(true);
                // is output buffer writter
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty ("Authorization", mtoken);
                //urlConnection.setRequestProperty("token", "token");
                urlConnection.setRequestProperty("Accept", "application/json");
                //set headers and method
                val writer: Writer = BufferedWriter(OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(JsonDATA);
                // json data
                writer.close();
                val inputStream: InputStream = urlConnection.getInputStream();
                //input stream
                val buffer: StringBuffer? = null
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = BufferedReader(InputStreamReader(inputStream))

                var inputLine: String? = reader.readLine()

                var list = handleJson(inputLine)
//                adapter.submitList(list)
//                dashboardItem_list.adapter = adapter


//                var intent = Intent(baseContext, SecondEndPoint::class.java)
//                intent.putExtra("DEVICE", list[0].deviceName )
//                intent.putExtra("DATE",list[0].dateTime)
//                //intent.putExtra("")
//                startActivity(intent)

            }catch (ex:Exception){

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (ex: Exception) {
                        Log.e(TAG, "Error closing stream", ex);
                    }
                }
            }
            return null
        }

    }

    private fun handleJson(jsonString: String?) : ArrayList<SensorData> {

        val jsonArray = JSONArray(jsonString)

        val list = ArrayList<SensorData>()

        var x = 0
        while (x < jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(x)
            list.add(
                SensorData(
                    jsonObject.getInt("deviceId"),
                    jsonObject.getString("deviceName"),
                    jsonObject.getInt("battery"),
                    jsonObject.getString("dateTime"),
                    jsonObject.getInt("airValue"),
                    jsonObject.getInt("waterValue"),
                    jsonObject.getInt("soilMoistureValue"),
                    jsonObject.getInt("soilMoisturePercent")
                )
            )
            x++
        }
        return list

    }

}
