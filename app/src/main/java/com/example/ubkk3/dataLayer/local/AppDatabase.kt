package com.example.ubkk3.dataLayer.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ubkk3.match.MatchDetails
import com.example.ubkk3.match.Player
import com.example.ubkk3.match.TeamDetails
import com.example.ubkk3.match.Tournament

@Database(
    entities = [Tournament::class, MatchDetails::class, TeamDetails::class, Player:: class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {

    abstract fun tournamentDao(): TournamentDao
    abstract fun matchDetailsDao(): MatchDetailsDao
    abstract fun teamDetailsDao(): TeamDetailsDao
    abstract fun playerDao(): PlayerDao

}
