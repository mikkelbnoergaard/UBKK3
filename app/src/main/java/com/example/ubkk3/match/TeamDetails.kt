package com.example.ubkk3.match

import kotlinx.serialization.Serializable
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
@Serializable
data class TeamDetails(
    val id: String = "",
    var teamName: String =  "",
    var member1: Player? = null,
    var member2: Player? = null,
) : Parcelable
