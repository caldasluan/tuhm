package br.com.pignata.tuhm.data.database.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromListInt(value: List<Int>?): String? {
        val gson = Gson()
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toListInt(value: String?): List<Int>? {
        val gson = Gson()
        val itemType = object : TypeToken<List<Int>>() {}.type
        return gson.fromJson(value, itemType)
    }
}