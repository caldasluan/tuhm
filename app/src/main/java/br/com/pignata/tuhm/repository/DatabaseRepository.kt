package br.com.pignata.tuhm.repository

import br.com.pignata.tuhm.data.database.dao.ProblemDao
import br.com.pignata.tuhm.data.database.dao.ProjectDao
import br.com.pignata.tuhm.data.database.entity.ProblemEntity
import br.com.pignata.tuhm.data.database.entity.ProjectEntity
import br.com.pignata.tuhm.data.database.entity.ProjectWithProblem
import kotlinx.coroutines.flow.Flow

class DatabaseRepository(private val projectDao: ProjectDao, private val problemDao: ProblemDao) {
    suspend fun insertProject(title: String, description: String, lastUpdate: Long) {
        projectDao.insertProject(
            ProjectEntity(
                title = title,
                description = description,
                lastUpdate = lastUpdate
            )
        )
    }

    suspend fun updateProject(
        idProject: Int,
        title: String,
        description: String,
        lastUpdate: Long
    ) {
        projectDao.updateProject(ProjectEntity(idProject, title, description, lastUpdate))
    }

    suspend fun deleteProblem(problemEntity: ProblemEntity) =
        problemDao.deleteProblem(problemEntity)

    suspend fun deleteProject(projectEntity: ProjectEntity) =
        projectDao.deleteProjectAndProblems(projectEntity)

    suspend fun insertProblemAndUpdateProject(
        description: String,
        gravity: Int,
        listHeuristics: List<Int>,
        image: String?,
        idProject: Int
    ) {
        problemDao.insertProblemAndUpdateProject(
            ProblemEntity(
                description = description,
                gravity = gravity,
                listHeuristics = listHeuristics,
                idProject = idProject,
                srcImage = image
            )
        )
    }

    suspend fun updateProblemAndProject(
        id: Int,
        description: String,
        gravity: Int,
        listHeuristics: List<Int>,
        image: String?,
        idProject: Int
    ) {
        problemDao.updateProblemAndProject(
            ProblemEntity(id, description, gravity, listHeuristics, image, idProject)
        )
    }

    suspend fun loadProblemsWithIdProject(idProject: Int): List<ProblemEntity> =
        problemDao.loadProblemsWithIdProject(idProject)

    fun loadAllProjects(): Flow<List<ProjectEntity>> = projectDao.loadAllProjects()

    fun loadProject(id: Int): Flow<List<ProjectWithProblem>> =
        projectDao.loadProjectWithProblems(id)
}