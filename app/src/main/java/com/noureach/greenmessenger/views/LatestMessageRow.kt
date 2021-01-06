package com.noureach.greenmessenger.views

import android.graphics.Color
import android.graphics.Typeface
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.noureach.greenmessenger.R
import com.noureach.greenmessenger.messages.LatestMessagesActivity
import com.noureach.greenmessenger.models.ChatMessage
import com.noureach.greenmessenger.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_profile_user.*
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessageRow(private val chatMessage: ChatMessage): Item<ViewHolder>(){

    var chatPartnerUser: User? = null

    override fun bind(viewHolder: ViewHolder, position: Int) {

        val chatPartnerId: String
        val tvMessageLatestMessage = viewHolder.itemView.message_textview_latest_message

        //check partner user
        if (chatMessage.fromId == FirebaseAuth.getInstance().uid){
            chatPartnerId = chatMessage.toId
            //load latest message into message_textview_latest_message
            tvMessageLatestMessage.text = "You: ${chatMessage.text}"
        }else{
            chatPartnerId = chatMessage.fromId
            tvMessageLatestMessage.setTextColor(Color.BLACK)
            tvMessageLatestMessage.setTypeface(tvMessageLatestMessage.typeface, Typeface.BOLD)
            tvMessageLatestMessage.text = chatMessage.text
        }

        //when know the user partner and load username and image
        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }
            override fun onDataChange(snapshot: DataSnapshot) {
                chatPartnerUser = snapshot.getValue(User::class.java)
                //display username into username_textview_latest_message
                viewHolder.itemView.username_textview_latest_message.text = chatPartnerUser?.username

                //display image
                val targetImageView = viewHolder.itemView.image_view_latest_message
                Picasso.get().load(chatPartnerUser?.profileImageUrl).into(targetImageView)
            }
        })
    }
    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }
}