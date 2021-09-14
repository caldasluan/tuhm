package br.com.pignata.tuhm.view.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.pignata.tuhm.R
import br.com.pignata.tuhm.data.database.entity.ProblemEntity
import br.com.pignata.tuhm.databinding.ViewHolderListProblemBinding

class ProjectAdapter(context: Context?) : RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {
    private val listAll: MutableList<ProblemEntity> by lazy { mutableListOf() }
    val list: List<ProblemEntity>
        get() = listAll

    private val listColors by lazy { context?.resources?.getIntArray(R.array.colors_gravity) }
    private val listSearch: MutableList<ProblemEntity> by lazy { mutableListOf() }
    private var click: ((ProblemEntity, View) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val binding =
            ViewHolderListProblemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProjectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        holder.bind(listSearch[position])
    }

    override fun getItemCount(): Int = listSearch.size

    fun updateList(listUpdate: List<ProblemEntity>?) {
        listAll.clear()
        listUpdate?.let { listAll.addAll(listUpdate) }
        updateSearchList(listAll)
    }

    fun updateSearchList(listUpdate: List<ProblemEntity>) {
        listSearch.clear()
        listSearch.addAll(listUpdate)
        notifyDataSetChanged()
    }

    fun updateListForDefault() = updateSearchList(listAll)

    fun setOnClick(onClick: ((ProblemEntity, View) -> Unit)?) {
        click = onClick
    }

    inner class ProjectViewHolder(private val binding: ViewHolderListProblemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(problem: ProblemEntity) {
            binding.viewGravity.setBackgroundColor(listColors?.get(problem.gravity ?: 0) ?: 0)
            binding.txtDescription.text = problem.description
            binding.txtHeuristicsViolateds.text = binding.root.context.getString(
                R.string.txt_violated_heuristics_count,
                problem.listHeuristics?.size ?: 0
            )
            binding.layoutCard.setOnClickListener { click?.invoke(problem, it) }
            if (!problem.srcImage.isNullOrEmpty()) {
                val bitmap = BitmapFactory.decodeFile(problem.srcImage)
                binding.imgProblem.setImageBitmap(bitmap)
                binding.imgProblem.visibility = View.VISIBLE
            } else {
                binding.imgProblem.visibility = View.GONE
            }
        }
    }
}