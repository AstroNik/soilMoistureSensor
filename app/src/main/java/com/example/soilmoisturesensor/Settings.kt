package com.example.soilmoisturesensor

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_settings.edit_text_newPassword
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

/**
 * Activity to modify user informations
 * @author Ehsan Kabir
 */

private lateinit var first_name: String
private lateinit var last_name: String
private lateinit var email: String
private lateinit var id: String
private lateinit var token: String
val auth = FirebaseAuth.getInstance()
val devices = ArrayList<String>()

class Settings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        this.setTitle("Settings")

        val intent = intent
        first_name = intent.getStringExtra("first_name")
        last_name = intent.getStringExtra("last_name")
        email = intent.getStringExtra("email")
        id = intent.getStringExtra("id")
        token = intent.getStringExtra("token")

        edit_text_fname.setText(first_name)
        edit_text_lname.setText(last_name)
        edit_text_email.setText(email)

        val currentUser = auth.currentUser

        //Password authentication layout visible
        layoutPassword.visibility = View.VISIBLE
        layoutUpdateEmail.visibility = View.GONE

        //Back to Dashboard
        backToLoginFromemail.setOnClickListener {
            finish()
        }

        /**
         * Method to authenticate user with their password before
         * letting them modify other informations
         */

        button_authenticate.setOnClickListener {
            val password = edit_text_password.text.toString()
            if (password.isEmpty()) {
                edit_text_password.error = "Password required"
                edit_text_password.requestFocus()
                return@setOnClickListener
            }

            currentUser?.let { user ->
                val credential = EmailAuthProvider.getCredential(user.email!!, password)
                progressbar.visibility = View.VISIBLE
                user.reauthenticate(credential)
                    .addOnCompleteListener { task ->
                        progressbar.visibility = View.GONE
                        when {
                            task.isSuccessful -> {
                                closeKeyBoard()
                                layoutPassword.visibility = View.GONE
                                layoutUpdateEmail.visibility = View.VISIBLE
                            }
                            task.exception is FirebaseAuthInvalidCredentialsException -> {
                                edit_text_password.error = "Invalid password"
                                edit_text_password.requestFocus()
                            }
                            else -> {

                            }
                        }
                    }
            }

            /**
             * Once the user is Authenticated, method to
             * let them modify other information in fire base
             */
            button_update.setOnClickListener { view ->
                closeKeyBoard()
                val email = edit_text_email.text.toString()
                val firstName = edit_text_fname.text.toString()
                val lastName = edit_text_lname.text.toString()
                val password = edit_text_newPassword.text.toString()

                if (email.isEmpty()) {
                    edit_text_email.error = "Email required"
                    edit_text_email.requestFocus()
                    return@setOnClickListener
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    edit_text_email.error = "Valid email required"
                    edit_text_email.requestFocus()
                    return@setOnClickListener
                }
                if (password.isEmpty() || password.length < 6) {
                    edit_text_newPassword.error =
                        "Password required and must be at least six characters"
                    edit_text_newPassword.requestFocus()
                    return@setOnClickListener
                }
                if (firstName.isEmpty()) {
                    edit_text_fname.error = "First name required"
                    edit_text_fname.requestFocus()
                    return@setOnClickListener
                }
                if (lastName.isEmpty()) {
                    edit_text_lname.error = "Last name required"
                    edit_text_lname.requestFocus()
                    return@setOnClickListener
                }

                currentUser?.let { user ->
                    user!!.updatePassword(password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                user.updateEmail(email)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            val r = JSONObject()
                                            r.put("uid", id)
                                            r.put("email", email)
                                            r.put("firstName", firstName)
                                            r.put("lastName", lastName)
                                            r.put("token", token)
                                            r.put("devices", devices)

                                            //#call to async class
                                            SendJsonDataToServer().execute(r.toString());
                                            Toast.makeText(
                                                this,
                                                "Information update successful",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            auth.signOut()
                                            startActivity(Intent(this, SignIn::class.java))
                                        } else {
                                            Toast.makeText(
                                                this,
                                                "Email change failed",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            } else {
                                Toast.makeText(
                                    this,
                                    "Password change failed",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }
                }
            }
        }
    }

    /**
     * Method that closes the keyboard
     */
    private fun closeKeyBoard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }


    //ASYNC Task class - POST Request
    /**
     * Inner class that updates the user data through
     * endpoint /updateUserData
     */
    inner class SendJsonDataToServer :
        AsyncTask<String?, String?, String?>() {


        override fun doInBackground(vararg params: String?): String? {
            val JsonDATA = params[0]!!
            var urlConnection: HttpURLConnection? = null
            var reader: BufferedReader? = null

            try {
                val url = URL("https://www.ecoders.ca/updateUserData");
                urlConnection = url.openConnection() as HttpURLConnection;
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Authorization", token);
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
//                    handleUserData(inputLine)
                    return inputLine
                }
            } catch (ex: Exception) {
                print(ex.message)
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (ex: Exception) {
                        print(ex.message)
                    }
                }
            }
            return null
        }
    }
}