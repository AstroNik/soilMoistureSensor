package com.example.soilmoisturesensor

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_notifications.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class Notifications : AppCompatActivity(), NotificationRecyclerViewAdapter.onItemClickListener {
    private lateinit var notification_adapter: NotificationRecyclerViewAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var list: ArrayList<NotificationData>
    private var clickPosition = -1

    var muid = ""
    var mtoken = ""
    private val TAG = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        mAuth = FirebaseAuth.getInstance()
        val mUser = FirebaseAuth.getInstance().currentUser

        mUser!!.getIdToken(true)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val idToken = task.result!!.token
                    muid = mUser.uid
                    mtoken = idToken.toString()

                    postRequestToGetNotifications()

                } else {
                    Log.d("ERROR Creating Token", task.exception.toString());
                }
            }


        backToHome.setOnClickListener {
            finish()
        }

    }


    private fun postRequestToGetNotifications() {
        val r = JSONObject()
        r.put("uid", muid)
        r.put("token", mtoken)
        SendJsonDataToServer().execute(r.toString());
    }

    inner class SendJsonDataToServer :
        AsyncTask<String?, String?, String?>() {

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result.equals(null)) {
                val t =
                    Toast.makeText(this@Notifications, "No New Notifications", Toast.LENGTH_LONG)
                t.setGravity(Gravity.CENTER, 0, 0)
                t.show()
            } else {
                list = handleJson(result)

                notificationsRecylerView.layoutManager = LinearLayoutManager(this@Notifications)
                notification_adapter = NotificationRecyclerViewAdapter(this@Notifications)

                notification_adapter.submitList(list)
                notificationsRecylerView.adapter = notification_adapter
                notification_adapter.notifyDataSetChanged();
                notificationsRecylerView.smoothScrollToPosition(0);
            }
        }

        override fun doInBackground(vararg params: String?): String? {
            val JsonDATA = params[0]!!
            var urlConnection: HttpURLConnection? = null
            var reader: BufferedReader? = null

            try {
                val url = URL("https://www.ecoders.ca/getNotifications");
                urlConnection = url.openConnection() as HttpURLConnection;
                urlConnection.setDoOutput(true);
                // is output buffer writter
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Authorization", mtoken);
                urlConnection.setRequestProperty("Accept", "application/json");
                //set headers and method
                val writer: Writer =
                    BufferedWriter(OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
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

                var inputLine = reader.readLine()

                if (inputLine.equals("null")) {
                    return null
                } else {
                    return inputLine
                }
            } catch (ex: Exception) {
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

    private fun handleJson(jsonString: String?): ArrayList<NotificationData> {
        val jsonArray = JSONArray(jsonString)
        val list = ArrayList<NotificationData>()
        var x = 0
        while (x < jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(x)
            list.add(
                NotificationData(
                    jsonObject.getInt("notificationId"),
                    jsonObject.getInt("deviceId"),
                    jsonObject.getString("dateTime"),
                    jsonObject.getString("title"),
                    jsonObject.getString("content"),
                    jsonObject.getBoolean("isRead")
                )
            )
            x++
        }
        return list
    }

    override fun onItemClick(position: Int) {
        clickPosition = position
    }
}



















































































































//    private fun postRequestToDeleteNotifications() {
//        val r = JSONObject()
//        val notificationID = list[clickPosition].notificationID
//        r.put("notificationID", notificationID)
//        r.put("uid", muid)
//        SendJsonDataToServerTwo().execute(r.toString());
//    }
//
//    inner class SendJsonDataToServerTwo :
//        AsyncTask<String?, String?, String?>() {
//
//        override fun onPostExecute(result: String?) {
//            super.onPostExecute(result)
//            list.remove(list[clickPosition])
//
//            notificationsRecylerView.layoutManager = LinearLayoutManager(this@Notifications)
//            notification_adapter = NotificationRecyclerViewAdapter(this@Notifications)
//
//            notification_adapter.submitList(list)
//            notificationsRecylerView.adapter = notification_adapter
//
//            if (list.size == 0) {
//                val t =
//                    Toast.makeText(this@Notifications, "No more Notifications", Toast.LENGTH_LONG)
//                t.setGravity(Gravity.CENTER, 0, 0)
//                t.show()
//            }
//
//            notification_adapter.notifyDataSetChanged()
//            notificationsRecylerView.smoothScrollToPosition(0)
//
//
//        }
//
//        override fun doInBackground(vararg params: String?): String? {
//            val JsonDATA = params[0]!!
//            var urlConnection: HttpURLConnection? = null
//            var reader: BufferedReader? = null
//
//            try {
//                val url = URL("https://www.ecoders.ca/updateNotification");
//                urlConnection = url.openConnection() as HttpURLConnection;
//                urlConnection.setDoOutput(true);
//                // is output buffer writter
//                urlConnection.setRequestMethod("POST");
//                urlConnection.setRequestProperty("Content-Type", "application/json");
//                urlConnection.setRequestProperty("Authorization", mtoken);
//                urlConnection.setRequestProperty("Accept", "application/json");
//                //set headers and method
//                val writer: Writer =
//                    BufferedWriter(OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
//                writer.write(JsonDATA);
//                // json data
//                writer.close();
//                val inputStream: InputStream = urlConnection.getInputStream();
//                //input stream
//                val buffer: StringBuffer? = null
//                if (inputStream == null) {
//                    // Nothing to do.
//                    return null;
//                }
//                reader = BufferedReader(InputStreamReader(inputStream))
//
//                var inputLine = reader.readLine()
//
//                if (inputLine.equals("null")) {
//                    return null
//                } else {
//                    return inputLine
//                }
//            } catch (ex: Exception) {
//            } finally {
//                if (urlConnection != null) {
//                    urlConnection.disconnect();
//                }
//                if (reader != null) {
//                    try {
//                        reader.close();
//                    } catch (ex: Exception) {
//                        Log.e(TAG, "Error closing stream", ex);
//                    }
//                }
//
//            }
//            return null
//        }
//    }