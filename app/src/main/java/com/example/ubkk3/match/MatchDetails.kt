package com.example.ubkk3.match

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.ubkk3.Converters.Converters

@Entity(
    tableName = "matchdetails",
    foreignKeys = [
        ForeignKey(entity = Tournament::class, parentColumns = ["id"], childColumns = ["tournamentId"]),
        ForeignKey(entity = TeamDetails::class, parentColumns = ["id"], childColumns = ["team1Id"]),
        ForeignKey(entity = TeamDetails::class, parentColumns = ["id"], childColumns = ["team2Id"])
    ]
)
@TypeConverters(Converters::class)
data class MatchDetails(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val tournamentId: Int = 0,

    val team1Id: Int,
    val team1Won: Boolean,

    val team2Id: Int,
    val team2Won: Boolean,

    val round: String
)