package br.com.pignata.tuhm.model

data class DataStateCallback<S, D>(val state: S, val data: D) {
    constructor(pair: Pair<S, D>) : this(pair.first, pair.second)
}
