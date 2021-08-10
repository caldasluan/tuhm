package br.com.pignata.tuhm.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.pignata.tuhm.databinding.ViewHolderViolatedHeuristicBinding

class NewProblemAdapter : RecyclerView.Adapter<NewProblemAdapter.NewProblemViewHolder>() {
    private val listAll: MutableList<String> by lazy { mutableListOf() }
    val list: List<String>
        get() = listAll

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewProblemViewHolder {
        val binding = ViewHolderViolatedHeuristicBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NewProblemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewProblemViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    fun updateList(list: List<String>) {
        listAll.clear()
        listAll.addAll(list)
        notifyDataSetChanged()
    }

    inner class NewProblemViewHolder(private val binding: ViewHolderViolatedHeuristicBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(str: String) {
            binding.txtHeuristic.text = str
        }
    }
}