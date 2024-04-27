package com.rk.attendence.database.util

import androidx.room.TypeConverter
import java.time.LocalDate

class Converters {

    @TypeConverter
    fun fromStringToMap(value: String): Map<String, String> {
        val map = mutableMapOf<String, String>()
        value.split(",").forEach {
            val key = it.substring(0, it.indexOf("="))
            val v = it.substring(it.indexOf("=") + 1)
            map[key] = v
        }
        return map.toMap()
    }

    @TypeConverter
    fun mapToString(dayTime: Map<String, String>): String {
        var fin = ""
        var count = 0
        dayTime.forEach { (key, value) ->
            count++
            fin += if (count == dayTime.size) "$key=$value"
            else "$key=$value,"
        }
        return fin
    }

    @TypeConverter
    fun stringToList(day: String): List<String> {
        val list = mutableListOf<String>()
        day.split(",").forEach {
            list.add(it)
        }
        return list
    }

    @TypeConverter
    fun listToString(dayList: List<String>): String {
        var day = ""
        val lIndex = dayList.lastIndex
        dayList.forEachIndexed { index, d ->
            day += if (lIndex == index) d
            else "$d,"
        }
        return day
    }

    @TypeConverter
    fun dateToLong(date: LocalDate): Long {
        return date.toEpochDay()
    }

    @TypeConverter
    fun longToDate(long: Long): LocalDate {
        return LocalDate.ofEpochDay(long)
    }

}