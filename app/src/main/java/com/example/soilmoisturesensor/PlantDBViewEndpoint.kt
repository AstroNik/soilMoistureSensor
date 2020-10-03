package com.example.soilmoisturesensor

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_plant_d_b_view_endpoint.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class PlantDBViewEndpoint : AppCompatActivity() {

    private val TAG =""

    var muid = ""
    var mtoken = ""
    var txt11:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plant_d_b_view_endpoint)

        val intg = intent
        muid = intg.getStringExtra("UID")
        mtoken = intg.getStringExtra("token")

        txt11 = txt1.text.toString()
        var plantTxtData = plantTxt.text
            txt1.text = ""
            txt2.text = ""
            txt3.text = ""
            txt4.text = ""
            txt5.text = ""
            txt6.text = ""
            txt7.text = ""
            txt8.text = ""
            txt9.text = ""
        btn.setOnClickListener{

           plantTxtData.trim()
            if(plantTxtData.isEmpty()){

                Toast.makeText(this,"Warning: Enter the Plant Name",Toast.LENGTH_LONG).show()
            }
            else {
                postRequestToGetPlantData()
            }
        }

    }


        private fun postRequestToGetPlantData() {
        val r = JSONObject()
        r.put("plantName", plantTxt.text.trim())


        //r.put("uid",muid)
        //r.put("token", mtoken)
        //#call to async class
        SendJsonDataToServer().execute(r.toString());
    }

    inner class SendJsonDataToServer :
        AsyncTask<String?, String?, String?>(){

        var inputLine: String? = null

//           override fun onPostExecute(result: String?) {
//            super.onPostExecute(result)
//            var list = handleJson(inputLine)
//            txt11 = list[0].botanicalName
//        }

        override fun doInBackground(vararg params: String?): String? {
            var JsonResponse: String? = null
            val JsonDATA = params[0]!!
            var urlConnection: HttpURLConnection? = null
            var reader: BufferedReader? = null

            try {
               // val url = URL("https://www.ecoders.ca/getAllPlantData");
                val url = URL("https://www.ecoders.ca/getPlantData");
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

                 inputLine = reader.readLine()
                this@PlantDBViewEndpoint.txt6.text = "bbjbhj"
                var out = inputLine.toString()
                var out1 = out.replace("{","")
                var out2 = out1.replace("}","")
                //var out3 = out2.replace("care","")
                var out4 = out2.replace(":",",")
                var out7 = out4.split(",").toTypedArray()

               // var list = handleJson(inputLine)
                this@PlantDBViewEndpoint.txt1.text = "Botanical Name: "+out7[3].replace("\"", "")
                this@PlantDBViewEndpoint.txt2.text = "Common Name: "+out7[5].replace("\"", "")
                this@PlantDBViewEndpoint.txt3.text = "Plant Type: "+out7[7].replace("\"", "")
                this@PlantDBViewEndpoint.txt4.text = "Mature Size: "+out7[9].replace("\"", "")
                this@PlantDBViewEndpoint.txt5.text = "Care: "
                this@PlantDBViewEndpoint.txt6.text = "Light Level: "+out7[12].replace("\"", "")
                this@PlantDBViewEndpoint.txt7.text = "Humidity: "+out7[14].replace("\"", "")
                this@PlantDBViewEndpoint.txt8.text = "Duration: "+out7[16].replace("\"", "")
                this@PlantDBViewEndpoint.txt9.text = "Direction: "+out7[18].replace("\"", "")


                if (inputLine.equals("null")) {
                    return null
                } else {

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

    private fun handleJson(jsonString: String?) : ArrayList<PlantData> {

        val jsonArray = JSONArray(jsonString)

        val list = ArrayList<PlantData>()
        val list1 = ArrayList<Care>()

        val car:Care

        val joined = ArrayList<PlantData>()




        var x = 0
        while (x < jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(x)
            list.add(
                PlantData(
                    jsonObject.getString("botanicalName"),
                    jsonObject.getString("bommonName"),
                    jsonObject.getString("plantType"),
                    jsonObject.getString("matureSize"),
                    Care(
                                jsonObject.getString("lightLevel"),
                                jsonObject.getString("humidity"),
                                jsonObject.getString("duration"),
                                jsonObject.getString("direction")
                            )
                    )

                    )
            x++
        }
        return list

    }


}

