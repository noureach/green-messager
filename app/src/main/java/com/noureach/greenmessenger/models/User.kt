package com.noureach.greenmessenger.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(val uid: String, val username: String, var profileImageUrl: String): Parcelable{
    constructor() : this("", "", "")
}