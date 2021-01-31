package com.noureach.greenmessenger.registerlogin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.noureach.greenmessenger.R
import com.noureach.greenmessenger.messages.LatestMessagesActivity
import com.noureach.greenmessenger.models.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_partner_profile.*
import kotlinx.android.synthetic.main.activity_profile_user.*

class ProfileUserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_user)

        //val partner = intent.getParcelableExtra<User>("User")
        Picasso.get().load(LatestMessagesActivity.currentUser?.profileImageUrl).into(iv_user_profile)
        tv_profile_user_username.text = LatestMessagesActivity.currentUser?.username
    }
}