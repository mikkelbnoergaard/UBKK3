package com.example.ubkk3.dataLayer.local

import TournamentDatabase
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
    fun provideDataBase(@ApplicationContext context: Context): TournamentDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            TournamentDatabase::class.java,
            "tournament.db"
        ).build()
    }

    @Provides
    fun provideDao(tournamentDatabase: TournamentDatabase): TournamentDao = tournamentDatabase.tournamentDao
}