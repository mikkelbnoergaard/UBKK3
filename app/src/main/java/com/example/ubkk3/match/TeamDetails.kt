package com.example.ubkk3.match

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.ubkk3.Converters.Converters

@Entity(tableName = "teamdetails")
@TypeConverters(Converters::class)
data class TeamDetails(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val matchId: Int = 0,
    var teamName: String =  ""
)
