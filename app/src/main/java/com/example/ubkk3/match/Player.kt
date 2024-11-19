package com.example.ubkk3.match

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.ubkk3.Converters.Converters

@Entity(
    foreignKeys = [ForeignKey(
        entity = Tournament::class,
        parentColumns = ["id"],
        childColumns = ["teamId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["teamId"])]
)
@TypeConverters(Converters::class)
data class Player(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val teamId: Int = 0,
    val name: String,
    val email: String,
    val cupsHit: Int = 0,
    val redemptions: Int = 0,
    val trickshots: Int = 0
)
