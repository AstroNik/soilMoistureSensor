package com.example.soilmoisturesensor

import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.content_main.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


class Home : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerlayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var adapter: RecyclerAdapter
    var muid = ""
    var mtoken = ""
    private val TAG = "";

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerlayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.nav_view)

        val Toggle = ActionBarDrawerToggle(
            this, drawerlayout, toolbar, 0, 0
        )

        drawerlayout.addDrawerListener(Toggle)
        Toggle.syncState()
        navigationView.setNavigationItemSelectedListener(this)

        mAuth = FirebaseAuth.getInstance()

        //Dashboard
        val mUser = FirebaseAuth.getInstance().currentUser
        mUser!!.getIdToken(true)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val idToken = task.result!!.token
                    //TODO: Send User's Name/LastName Email and idToken
                    muid = mUser.uid
                    mtoken = idToken.toString()
                    postRequestToGetDashboardData();
                    postRequestToGetUniqueDeviceData()
                } else {
                    Log.d("ERROR Creating Token", task.exception.toString());
                }
            }
        dashboardItem_list.layoutManager= LinearLayoutManager(this@Home)
        adapter = RecyclerAdapter()
    }

    private fun postRequestToGetUniqueDeviceData() {
        val r = JSONObject()
        val timeZone = TimeZone.getDefault();
        r.put("timezone", timeZone.id)
        r.put("uid", muid)
        r.put("token", mtoken)

        SendJsonDataToSecondEndPoint().execute(r.toString());
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

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result.equals(null)){
                val t = Toast.makeText(this@Home,"No devices to display", Toast.LENGTH_LONG)
                t.setGravity(Gravity.CENTER,0,0)
                t.show()
            }else{
                var list = handleJson(result)
                adapter.submitList(list)
                dashboardItem_list.adapter = adapter
                adapter.notifyDataSetChanged();
                dashboardItem_list.smoothScrollToPosition(0);
            }
        }
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

                if (inputLine.equals("null")){
                    return null
                }else{
                    return inputLine
                }

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

    inner class SendJsonDataToSecondEndPoint :
        AsyncTask<String?, String?, String?>(){

//        override fun onPostExecute(result: String?) {
//            super.onPostExecute(result)
//            var list = handleJson(result)
//            adapter.submitList(list)
//            dashboardItem_list.adapter = adapter
//            adapter.notifyDataSetChanged();
//            dashboardItem_list.smoothScrollToPosition(0);
//        }
        override fun doInBackground(vararg params: String?): String? {
            val JsonDATA = params[0]!!
            var urlConnection: HttpURLConnection? = null
            var reader: BufferedReader? = null

            try {
                val url = URL("https://www.ecoders.ca/uniqueDeviceData");
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

                return inputLine


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


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.Dashboard -> {
                finish()
                startActivity(Intent(applicationContext, Home::class.java))
            }
            R.id.Game -> {
                startActivity(Intent(applicationContext, Game::class.java))
            }
            R.id.Logout -> {
                mAuth.signOut()
                startActivity(Intent(this, MainActivity::class.java))
                Toast.makeText(this, "Successfully Log out", Toast.LENGTH_LONG).show()
            }
        }
        drawerlayout.closeDrawer(GravityCompat.START)
        return true
    }
}
