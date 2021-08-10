package br.com.pignata.tuhm.data.database.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "problem")
data class ProblemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo val description: String?,
    @ColumnInfo val gravity: Int?,
    @ColumnInfo(name = "list_heuristics") val listHeuristics: List<Int>?,
    @ColumnInfo val srcImage: String?,
    @ColumnInfo(name = "id_project") val idProject: Int?
) : Parcelable
