package com.noureach.greenmessenger.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.noureach.greenmessenger.R
import com.noureach.greenmessenger.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class NewMessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        //use to set title bar
        supportActionBar?.title = "Select User"

        //call function
        fetchUser()
    }
    //use companion object to make val USER_KEY static
    companion object{
        const val USER_KEY = "USER_KEY"
    }
    private fun fetchUser(){
        //use to create file /users/ in Firebase Database for storing user information
        val ref = FirebaseDatabase.getInstance().getReference("/users/")
        //use ListenerForSingleValueEvent to read specific user from firebase
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                //use GroupAdapter<ViewHolder>() to bind data into ViewHolder
                val adapter = GroupAdapter<ViewHolder>()

                //A DataSnapshot contains data from a Database location
                //children is the data of each node and extract the contents of the snapshot data
                //use forEach to read child node one at the time
                snapshot.children.forEach {
                    Log.d("NewMessage", it.toString())
                    //it.getValue used to marshall the data contained in this snapshot into a User class
                    val user = it.getValue(User::class.java)
                    //check if the data that received is non-null
                    if (user!=null){
                        //then add data into recyclerView
                        adapter.add(UserItem(user))
                    }
                }
                //setOnItemClickListener to each items list in recyclerView once we click
                adapter.setOnItemClickListener { item, view ->

                    //declare parameter item as UserItem class = userItem
                    val userItem = item as UserItem

                    //show ChatLogActivity once we click on each user in recycler view
                    val intent = Intent(view.context, ChatLogActivity::class.java)
                    //use putExtra to pass specific data into ChatLogActivity
                    //we need user username to show in ChatLog actionbar once we click on that user
                    intent.putExtra(USER_KEY, userItem.user)
                    //start
                    startActivity(intent)
                }

                //connect adapter with recycler view
                recyclerview_newmessage.adapter = adapter
                //add line of each item list in recycler view
                recyclerview_newmessage.addItemDecoration(DividerItemDecoration(this@NewMessageActivity, DividerItemDecoration.VERTICAL))
            }

        })
    }
}
/*In the RecyclerItem class,
we can see that when we extend groupie’s item class,
 we override two functions, bind()
 which comes with a viewholder and the recycler view position and the getLayout()
 function where we specify the item layout we are working with,
 that is the item layout we created above. In the bind function,
 we call the viewholder variable passed in as a parameter in the bind function
 and do whatever we want to do with the views in our item layout.*/
class UserItem(val user: User): Item<ViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        //get username into recycler view
        viewHolder.itemView.username_text_view_new_message.text = user.username

        //use Picasso to get image into recycler view
        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.image_view_newmessage)
    }

}