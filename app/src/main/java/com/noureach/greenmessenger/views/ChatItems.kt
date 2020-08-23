package com.noureach.greenmessenger.views

import com.noureach.greenmessenger.R
import com.noureach.greenmessenger.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatToItem(val text: String, val user: User): Item<ViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.text_view_chat_to_row.text = text

        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.image_view_chat_to_row
        Picasso.get().load(uri).into(targetImageView)
    }

}
class ChatFromItem(val text: String, val user: User): Item<ViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.text_view_chat_from_row.text = text


        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.image_view_chat_from_row
        Picasso.get().load(uri).into(targetImageView)
    }
}
