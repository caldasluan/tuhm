package br.com.pignata.tuhm.view.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.pignata.tuhm.data.database.entity.ProjectWithProblem
import br.com.pignata.tuhm.model.DataStateCallback
import br.com.pignata.tuhm.repository.DatabaseRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ProjectViewModel(private val repository: DatabaseRepository) : ViewModel() {
    private val projectMutable: MutableLiveData<DataStateCallback<ProjectState, ProjectWithProblem?>> by lazy { MutableLiveData() }
    val project: LiveData<DataStateCallback<ProjectState, ProjectWithProblem?>>
        get() = projectMutable

    fun loadProject(id: Int) {
        viewModelScope.launch {
            repository.loadProject(id)
                .collect { projectMutable.postValue(DataStateCallback((if (it.getOrNull(0)?.listProblems?.isEmpty() == true) ProjectState.STATE_EMPTY else ProjectState.STATE_LIST) to it.getOrNull(0))) }
        }
    }

    enum class ProjectState {
        STATE_EMPTY, STATE_LIST
    }
}