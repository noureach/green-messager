package com.noureach.greenmessenger.registerlogin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.noureach.greenmessenger.R
import com.noureach.greenmessenger.models.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_partner_profile.*
import kotlinx.android.synthetic.main.activity_profile_user.*

class PartnerProfile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_partner_profile)

        val partner = intent.getParcelableExtra<User>("Partner")
        Picasso.get().load(partner?.profileImageUrl).into(iv_partner_profile)
        tv_profile_partner_username.text = partner?.username
    }
}