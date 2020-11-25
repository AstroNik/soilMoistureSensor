package com.example.soilmoisturesensor

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_unique_data.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Activity that handles details of a device when clicked from the dashboard
 * @author Ehsan Kabir
 */
private var position: Int = -1
private lateinit var userDevices: ArrayList<String>
private lateinit var firstEndPointList: ArrayList<SensorData>
private lateinit var mAuth: FirebaseAuth
private lateinit var deviceName: String
private val TAG = "";
var muid = ""
var mtoken = ""

class UniqueDataActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unique_data)

        mAuth = FirebaseAuth.getInstance()

        backToHome.setOnClickListener {
            finish()
        }

        userDevices = intent.getSerializableExtra("userDevices") as ArrayList<String>
        position = intent.getIntExtra("itemClicked", -1)
        val firstEndpointData = intent.getStringExtra("FirstEndpointData")
        firstEndPointList = handleJsonforFirstEndPoint(firstEndpointData)!!
        deviceName = firstEndPointList?.get(0)?.deviceName.toString()
        populateValues(firstEndPointList)

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        date.text = "Date: " + day + "/" + (month + 1) + "/" + year
        handleDataForSpecificDate("" + year + "-" + formatMonth(month) + "-" + formatDay(day) + "T00:00:00.000Z")


        //Change device name button on click handler
        btn_changeDeviceName.setOnClickListener {
            val deviceName = editText_nameOfDevice.text.toString()
            if (deviceName.isEmpty()) {
                Toast.makeText(this, "Device name can not be blank", Toast.LENGTH_SHORT).show()
            } else {
                closeKeyBoard()
                changeDeviceName(deviceName)
            }
        }

        //calendar button on click handler
        time_picker.setOnClickListener {
            val dpd = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                    var month = formatMonth(mMonth)
                    var day = formatDay(mDay)
                    date.text = ("Date: " + day + "/" + (month) + "/" + mYear)
                    handleDataForSpecificDate("" + mYear + "-" + (month) + "-" + day + "T00:00:00.000Z")

                },
                year,
                month,
                day
            )
            dpd.show()
        }
    }

    /**
     * Method to handle specific date requests from the calendar
     * and shows the graph
     */
    private fun handleDataForSpecificDate(s: String) {

        val mUser = FirebaseAuth.getInstance().currentUser
        mUser!!.getIdToken(true)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val idToken = task.result!!.token
                    muid = mUser.uid
                    mtoken = idToken.toString()
                    postRequestToGetSpecificDateData(s);
                } else {
                    Log.d("ERROR Creating Token", task.exception.toString());
                }
            }
    }


    /**
     * Method to change Device name
     */
    private fun changeDeviceName(s: String) {

        val mUser = FirebaseAuth.getInstance().currentUser
        mUser!!.getIdToken(true)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val idToken = task.result!!.token
                    val r = JSONObject()
                    mtoken = idToken.toString()
                    r.put("uid", mUser.uid)
                    r.put("deviceId", firstEndPointList[0].deviceId)
                    r.put("deviceName", s)
                    r.put("token", mtoken)

                    SendJsonDataToUpateDeviceName().execute(r.toString());
                    deviceDetailHeader.text = "Device Details for " + deviceName
                    val t =
                        Toast.makeText(this, "Device Name Changed Successfully", Toast.LENGTH_SHORT)
                    t.setGravity(Gravity.CENTER, 0, 0)
                    t.show()
                    startActivity(Intent(this, Home::class.java))
                } else {
                    Log.d("ERROR Creating Token", task.exception.toString());
                }
            }
    }

    /**
     * Inner class that updates device name Asynchronously.
     * endpoint /updateDeviceName
     */
    inner class SendJsonDataToUpateDeviceName :
        AsyncTask<String?, String?, String?>() {

        override fun doInBackground(vararg params: String?): String? {
            val JsonDATA = params[0]!!
            var urlConnection: HttpURLConnection? = null
            var reader: BufferedReader? = null

            try {
                val url = URL("https://www.ecoders.ca/updateDeviceName");
                urlConnection = url.openConnection() as HttpURLConnection;
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Authorization", mtoken);
                urlConnection.setRequestProperty("Accept", "application/json");
                val writer: Writer =
                    BufferedWriter(OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(JsonDATA);
                writer.close();
                val inputStream: InputStream = urlConnection.getInputStream();
                if (inputStream == null) {
                    return null;
                }
                reader = BufferedReader(InputStreamReader(inputStream))

                var inputLine: String? = reader.readLine()

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

    private fun postRequestToGetSpecificDateData(s: String) {
        val r = JSONObject()
        val timeZone = TimeZone.getDefault();
        r.put("timezone", timeZone.id)
        r.put("uid", muid)
        r.put("deviceId", firstEndPointList[0].deviceId)
        r.put("date", s)

        SendJsonDataToThirdEndPoint().execute(r.toString());
    }

    /**
     * Inner class that updates graph when a date is picked from date picker Asynchronously.
     * endpoint /specificDate
     */
    inner class SendJsonDataToThirdEndPoint :
        AsyncTask<String?, String?, String?>() {

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            val list = handleJsonForThirdEndpoint(result)
            if (list != null) {
                showbarChart(list)
            } else {
                barChart.clear()
                Toast.makeText(this@UniqueDataActivity, "No data to display", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        override fun doInBackground(vararg params: String?): String? {
            val JsonDATA = params[0]!!
            var urlConnection: HttpURLConnection? = null
            var reader: BufferedReader? = null

            try {
                val url = URL("https://www.ecoders.ca/specificDate");
                urlConnection = url.openConnection() as HttpURLConnection;
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                val writer: Writer =
                    BufferedWriter(OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(JsonDATA);
                writer.close();
                val inputStream: InputStream = urlConnection.getInputStream();
                if (inputStream == null) {
                    return null;
                }
                reader = BufferedReader(InputStreamReader(inputStream))

                var inputLine: String? = reader.readLine()

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
     * Method to close keyboard
     */
    private fun closeKeyBoard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    /**
     * Method that populates device name and last updated time
     */
    private fun populateValues(firstEndPointList: ArrayList<SensorData>?) {
        editText_nameOfDevice.setText(deviceName)
        textview_lastUpdated.text =
            "Last Updated: " + firstEndPointList?.get(0)?.dateTime?.let { formatDateFull(it) }
        deviceDetailHeader.text = "Device Details for " + deviceName
    }

    /**
     * Method that populates the bar graph
     * @param ArrayList<UniqueData>
     */
    private fun showbarChart(list: ArrayList<UniqueData>) {
        val barChart = findViewById(R.id.barChart) as BarChart
        val barEntry = ArrayList<BarEntry>()
        var x = 0
        while (x < list.size) {
            val time = list[x].dateTime
            barEntry.add(
                BarEntry(
                    formatDate(time).toFloat(),
                    (list[x].soilMoisturePercent).toFloat()
                )
            )
            x++
        }
        val barDataSet = BarDataSet(barEntry, "Moisture Percentages")
        barDataSet.setColors(Color.rgb(0, 0, 155))
        barDataSet.valueTextColor = Color.BLACK
        barDataSet.valueTextSize = 13f
        barDataSet.setDrawValues(false);
        val barData = BarData(barDataSet)
        barData.setBarWidth(0.1f); // set custom bar width

        barChart.setFitBars(true)
        barChart.setDragEnabled(true)
        barChart.setVisibleXRangeMaximum(12f)

        barChart.getXAxis().setLabelCount(24)
        barChart.setPinchZoom(true);

        barChart.getXAxis().position = XAxis.XAxisPosition.BOTTOM

        barChart.getXAxis().isGranularityEnabled = true
        barChart.data = barData
        barChart.description.text = "Moisture percentages at different times"
        barChart.animateY(1700)
        barChart.invalidate()
    }
}

/**
 * Accepts json String, extracts data and creates an array list of unique data
 * @param jsonString
 * @return ArrayList<UniqueData>
 */
private fun handleJsonForThirdEndpoint(jsonString: String?): ArrayList<UniqueData>? {
    if (jsonString.equals(null))
        return null
    try {
        val jsonArray = JSONArray(jsonString)
        val list = ArrayList<UniqueData>()
        var x = 0
        while (x < jsonArray.length()) {
            if (jsonArray.getJSONObject(x) != null) {
                val jsonObject = jsonArray.getJSONObject(x)
                list.add(
                    UniqueData(
                        jsonObject.getInt("deviceId"),
                        jsonObject.getString("dateTime"),
                        jsonObject.getInt("soilMoisturePercent")
                    )
                )
                x++
            }
        }
        return list
    } catch (e: JSONException) {
        e.printStackTrace()
    }
    return null
}

/**
 * Accepts json String, extracts data and creates an array list of sensor data
 * @param jsonString
 * @return ArrayList<SensorData>
 */
private fun handleJsonforFirstEndPoint(jsonString: String?): ArrayList<SensorData>? {
    try {
        val jsonArray = JSONArray(jsonString)
        val list = ArrayList<SensorData>()
        if (jsonArray.getJSONObject(position) != null) {
            val jsonObject = jsonArray.getJSONObject(position)
            list.add(
                SensorData(
                    jsonObject.getInt("deviceId"),
                    userDevices[position],
                    jsonObject.getInt("battery"),
                    jsonObject.getString("dateTime"),
                    jsonObject.getInt("airValue"),
                    jsonObject.getInt("waterValue"),
                    jsonObject.getInt("soilMoistureValue"),
                    jsonObject.getInt("soilMoisturePercent")
                )
            )
        }
        return list
    } catch (e: JSONException) {
        e.printStackTrace()
    }
    return null
}


/**
 * All the methods below are some of the methods to format the date according to need
 *
 */
fun formatDate(date: String): String {
    var deviceDate = date.replace("\\..*".toRegex(), "")
    deviceDate = deviceDate.replace("T".toRegex(), " ")
    deviceDate = deviceDate.toDate().formatTo("HH.mm")
    return deviceDate
}

fun String.toDate(
    dateFormat: String = "yyyy-MM-dd HH:mm:ss",
    timeZone: TimeZone = TimeZone.getTimeZone("UTC")
): Date {
    val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
    parser.timeZone = timeZone
    return parser.parse(this)
}

fun Date.formatTo(dateFormat: String, timeZone: TimeZone = TimeZone.getDefault()): String {
    val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
    formatter.timeZone = timeZone
    return formatter.format(this)
}

fun formatDateFull(date: String): String {
    var deviceDate = date.replace("\\..*".toRegex(), "")
    deviceDate = deviceDate.replace("T".toRegex(), " ")
    deviceDate = deviceDate.toDate().formatTo("dd MMM yyyy h:mm a")
    return deviceDate
}

fun formatMonth(month: Int): String {

    if (month < 9) {
        return "0" + (month + 1)
    } else {
        return "" + (month + 1)
    }
}

fun formatDay(day: Int): String {
    if (day < 10) {
        return "0" + day
    } else {
        return "" + day
    }
}

