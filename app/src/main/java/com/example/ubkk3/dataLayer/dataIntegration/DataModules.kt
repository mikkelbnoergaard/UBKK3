package com.example.ubkk3.dataLayer.local

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModules {

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "tournament.db"
        ).build()
    }

    @Provides
    fun provideTournamentDao(tournamentDatabase: AppDatabase): TournamentDao = tournamentDatabase.tournamentDao()

    @Provides
    fun provideMatchDetailsDao(tournamentDatabase: AppDatabase): MatchDetailsDao = tournamentDatabase.matchDetailsDao()

    @Provides
    fun provideTeamDetailsDao(tournamentDatabase: AppDatabase): TeamDetailsDao = tournamentDatabase.teamDetailsDao()

    @Provides
    fun providePlayerDao(tournamentDatabase: AppDatabase): PlayerDao = tournamentDatabase.playerDao()
}