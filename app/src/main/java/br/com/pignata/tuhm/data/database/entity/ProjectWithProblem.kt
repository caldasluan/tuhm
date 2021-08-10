package br.com.pignata.tuhm.data.database.entity

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProjectWithProblem(
    @Embedded val project: ProjectEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id_project"
    )
    val listProblems: List<ProblemEntity>
) : Parcelable
