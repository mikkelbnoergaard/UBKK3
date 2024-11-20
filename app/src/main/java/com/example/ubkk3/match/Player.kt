package com.example.ubkk3.match

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.ubkk3.Converters.Converters

@Entity(
    tableName = "player",
    indices = [Index(value = ["teamId"])],
    foreignKeys = [ForeignKey(entity = TeamDetails::class, parentColumns = ["id"], childColumns = ["teamId"])]
)
data class Player(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val teamId: Int,
    val name: String,
    val email: String
)