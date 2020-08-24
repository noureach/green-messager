package com.noureach.greenmessenger.registerlogin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.noureach.greenmessenger.messages.LatestMessagesActivity
import com.noureach.greenmessenger.R
import com.noureach.greenmessenger.models.User
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //set on click on button register_button_register
        register_button_register.setOnClickListener {
            //call function performRegister()
            performRegister()
        }
        //set on click on text view already_have_an_account_register
        already_have_an_account_register.setOnClickListener {
            //displays system messages in logcat
            Log.d("RegisterActivity", "Try to show Login activity")

            //use Intent for show LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        //set on click on select_photo_button_register
        select_photo_button_register.setOnClickListener {
            //displays system messages in logcat
            Log.d("RegisterActivity", "Try to show photo selector")

            //Intent.ACTION_PICK action is in buit in android and helps to pick an image item from a data source.
            val intent = Intent(Intent.ACTION_PICK)
            //set type for image
            intent.type = "image/*"
            //Once image is selected, the result will be back to our main activity, and the result will be returned to onActivityResult() method
            startActivityForResult(intent, 0)
        }
    }

    // declare global variable
    private var selectedPhotoUri: Uri? = null

    //override fun onActivityResult for display image which selected
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //check result come back
        //requestCode helps us to identify from which Intent will came back
        //resultCode use to distinguish one result from another.
        //data ia use to make sure the image was selected
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            //proceed and check what the selected image was...
            Log.d("RegisterActivity", "Image was selected!")

            //then store data in selectedPhotoUri var
            selectedPhotoUri = data.data

            // Here we are receiving the selected image Uri.
            // Once we have the uri, we can convert them to Bitmap and then display it on ImageView
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            selectphoto_imageview_register.setImageBitmap(bitmap)

            //alpha = 0f use use to make image circle in display
            select_photo_button_register.alpha = 0f
//            val bitmapDrawable = BitmapDrawable(bitmap)
//            select_photo_button_register.setBackgroundDrawable(bitmapDrawable)
        }
    }
    private fun performRegister(){
        //declare the val to store the text when user input
        val username = username_edittext_register.text.toString()
        val email = email_edittext_register.text.toString()
        val password = password_edittext_register.text.toString()

        //check the email and password whether use input or not
        if (email.isEmpty() || password.isEmpty()){
            //show toast message "Please enter email and password!"
            Toast.makeText(this, "Please enter email and password!", Toast.LENGTH_SHORT).show()
            //provide user can input again
            return
        }

        // show logcat message what the user input is...
        Log.d("RegisterActivity", "Username is : $username")
        Log.d("RegisterActivity", "Email is : $email")
        Log.d("RegisterActivity", "Password is : $password")

        //create user and store email password to Firebase
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                //use to write more code that we want when the user created successfully
            .addOnCompleteListener{
                //check if not successful
                if (!it.isSuccessful) return@addOnCompleteListener

                //show the user that created with uid
                Log.d("RegisterActivity", "Successfully created user with uid : ${it.result?.user?.uid}")

                //call function uploadImageToFirebaseStorage() when create user success
                uploadImageToFirebaseStorage()
            }
                //use to show message when the user fail to create
            .addOnFailureListener{
                Log.d("RegisterActivity", "Failed to create user: ${it.message}")
                Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun uploadImageToFirebaseStorage(){
        //check image before upload to Firebase
        if (selectedPhotoUri == null) return

        //use username to identify which user image
        val username = username_edittext_register.text.toString()
        //use to give image random ID
        val filename = UUID.randomUUID().toString()
        //use to create file images in Firebase for storing image
        val ref = FirebaseStorage.getInstance().getReference("/images/$username - ($filename)")

        // use to put image into file in Firebase
        ref.putFile(selectedPhotoUri!!)
                //use to write more code that we want when the image uploaded to Firebase successfully
            .addOnSuccessListener {
                //show successful message in logcat
                Log.d("RegisterActivity", "Successfully uploaded image: ${it.metadata?.path}")

                //use to show image link
                ref.downloadUrl.addOnSuccessListener {
                    Log.d("RegisterActivity", "File location: $it")

                    //call function
                    saveUserToFirebaseDatabase(it.toString())
                }
            }
                //use to show fail upload image message
            .addOnFailureListener{
                Log.d("RegisterActivity", "Failed upload image")

            }
    }
    private fun saveUserToFirebaseDatabase(profileImageUrl: String){
        //use to get uid user from Firebase
        val uid = FirebaseAuth.getInstance().uid ?: ""
        //use to create file /users/ in Firebase Database for store information user with uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        //store all information user in val user from class User
        val user = User(uid, username_edittext_register.text.toString(), profileImageUrl)

        //set all information user to Firebase Database
        ref.setValue(user)
            //use to write more code that we want when the user saved to Firebase Database successfully
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Finally we saved user to firebase database!")

                //use intent to load new activity LatestMessagesActivity when user saved successful
                val intent = Intent(this, LatestMessagesActivity::class.java)
                //use to clear last activity
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                //start Intent
                startActivity(intent)
            }
                //use to show fail message when user failed to save
            .addOnFailureListener{
                Log.d("RegisterActivity", "Failed save user to firebase database: ${it.message}")

            }
    }
}
