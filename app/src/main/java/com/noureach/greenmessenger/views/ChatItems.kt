package com.noureach.greenmessenger.views

import android.content.Intent
import android.widget.Toast
import com.noureach.greenmessenger.R
import com.noureach.greenmessenger.messages.NewMessageActivity
import com.noureach.greenmessenger.messages.UserItem
import com.noureach.greenmessenger.models.User
import com.noureach.greenmessenger.registerlogin.PartnerProfile
import com.noureach.greenmessenger.registerlogin.ProfileUserActivity
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatToItem(val text: String, private val user: User): Item<ViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.apply {
            text_view_chat_to_row.text = text

            val uri = user.profileImageUrl
            val targetImageView = image_view_chat_to_row
            Picasso.get().load(uri).into(targetImageView)

            image_view_chat_to_row.setOnClickListener {
                val intent = Intent(context, ProfileUserActivity::class.java)
                context.startActivity(intent)
                }

            text_view_chat_to_row.setOnLongClickListener {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
                false
                 }
            }
        }
    }

class ChatFromItem(val text: String, private val user: User): Item<ViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.apply {
            text_view_chat_from_row.text = text

            val uri = user.profileImageUrl
            val targetImageView = image_view_chat_from_row
            Picasso.get().load(uri).into(targetImageView)

            image_view_chat_from_row.setOnClickListener {
                val intent = Intent(context, PartnerProfile::class.java)

                intent.putExtra("Partner", user)

                context.startActivity(intent)
            }

            text_view_chat_from_row.setOnLongClickListener {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
                false
            }
        }
    }
}
