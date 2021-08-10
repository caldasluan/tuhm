package br.com.pignata.tuhm.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.pignata.tuhm.data.database.entity.ProjectEntity
import br.com.pignata.tuhm.databinding.ViewHolderListProjectBinding
import br.com.pignata.tuhm.view.view.setOnSingleClickListener

class ListProjectsAdapter : RecyclerView.Adapter<ListProjectsAdapter.ListProjectsViewHolder>() {
    private val listAll: MutableList<ProjectEntity> by lazy { mutableListOf() }
    val list: List<ProjectEntity>
        get() = listAll

    private val listSearch: MutableList<ProjectEntity> by lazy { mutableListOf() }
    private var click: ((ProjectEntity, View) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListProjectsViewHolder {
        val binding =
            ViewHolderListProjectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListProjectsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListProjectsViewHolder, position: Int) {
        holder.bind(listSearch[position])
    }

    override fun getItemCount(): Int = listSearch.size

    fun updateList(listUpdate: List<ProjectEntity>) {
        listAll.clear()
        listAll.addAll(listUpdate)
        updateSearchList(listAll)
    }

    fun updateSearchList(listUpdate: List<ProjectEntity>) {
        listSearch.clear()
        listSearch.addAll(listUpdate)
        notifyDataSetChanged()
    }

    fun updateListForDefault() = updateSearchList(listAll)

    fun setOnClick(onClick: ((ProjectEntity, View) -> Unit)?) {
        click = onClick
    }

    inner class ListProjectsViewHolder(private val binding: ViewHolderListProjectBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(projectEntity: ProjectEntity) {
            binding.txtTitle.text = projectEntity.title
            binding.txtDescription.text = projectEntity.description
            binding.root.setOnSingleClickListener { click?.invoke(projectEntity, it) }
        }
    }
}