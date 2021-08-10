package br.com.pignata.tuhm.view.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.pignata.tuhm.data.database.entity.ProjectEntity
import br.com.pignata.tuhm.model.DataStateCallback
import br.com.pignata.tuhm.repository.DatabaseRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ListProjectsViewModel(private val repository: DatabaseRepository) : ViewModel() {
    private val listProjectsMutable: MutableLiveData<DataStateCallback<ListProjectsState, List<ProjectEntity>>> by lazy { MutableLiveData() }
    val listProjects: LiveData<DataStateCallback<ListProjectsState, List<ProjectEntity>>>
        get() = listProjectsMutable

    fun loadAllProjects() {
        viewModelScope.launch {
            repository.loadAllProjects().collect {
                listProjectsMutable.postValue(DataStateCallback((if (it.isEmpty()) ListProjectsState.STATE_EMPTY else ListProjectsState.STATE_LIST) to it))
            }
        }
    }

    enum class ListProjectsState {
        STATE_EMPTY, STATE_LIST
    }
}