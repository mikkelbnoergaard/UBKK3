package com.example.ubkk3.match

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Player(
    val name: String = "",
    val email: String = "",
    val cupsHit: Int = 0,
    val redemptions: Int = 0,
    val trickshots: Int = 0
) : Parcelable
