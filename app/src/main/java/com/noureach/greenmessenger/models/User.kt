package com.noureach.greenmessenger.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

// use Parcelable to pass custom Model or Data class from one Activity to another Activity

@Parcelize
class User(val uid: String, val username: String, var profileImageUrl: String): Parcelable{
    constructor() : this("", "", "")
}