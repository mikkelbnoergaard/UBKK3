package com.example.ubkk3.match

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.ubkk3.Converters.Converters

@Entity(
    tableName = "matchdetails",
    indices = [
        Index(value = ["tournamentId"]),
        Index(value = ["team1Id"]),
        Index(value = ["team2Id"])
    ],
    foreignKeys = [
        ForeignKey(entity = Tournament::class, parentColumns = ["id"], childColumns = ["tournamentId"]),
        ForeignKey(entity = TeamDetails::class, parentColumns = ["id"], childColumns = ["team1Id"]),
        ForeignKey(entity = TeamDetails::class, parentColumns = ["id"], childColumns = ["team2Id"])
    ]
)
data class MatchDetails(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tournamentId: Int,
    val team1Id: Int,
    val team2Id: Int,
    val team1Won: Boolean? = null,
    val team2Won: Boolean? = null,
    val round: String? = null
)