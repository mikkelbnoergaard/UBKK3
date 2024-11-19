package com.example.ubkk3.match

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.ubkk3.Converters.Converters

@Entity
@TypeConverters(Converters::class)
data class Tournament(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val tournamentName: String,
    val isActive: Boolean = false
)
