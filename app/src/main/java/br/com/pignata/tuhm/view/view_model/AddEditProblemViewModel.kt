package br.com.pignata.tuhm.view.view_model

import androidx.lifecycle.*
import br.com.pignata.tuhm.data.database.entity.ProblemEntity
import br.com.pignata.tuhm.repository.DatabaseRepository
import kotlinx.coroutines.launch

class AddEditProblemViewModel(
    private val state: SavedStateHandle,
    private val repository: DatabaseRepository
) : ViewModel() {
    private val newProblemStateMutable: MutableLiveData<NewProblemStatus> by lazy {
        MutableLiveData(
            NewProblemStatus.STATE_WAIT
        )
    }
    val newProblemState: LiveData<NewProblemStatus>
        get() = newProblemStateMutable

    val stateDescription = state.getLiveData<String?>("stateDescription")
    val stateGravity = state.getLiveData<String?>("stateGravity")

    fun insertProblem(
        description: String,
        gravity: Int,
        listHeuristics: List<Int>,
        idProject: Int
    ) {
        viewModelScope.launch {
            newProblemStateMutable.postValue(NewProblemStatus.STATE_LOADING)
            repository.insertProblemAndUpdateProject(
                description,
                gravity,
                listHeuristics,
                idProject
            )
            newProblemStateMutable.postValue(NewProblemStatus.STATE_ADD)
        }
    }

    fun updateProblem(
        id: Int,
        description: String,
        gravity: Int,
        listHeuristics: List<Int>,
        idProject: Int
    ) {
        viewModelScope.launch {
            newProblemStateMutable.postValue(NewProblemStatus.STATE_LOADING)
            repository.updateProblemAndProject(
                id,
                description,
                gravity,
                listHeuristics,
                idProject
            )
            newProblemStateMutable.postValue(NewProblemStatus.STATE_EDIT)
        }
    }

    fun deleteProblem(problemEntity: ProblemEntity) {
        viewModelScope.launch {
            newProblemStateMutable.postValue(NewProblemStatus.STATE_LOADING)
            repository.deleteProblem(problemEntity)
            newProblemStateMutable.postValue(NewProblemStatus.STATE_DELETE)
        }
    }

    fun saveState(desciption: String?, gravity: String?) {
        state["stateDescription"] = desciption
        state["stateGravity"] = gravity
    }

    enum class NewProblemStatus {
        STATE_WAIT, STATE_LOADING, STATE_ADD, STATE_EDIT, STATE_DELETE
    }
}