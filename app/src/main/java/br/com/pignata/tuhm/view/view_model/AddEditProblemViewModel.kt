package br.com.pignata.tuhm.view.view_model

import android.graphics.Bitmap
import androidx.lifecycle.*
import br.com.pignata.tuhm.data.database.entity.ProblemEntity
import br.com.pignata.tuhm.repository.DatabaseRepository
import kotlinx.coroutines.launch
import java.io.File

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

    private val imageMutableLazy = lazy { MutableLiveData<Bitmap?>() }
    private val imageMutable: MutableLiveData<Bitmap?> by imageMutableLazy
    val image: LiveData<Bitmap?>
        get() = imageMutable

    val stateDescription = state.getLiveData<String?>("stateDescription")
    val stateGravity = state.getLiveData<String?>("stateGravity")

    fun insertProblem(
        description: String,
        gravity: Int,
        listHeuristics: List<Int>,
        image: String?,
        idProject: Int
    ) {
        viewModelScope.launch {
            newProblemStateMutable.postValue(NewProblemStatus.STATE_LOADING)
            repository.insertProblemAndUpdateProject(
                description,
                gravity,
                listHeuristics,
                image,
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
        image: String?,
        idProject: Int
    ) {
        viewModelScope.launch {
            newProblemStateMutable.postValue(NewProblemStatus.STATE_LOADING)
            repository.updateProblemAndProject(
                id,
                description,
                gravity,
                listHeuristics,
                image,
                idProject
            )
            newProblemStateMutable.postValue(NewProblemStatus.STATE_EDIT)
        }
    }

    fun deleteProblem(problemEntity: ProblemEntity) {
        viewModelScope.launch {
            newProblemStateMutable.postValue(NewProblemStatus.STATE_LOADING)

            problemEntity.srcImage?.let {
                val file = File(it)
                if (file.exists()) file.delete()
            }
            repository.deleteProblem(problemEntity)

            newProblemStateMutable.postValue(NewProblemStatus.STATE_DELETE)
        }
    }

    fun saveImage(image: Bitmap?) {
        imageMutable.value = image
    }

    fun saveImageInitialize(image: Bitmap?) {
        if (!imageMutableLazy.isInitialized()) {
            saveImage(image)
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