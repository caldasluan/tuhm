package br.com.pignata.tuhm.view.view_model

import androidx.lifecycle.*
import br.com.pignata.tuhm.data.database.entity.ProjectEntity
import br.com.pignata.tuhm.repository.DatabaseRepository
import kotlinx.coroutines.launch

class AddEditProjectViewModel(
    private val state: SavedStateHandle,
    private val repository: DatabaseRepository
) : ViewModel() {
    private val addEditProjectStateMutable: MutableLiveData<AddEditProjectStatus> by lazy {
        MutableLiveData(
            AddEditProjectStatus.STATE_WAIT
        )
    }
    val addEditProjectState: LiveData<AddEditProjectStatus>
        get() = addEditProjectStateMutable

    val stateToolbarTitle = state.getLiveData<String?>("stateToolbarTitle")
    val stateTitle = state.getLiveData<String?>("stateTitle")
    val stateDescription = state.getLiveData<String?>("stateDescription")

    fun insertProject(title: String, description: String) {
        viewModelScope.launch {
            addEditProjectStateMutable.postValue(AddEditProjectStatus.STATE_LOADING)
            repository.insertProject(title, description, System.currentTimeMillis())
            addEditProjectStateMutable.postValue(AddEditProjectStatus.STATE_ADD)
        }
    }

    fun updateProject(idProject: Int, title: String, description: String) {
        viewModelScope.launch {
            addEditProjectStateMutable.postValue(AddEditProjectStatus.STATE_LOADING)
            repository.updateProject(idProject, title, description, System.currentTimeMillis())
            addEditProjectStateMutable.postValue(AddEditProjectStatus.STATE_EDIT)
        }
    }

    fun deleteProject(projectEntity: ProjectEntity) {
        viewModelScope.launch {
            addEditProjectStateMutable.postValue(AddEditProjectStatus.STATE_LOADING)
            repository.deleteProject(projectEntity)
            addEditProjectStateMutable.postValue(AddEditProjectStatus.STATE_DELETE)
        }
    }

    fun saveState(titleToolbar: String?, title: String?, desciption: String?) {
        state["stateToolbarTitle"] = titleToolbar
        state["stateTitle"] = title
        state["stateDescription"] = desciption
    }

    enum class AddEditProjectStatus {
        STATE_WAIT, STATE_LOADING, STATE_ADD, STATE_EDIT, STATE_DELETE
    }
}