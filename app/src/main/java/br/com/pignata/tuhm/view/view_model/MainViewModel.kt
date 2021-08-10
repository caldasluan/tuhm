package br.com.pignata.tuhm.view.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    private val textSearchMutable: MutableLiveData<String?> by lazy { MutableLiveData() }
    val textSearch: LiveData<String?>
        get() = textSearchMutable

    private val heuristicsSelectedMutable: MutableLiveData<MutableMap<Int, Boolean>> by lazy {
        MutableLiveData(mutableMapOf())
    }
    val heuristicsSelected: LiveData<MutableMap<Int, Boolean>>
        get() = heuristicsSelectedMutable

    fun udpateTextSearch(text: String?) {
        textSearchMutable.value = text
    }

    fun checkHeuristic(index: Int, check: Boolean) {
        val mapHeuristic = heuristicsSelected.value

        if (mapHeuristic?.getOrElse(index) { false } != check) {
            mapHeuristic?.put(index, check)
        }
        heuristicsSelectedMutable.value = mapHeuristic
    }

    fun addAllCheckHeuristic(list: List<Int>?) {
        val mapHeuristic = heuristicsSelected.value
        list?.forEach { mapHeuristic?.put(it, true) }
        heuristicsSelectedMutable.value = mapHeuristic
    }

    fun clearHeuristic() {
        heuristicsSelected.value?.clear()
        heuristicsSelectedMutable.value = heuristicsSelected.value
    }
}