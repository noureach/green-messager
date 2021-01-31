package com.noureach.greenmessenger.messages

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.noureach.greenmessenger.R
import com.noureach.greenmessenger.models.ChatMessage
import com.noureach.greenmessenger.models.User
import com.noureach.greenmessenger.registerlogin.PartnerProfile
import com.noureach.greenmessenger.registerlogin.ProfileUserActivity
import com.noureach.greenmessenger.views.ChatFromItem
import com.noureach.greenmessenger.views.ChatToItem
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_log_action_bar.*
import kotlinx.android.synthetic.main.chat_to_row.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import kotlinx.android.synthetic.main.latest_message_row.*

class ChatLogActivity : AppCompatActivity() {

    //use companion object to make val TAG static and global variable
    companion object {
        const val TAG = "Chat Log"
    }

    //use GroupAdapter<ViewHolder>() to bind data into ViewHolder = adapter
    val adapter = GroupAdapter<ViewHolder>()

    //separates a data from User class = var toUser
    var toUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        //connect adapter with recycler view chat log
        recyclerview_chatlog.adapter = adapter

        // use Parcelable to pass custom Model or Data class from one Activity to another Activity
        //get data from NewMessageActivity by using key value = USER_KEY into var toUser
        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        /*use username as a actionbar in ChatLogActivity from class User
        and that username is the username
        that we clicked on user in NewMessageActivity
         */
        //supportActionBar?.title = toUser?.username

        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.chat_log_action_bar)

        tv_chat_log.text = toUser?.username
        Picasso.get().load(toUser?.profileImageUrl).into(iv_chat_log)

        iv_chat_log.setOnClickListener {
            val intent = Intent(this, PartnerProfile::class.java)
            intent.putExtra("Partner", toUser)
            startActivity(intent)
        }
        tv_chat_log.setOnClickListener {
            val intent = Intent(this, PartnerProfile::class.java)
            intent.putExtra("Partner", toUser)
            startActivity(intent)
        }
        iv_back_chat_log.setOnClickListener {
            val intent = Intent(this, LatestMessagesActivity::class.java)
            startActivity(intent)
        }

        //call fun
        listenForMessage()

        //add action
        send_button_chat_log.setOnClickListener {
            Log.d(TAG, "Attempt to send message...")
            //call fun
            performSendMessage()
        }
    }
    private fun listenForMessage() {
        //Retrieve uid from FirebaseAuth
        val fromId = FirebaseAuth.getInstance().uid
        //user uid that we passed from NewMessageActivity when we click on that user = toId
        val toId = toUser?.uid

        //create user-message in FirebaseDB to store user message
        //fromId is current user and toId is user that want to message to
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        //retrieve List that contains all messages by collecting each messages in Child Event onChildAdded()
        ref.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                //snapshot.getValue used to marshall the data contained in snapshot into ChatMessage class
                val chatMessage = snapshot.getValue(ChatMessage::class.java)

                //check if the data from ChatMessage != null so we can add all that data into recyclerview ChatLog
                if (chatMessage != null) {
                    Log.d(TAG, chatMessage.text)

                    //check chatMessage.fromId is currentUser or not
                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        //retrieve currentUser to know the one who send the message
                        val currentUser = LatestMessagesActivity.currentUser ?: return
                        //add currentUser message and profile into recyclerview ChatLog
                        adapter.add(ChatToItem(chatMessage.text, currentUser))
                    } else {
                        //add the user massage that we want to message(partner) into recyclerview ChatLog
                        adapter.add(ChatFromItem(chatMessage.text, toUser!!))
                    }
                }

                //when open ChatLog we will the latest message
                recyclerview_chatlog.scrollToPosition(adapter.itemCount - 1)
            }

            override fun onCancelled(error: DatabaseError) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }
        })
    }

    private fun performSendMessage() {

        //get text from enter_message_edit_text_chat_log
        val text = enter_message_edit_text_chat_log.text.toString()
        ////Retrieve uid from FirebaseAuth
        val fromId = FirebaseAuth.getInstance().uid
        //get data from NewMessageActivity by using key value = USER_KEY into var toUser
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        //uid user that we want to message to
        val toId = user.uid
        //check if fromId = null and return
        if (fromId == null) return

        //create file in firebase to store user-message fromId to toId and toId to fromId at the same time
        /*push:	Add to a list of data in the database. Every time you push a new node onto a list,
        your database generates a unique key, like messages/users/<unique-user-id>/<username>*/
        val reference =
            FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toReference =
            FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        //call fun ChatMessage = chatMessage
        val chatMessage =
            ChatMessage(reference.key!!, text, fromId, toId, System.currentTimeMillis() / 1000)
        if (text.isEmpty()) {
            Toast.makeText(this, "Please enter message", Toast.LENGTH_SHORT).show()
            return
        } else {
            //write all value and data from ChatMessage into firebase database
            //set:	Write or replace data to a defined path, like messages/users/<username>
            reference.setValue(chatMessage)
                //addOnSuccessListener to write more code when data set into firebase successfully
                .addOnSuccessListener {
                    Log.d(TAG, "Saved our message: ${reference.key}")
                    //clear enter_message_edit_text_chat_log when we click send button
                    enter_message_edit_text_chat_log.text.clear()
                    //see the message that we send when we click button send
                    recyclerview_chatlog.scrollToPosition(adapter.itemCount - 1)
                }
            //write all value and data from ChatMessage into firebase database in node toId to fromId
            toReference.setValue(chatMessage)

            //create file in firebase to store latest-messages fromId to toId and toId to fromId at the same time
            val latestMessageRef =
                FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
            //write all value and data from ChatMessage into firebase database
            latestMessageRef.setValue(chatMessage)

            val latestMessageToRef =
                FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
            //write all value and data from ChatMessage into firebase database in node toId to fromId
            latestMessageToRef.setValue(chatMessage)
        }

    }
}
