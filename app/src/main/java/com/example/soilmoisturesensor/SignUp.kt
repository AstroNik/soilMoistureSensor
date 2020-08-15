package com.example.soilmoisturesensor

import android.content.Intent
import android.net.NetworkRequest
import android.net.http.HttpResponseCache
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.io.*
import java.net.URL
import java.net.URLEncoder
import javax.net.ssl.HttpsURLConnection



class SignUp : AppCompatActivity() {

    // Initialize Firebase Auth
    val mAuth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)


        ok.setOnClickListener(View.OnClickListener { view ->
            register()

            //Save the data in the database, display a message that account has been created
        })

        cancel.setOnClickListener {

            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun register() {

        val mName = fullName.text.toString()
        val mEmail = Email.text.toString()
        val mPwd = password1.text.toString()
        val mPwd1 = password2.text.toString()

        if (!mName.isEmpty() && !mEmail.isEmpty() && !mPwd.isEmpty()) {

            if (mPwd.equals(mPwd1)) {

                mAuth.createUserWithEmailAndPassword(mEmail, mPwd)
                    .addOnCompleteListener(this, OnCompleteListener { task ->

                        if (task.isSuccessful) {

                            val mUser = FirebaseAuth.getInstance().currentUser
                            mUser!!.getIdToken(true)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val idToken = task.result!!.token
                                        //TODO: Send User's Name/LastName Email and idToken
                                        val uid = mUser.uid

                                        val email = mEmail  //sub this out to just mEmail when sending

                                        val firstSpace =  mName.indexOf(" ")
                                        val firstName = mName.substring(0, firstSpace) //split this into first and last name in the form
                                        val lastName = mName.substring(firstSpace).trim()

                                        val token = idToken // can simply just pass

                                        //THE ENDPOINT IS https://www.ecoders.ca/addUser
                                        //Invoking the SendPost Request Method
                                        sendPostRequest(uid.toString(), email, firstName,lastName,token.toString());


                                    } else {
                                        Log.d("ERROR Creating Token",task.exception.toString());
                                    }
                                }

                            startActivity(Intent(this, MainActivity::class.java))

                            Toast.makeText(this, "SignUp Successful", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this, "Error :(", Toast.LENGTH_LONG).show()

                        }
                    })
            } else {

                Toast.makeText(this, "Password doesnot match", Toast.LENGTH_LONG).show()
            }
        } else {

            Toast.makeText(this, "Please fill values", Toast.LENGTH_SHORT).show()
        }
    }

    //Post Request Method
    fun sendPostRequest(uid:String, email:String, fName:String, lName:String, token: String){


        //Req. Parameters
        var reqParam = URLEncoder.encode("uid", "UTF-8") + "=" + URLEncoder.encode(uid, "UTF-8")
        reqParam += "&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8")
        reqParam += "&" + URLEncoder.encode("fname", "UTF-8") + "=" + URLEncoder.encode(fName, "UTF-8")
        reqParam += "&" + URLEncoder.encode("lname", "UTF-8") + "=" + URLEncoder.encode(lName, "UTF-8")
        reqParam += "&" + URLEncoder.encode("token", "UTF-8") + "=" + URLEncoder.encode(token, "UTF-8")

        //ENDPOINT SERVER URL
        val mURL = URL("https://www.ecoders.ca/addUser")

        //mURL.addRequestProperty("AUTHORIZATON", token);
        var connection: HttpsURLConnection;

        connection = mURL.openConnection() as HttpsURLConnection;

        connection.addRequestProperty("Authorization",token);
        connection.addRequestProperty("Content-Type","application/json");
        connection.connect();

        with(connection){

            requestMethod = "POST"

            val wr = OutputStreamWriter(outputStream)
            wr.write(reqParam)
            wr.flush()

            println("URL : $url")
            println("Response Code : $responseCode")

            BufferedReader(InputStreamReader(inputStream)).use{

                val response = StringBuffer()

                var inputLine = it.readLine()

                while (inputLine != null){

                    response.append(inputLine)
                    inputLine = it.readLine()
                }

                println("Response: $response")
            }
        }


    }

}
