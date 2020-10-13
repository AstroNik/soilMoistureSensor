package com.example.soilmoisturesensor

import android.content.ClipData
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_notifications.*
import kotlinx.android.synthetic.main.content_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

/**
 * Notification activity which is basically the Nofitcation Page of the Application
 * @author Manpreet Sandhu
 */
class Notifications : AppCompatActivity() {

    private lateinit var notification_adapter: NotificationRecyclerViewAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var deleteIcon: Drawable
    private lateinit var list: ArrayList<NotificationData>

    private var muid = ""
    private var mtoken = ""
    private val TAG = "";
    private var swipeBackground: ColorDrawable = ColorDrawable(Color.parseColor("#FF0000"))
    private var swipePosition = -1

    private var userDevices = ArrayList<String>()
    private var deviceId = ArrayList<String>()
    private var devNames = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        this.setTitle("Notifications")

        mAuth = FirebaseAuth.getInstance()
        val mUser = FirebaseAuth.getInstance().currentUser

        deleteIcon = ContextCompat.getDrawable(this, R.drawable.ic_delete)!!

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


        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT
                    or ItemTouchHelper.RIGHT
        ) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, position: Int) {

                swipePosition = viewHolder.adapterPosition
                postRequestToDeleteNotifications()
                notification_adapter.removeItem(viewHolder)


            }
            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {


                val itemView = viewHolder.itemView
                val iconMargin = (itemView.height - deleteIcon.intrinsicHeight) / 2

                if (dX > 0) {

                    swipeBackground.setBounds(
                        itemView.left,
                        itemView.top,
                        dX.toInt(),
                        itemView.bottom
                    )
                    deleteIcon.setBounds(
                        itemView.left + iconMargin,
                        itemView.top + iconMargin,
                        itemView.left + iconMargin + deleteIcon.intrinsicWidth,
                        itemView.bottom - iconMargin
                    )

                } else {

                    swipeBackground.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                    deleteIcon.setBounds(
                        itemView.right - iconMargin - deleteIcon.intrinsicWidth,
                        itemView.top + iconMargin,
                        itemView.right - iconMargin,
                        itemView.bottom - iconMargin
                    )

                }
                swipeBackground.draw(c)

                c.save() //saving the canvas

                if (dX > 0) {
                    c.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                } else {
                    c.clipRect(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                }
                deleteIcon.draw(c)

                c.restore() //restoring the canvas

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }

        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(notificationsRecylerView)

        backToHome.setOnClickListener {
            finish()
            startActivity((Intent(this, Home::class.java)))
        }

    }


    /**
     * Method to add uid, token to the header and do a post request
     * @author Manpreet Sandhu
     */
    private fun postRequestToGetNotifications() {
        val r = JSONObject()
        r.put("uid", muid)
        r.put("token", mtoken)
        SendJsonDataToServer().execute(r.toString());
    }

    /**
     * Inner Class to get Notification data  by calling our
     * endpoint https://www.ecoders.ca/getNotifications
     *
     * @author Manpreet Sandhu
     */
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
                list = handleJson1(result)

                //get intent object, data from intent
                val intent = getIntent()
                userDevices = intent.getStringArrayListExtra("DeviceName")
                deviceId = intent.getStringArrayListExtra("DeviceId")

                //get the deviceName for the available deviceId's
                var index = 0


                for (j in deviceId){
                    for(i in list){
                        val a = i.deviceID.toString()
                        val b = i.deviceID
                        if (j.equals(i.deviceID.toString()))
                            devNames.add(userDevices.get(index))
                    }
                    index++
                }

                list = handleJson2(result)


                notificationsRecylerView.layoutManager = LinearLayoutManager(this@Notifications)
                notification_adapter = NotificationRecyclerViewAdapter()
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


    /**
    //     * This converts json String to a Array list
    //     * In this case, it is converting the data retrieved from the
    //     * endpoint /getNoficationData and converting it to an arraylist
    //     * of SensorData
    //     *
    //     * @author Manpreet Sandhu
    //     */
    private fun handleJson1(jsonString: String?): ArrayList<NotificationData> {
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
                    jsonObject.getBoolean("isRead"),
                    ""

                )
            )
            x++
        }
        return list
    }

    /**
     * This converts json String to a Array list
     * In this case, it is converting the data retrieved from the
     * endpoint /getNoficationData and converting it to an arraylist
     * of SensorData
     *
     * @author Manpreet Sandhu
     */
    private fun handleJson2(jsonString: String?): ArrayList<NotificationData> {
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
                    jsonObject.getBoolean("isRead"),
                    devNames[x]
                )
            )
            x++
        }
        return list
    }


    /**
     * Method to add uid, notificationId to the header and do a post request
     * @author Manpreet Sandhu
     */
    private fun postRequestToDeleteNotifications() {
        val r = JSONObject()
        val deviceID = list[swipePosition].deviceID
        r.put("deviceID", deviceID)
        r.put("uid", muid)
        SendJsonDataToServerTwo().execute(r.toString());
    }

    /**
     * Inner Class to Update Notification data  by calling our
     * endpoint https://www.ecoders.ca/updateNotification
     *
     * @author Manpreet Sandhu
     */
    inner class SendJsonDataToServerTwo :
        AsyncTask<String?, String?, String?>() {

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            notificationsRecylerView.layoutManager = LinearLayoutManager(this@Notifications)
            notification_adapter = NotificationRecyclerViewAdapter()

            notification_adapter.submitList(list)
            notificationsRecylerView.adapter = notification_adapter

            if (list.size == 0) {
                val t =
                    Toast.makeText(this@Notifications, "No more Notifications", Toast.LENGTH_LONG)
                t.setGravity(Gravity.CENTER, 0, 0)
                t.show()
            }

            notification_adapter.notifyDataSetChanged()
            notificationsRecylerView.smoothScrollToPosition(0)


        }

        override fun doInBackground(vararg params: String?): String? {
            val JsonDATA = params[0]!!
            var urlConnection: HttpURLConnection? = null
            var reader: BufferedReader? = null

            try {
                val url = URL("https://www.ecoders.ca/updateNotification");
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
}


