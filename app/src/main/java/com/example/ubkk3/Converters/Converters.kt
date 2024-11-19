package com.example.ubkk3.Converters

import androidx.room.TypeConverter
import com.example.ubkk3.match.MatchDetails
import com.example.ubkk3.match.Player
import com.example.ubkk3.match.TeamDetails
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromMatchDetailsList(value: List<MatchDetails>?): String {
        val gson = Gson()
        val type = object : TypeToken<List<MatchDetails>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toMatchDetailsList(value: String): List<MatchDetails>? {
        val gson = Gson()
        val type = object : TypeToken<List<MatchDetails>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun toTeamDetails(value: String): TeamDetails? {
        val gson = Gson()
        val type = object : TypeToken<TeamDetails>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromTeamDetails(value: TeamDetails?): String {
        val gson = Gson()
        val type = object : TypeToken<TeamDetails>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toPlayerList(value: String): List<Player>? {
        val gson = Gson()
        val type = object : TypeToken<List<Player>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromPlayerList(value: List<Player>?): String {
        val gson = Gson()
        val type = object : TypeToken<List<Player>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toPlayer(value: String): Player? {
        val gson = Gson()
        val type = object : TypeToken<Player>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromPlayer(value: Player?): String {
        val gson = Gson()
        val type = object : TypeToken<Player>() {}.type
        return gson.toJson(value, type)
    }
}