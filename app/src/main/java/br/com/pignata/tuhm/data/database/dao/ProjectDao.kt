package br.com.pignata.tuhm.data.database.dao

import androidx.room.*
import br.com.pignata.tuhm.data.database.entity.ProjectEntity
import br.com.pignata.tuhm.data.database.entity.ProjectWithProblem
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ProjectDao {
    @Query("SELECT * FROM project ORDER BY last_update DESC")
    abstract fun loadAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM project WHERE id = :id")
    abstract fun loadProjectWithProblems(id: Int): Flow<List<ProjectWithProblem>>

    @Insert
    abstract suspend fun insertProject(project: ProjectEntity)

    @Update
    abstract suspend fun updateProject(project: ProjectEntity)

    @Delete
    abstract suspend fun deleteProject(project: ProjectEntity)

    @Query("DELETE FROM problem WHERE id_project = :idProject")
    abstract suspend fun deleteProblemWithIdProject(idProject: Int)

    @Transaction
    open suspend fun deleteProjectAndProblems(project: ProjectEntity) {
        deleteProject(project)
        deleteProblemWithIdProject(project.id)
    }
}