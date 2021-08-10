package br.com.pignata.tuhm.data.database.dao

import androidx.room.*
import br.com.pignata.tuhm.data.database.entity.ProblemEntity

@Dao
abstract class ProblemDao {
    @Query("SELECT * FROM problem WHERE id_project = :idProject")
    abstract suspend fun loadProblemsWithIdProject(idProject: Int): List<ProblemEntity>

    @Insert
    abstract suspend fun insertProblem(problem: ProblemEntity)

    @Update
    abstract suspend fun updateProblem(problem: ProblemEntity)

    @Delete
    abstract suspend fun deleteProblem(problem: ProblemEntity)

    @Query("UPDATE project SET last_update = :timestamp WHERE id LIKE :id ")
    abstract suspend fun updateProjectLastUpdate(id: Int, timestamp: Long)

    @Transaction
    open suspend fun insertProblemAndUpdateProject(problem: ProblemEntity) {
        insertProblem(problem)
        updateProjectLastUpdate(problem.idProject ?: 0, System.currentTimeMillis())
    }

    @Transaction
    open suspend fun updateProblemAndProject(problem: ProblemEntity) {
        updateProblem(problem)
        updateProjectLastUpdate(problem.idProject ?: 0, System.currentTimeMillis())
    }
}