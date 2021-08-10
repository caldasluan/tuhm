package br.com.pignata.tuhm.data.database.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "project")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo var title: String?,
    @ColumnInfo var description: String?,
    @ColumnInfo(name = "last_update") var lastUpdate: Long?
) : Parcelable