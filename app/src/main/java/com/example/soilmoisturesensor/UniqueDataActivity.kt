package com.example.soilmoisturesensor

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.Gravity
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

private var position: Int = -1
private lateinit var userDevices : ArrayList<String>
private lateinit var firstEndPointList: ArrayList<SensorData>
private lateinit var mAuth: FirebaseAuth
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
            startActivity((Intent(this, Home::class.java)))
        }

        val intent = intent

        userDevices = intent.getSerializableExtra("userDevices") as ArrayList<String>
        position = intent.getIntExtra("itemClicked", -1)
        val firstEndpointData = intent.getStringExtra("FirstEndpointData")
        val secondEndPointData = intent.getStringExtra("SecondEndpointData")
        firstEndPointList = handleJsonforFirstEndPoint(firstEndpointData)!!
        populateValues(firstEndPointList)

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        date.text = "Date: " + day + "/" + (month + 1) + "/" + year


        val secondEndPointList = handleJson(secondEndPointData)
        if (secondEndPointList != null) {
            showbarChart(secondEndPointList)
        } else {
//            a()
            Toast.makeText(this, "No data to display in chart", Toast.LENGTH_SHORT).show()
        }


        btn_changeDeviceName.setOnClickListener {
            val deviceName = editText_nameOfDevice.text.toString()
            if (deviceName.isEmpty()){
                Toast.makeText(this, "Device name can not be blank", Toast.LENGTH_SHORT).show()
            }else{
                changeDeviceName(deviceName)
            }
        }


        time_picker.setOnClickListener {
            val dpd = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                    var month = ""
                    var day = ""
                    if (mMonth < 9 && mDay < 10) {
                        month = "0" + (mMonth + 1)
                        day = "0" + mDay
                    } else if (mMonth < 9) {
                        month = "0" + (mMonth + 1)
                        day = "" + mDay
                    } else if (mDay < 10) {
                        day = "0" + mDay
                        month = "" + (mMonth + 1)
                    } else {
                        month = "" + (mMonth + 1)
                        day = "" + mDay
                    }
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

    private fun handleDataForSpecificDate(s: String) {

        //Dashboard
        val mUser = FirebaseAuth.getInstance().currentUser
        mUser!!.getIdToken(true)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val idToken = task.result!!.token
                    //TODO: Send User's Name/LastName Email and idToken
                    muid = mUser.uid
                    mtoken = idToken.toString()
                    postRequestToGetSpecificDateData(s);
                } else {
                    Log.d("ERROR Creating Token", task.exception.toString());
                }
            }
    }



    private fun changeDeviceName(s : String) {

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
                    val t = Toast.makeText(this, "Device Name Changed Successfully", Toast.LENGTH_SHORT)
                    t.setGravity(Gravity.CENTER, 0, 0)
                    t.show()
                } else {
                    Log.d("ERROR Creating Token", task.exception.toString());
                }
            }
    }

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
                urlConnection.setRequestProperty ("Authorization", mtoken);
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

    inner class SendJsonDataToThirdEndPoint :
        AsyncTask<String?, String?, String?>() {

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            val list = handleJsonForThirdEndpoint(result)
            if (list != null) {
                showbarChart(list)
            } else {
                barChart.clear()
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

    private fun populateValues(firstEndPointList: ArrayList<SensorData>?) {
        editText_nameOfDevice.setText(firstEndPointList?.get(0)?.deviceName)
        textview_lastUpdated.text =
            "Last Updated: " + firstEndPointList?.get(0)?.dateTime?.let { formatDateFull(it) }
    }

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
        barDataSet.colors = ColorTemplate.createColors(ColorTemplate.LIBERTY_COLORS)
        barDataSet.valueTextColor = Color.BLACK
        barDataSet.valueTextSize = 16f

        val barData = BarData(barDataSet)
        barData.setBarWidth(0.1f); // set custom bar width

        barChart.setFitBars(true)
        barChart.setDragEnabled(true)
        barChart.setVisibleXRangeMaximum(3f)

        barChart.setPinchZoom(true);

        barChart.getXAxis().position = XAxis.XAxisPosition.BOTTOM
        barChart.getXAxis().mAxisMaximum = 5f
        barChart.getXAxis().isGranularityEnabled = true
        barChart.data = barData
        barChart.description.text = "Moisture percentages at different times"
        barChart.animateY(1500)

    }

}


private fun handleJson(jsonString: String?): ArrayList<UniqueData>? {
    if (jsonString.equals(null))
        return null
    try {
        val jsonArray = JSONArray(jsonString)
        val list = ArrayList<UniqueData>()
        if (jsonArray.getJSONObject(position) != null) {
            val jsonObject = jsonArray.getJSONObject(position)
            list.add(
                UniqueData(
                    jsonObject.getInt("deviceId"),
                    jsonObject.getString("dateTime"),
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