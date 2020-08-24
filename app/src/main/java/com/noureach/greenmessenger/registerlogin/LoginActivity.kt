package com.noureach.greenmessenger.registerlogin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.noureach.greenmessenger.messages.LatestMessagesActivity
import com.noureach.greenmessenger.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //set on click on button login_button_login
        login_button_login.setOnClickListener {
            //declare val email and password to store email and password from edit text that user input
            val email = email_edittext_login.text.toString()
            val password = password_edittext_login.text.toString()

            Log.d("Login", "Attempt login with email/pw : $email/$password")

            //check the email and password whether use input or not
            if (email.isEmpty() || password.isEmpty()){
                //show toast message "Please enter email and password!"
                Toast.makeText(this, "Please enter email and password!", Toast.LENGTH_SHORT).show()
                //provide user can input again
                return@setOnClickListener
            }
            //use for user can login with email and password
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                //use to write more code that we want when user login successfully
                .addOnSuccessListener {
                    //show LatestMessageActivity when user login successfully
                    val intent = Intent(this, LatestMessagesActivity::class.java)
                    //use to clear last activity
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    //start Intent
                    startActivity(intent)
                }
                    //show toast message when user fail to login
                .addOnFailureListener {
                    Toast.makeText(this, "Email/Password incorrect!", Toast.LENGTH_SHORT).show()
                }
        }
        //set on click on back_to_register_textview_login
        back_to_register_textview_login.setOnClickListener {
            //finish current activity and back to last activity
            finish()
        }
    }
}