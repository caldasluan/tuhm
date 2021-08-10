package br.com.pignata.tuhm.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.com.pignata.tuhm.data.database.converter.Converters
import br.com.pignata.tuhm.data.database.dao.ProblemDao
import br.com.pignata.tuhm.data.database.dao.ProjectDao
import br.com.pignata.tuhm.data.database.entity.ProblemEntity
import br.com.pignata.tuhm.data.database.entity.ProjectEntity

@Database(version = 1, entities = [ProjectEntity::class, ProblemEntity::class])
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        fun getInstance(context: Context): AppDatabase =
            Room.databaseBuilder(
                context,
                AppDatabase::class.java, "database"
            ).enableMultiInstanceInvalidation().build()
    }

    abstract fun projectDao(): ProjectDao
    abstract fun problemDao(): ProblemDao
}