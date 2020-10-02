package com.example.soilmoisturesensor

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.fragment_update_email.*
import kotlinx.android.synthetic.main.fragment_update_password.*


/**
 * Update email fragment.
 * First, it authenticates the user and second, it changes the email
 *
 * @author Ehsan kabir
 */
class UpdateEmailFragment : Fragment() {

    val auth = FirebaseAuth.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val currentUser = auth.currentUser
        super.onViewCreated(view, savedInstanceState)
        layoutPassword.visibility = View.VISIBLE
        layoutUpdateEmail.visibility = View.GONE

        backToLoginFromemail.setOnClickListener {
            startActivity(Intent(activity, Home::class.java))
        }

        button_authenticate.setOnClickListener{
            val password = edit_text_password.text.toString()
            if (password.isEmpty()){
                edit_text_password.error = "Password required"
                edit_text_password.requestFocus()
                return@setOnClickListener
            }

            currentUser?.let { user->
                val credential = EmailAuthProvider.getCredential(user.email!!, password)
                progressbar.visibility = View.VISIBLE
                user.reauthenticate(credential)
                    .addOnCompleteListener{task->
                        progressbar.visibility = View.GONE
                        when {
                            task.isSuccessful -> {
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

            button_update.setOnClickListener {view->
                val email = edit_text_email.text.toString()
                if(email.isEmpty()){
                    edit_text_email.error = "Email required"
                    edit_text_email.requestFocus()
                    return@setOnClickListener
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    edit_text_email.error = "Valid email required"
                    edit_text_email.requestFocus()
                    return@setOnClickListener
                }

                progressbar.visibility = View.VISIBLE
                currentUser?.let {user->
                    user.updateEmail(email)
                        .addOnCompleteListener{task->
                            progressbar.visibility = View.GONE
                            if (task.isSuccessful){
                                layoutPassword.visibility = View.GONE
                                layoutUpdateEmail.visibility = View.GONE
                                Toast.makeText(activity, "Email change successful", Toast.LENGTH_SHORT).show()
                                auth.signOut()
                                startActivity(Intent(activity, SignIn::class.java))
                            }
                            else{
                                Toast.makeText(activity, "Email change failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }

        }
    }
}




