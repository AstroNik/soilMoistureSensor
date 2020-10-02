package com.example.soilmoisturesensor

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.fragment_update_email.*
import kotlinx.android.synthetic.main.fragment_update_password.*

/**
 * Update password fragment.
 * First, it checks the credential and re-authenticates the user
 * Then it changes the password
 *
 * @author Ehsan kabir
 */
class UpdatePasswordFragment : Fragment() {

    val auth = FirebaseAuth.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_changePassword.setOnClickListener {
            changePassword()
        }
        backToLogin.setOnClickListener {
            startActivity(Intent(activity, Home::class.java))
        }

    }

    private fun changePassword() {
        val user = auth.currentUser
        if (edit_text_oldPassword.text.isNotEmpty() &&
            edit_text_newPassword.text.isNotEmpty() &&
            edit_text_confirmPassword.text.isNotEmpty()
        ) {
            if (edit_text_newPassword.length() >5) {
                if (edit_text_newPassword.text.toString()
                        .equals(edit_text_confirmPassword.text.toString())
                ) {
                    if (user != null && user.email != null) {
                        val credential = EmailAuthProvider.getCredential(
                            user.email!!,
                            edit_text_oldPassword.text.toString()
                        )

                        user?.reauthenticate(credential)
                            ?.addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Toast.makeText(
                                        activity,
                                        "Re-authentication success",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    changePasswordInFirebase(user)
                                } else {
                                    Toast.makeText(
                                        activity,
                                        "Re-authentication failed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else {

                    }
                } else {
                    Toast.makeText(activity, "Password mismatch", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(activity, "Passsword must be atleast six characters long", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            Toast.makeText(activity, "Please enter all the fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun changePasswordInFirebase(user: FirebaseUser) {
        val pass = edit_text_confirmPassword.text.toString().trim()
        user!!.updatePassword(pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(activity, "Passsword successfully changed", Toast.LENGTH_SHORT)
                        .show()
                    passordChangeLayout.visibility = View.GONE
                    auth.signOut()
                    startActivity(Intent(activity, SignIn::class.java))
                } else {

                }
            }
    }


}