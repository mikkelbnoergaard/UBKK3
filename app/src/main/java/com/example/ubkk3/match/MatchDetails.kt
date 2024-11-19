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
        childColumns = ["tournamentId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["tournamentId"])]
)
@TypeConverters(Converters::class)
data class MatchDetails(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val tournamentId: Int = 0,

    val team1: TeamDetails,
    val team1Won: Boolean,

    val team2: TeamDetails,
    val team2Won: Boolean,

    val round: String
)