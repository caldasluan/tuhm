package br.com.pignata.tuhm.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import br.com.pignata.tuhm.R
import br.com.pignata.tuhm.data.database.entity.ProblemEntity
import br.com.pignata.tuhm.databinding.ViewHolderProblemReportBinding

class ReportAdapter : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {
    private val listAll: MutableList<ProblemEntity> by lazy { mutableListOf() }
    val list: List<ProblemEntity>
        get() = listAll
    private var click: ((ProblemEntity, View) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val binding = ViewHolderProblemReportBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReportViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    fun updateList(list: List<ProblemEntity>) {
        listAll.clear()
        listAll.addAll(list)
        notifyDataSetChanged()
    }

    fun setOnClick(onClick: ((ProblemEntity, View) -> Unit)?) {
        click = onClick
    }

    inner class ReportViewHolder(private val binding: ViewHolderProblemReportBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(problem: ProblemEntity) {
            binding.txtDescription.text = problem.description

            when (problem.gravity) {
                0 -> colorGravity(colorView = R.color.blue_gravity)
                1 -> colorGravity(colorView = R.color.green_gravity)
                2 -> colorGravity(colorView = R.color.yellow_gravity)
                3 -> colorGravity(colorView = R.color.orange_gravity)
                4 -> colorGravity(colorCard = R.color.red_gravity)
            }

            binding.layoutCard.setOnClickListener { click?.invoke(problem, it) }
        }

        private fun colorGravity(colorCard: Int? = null, colorView: Int? = null) {
            colorCard?.let {
                binding.cardLayout.setCardBackgroundColor(getColor(colorCard))
                binding.txtDescription.setTextColor(getColor(R.color.white))
            }
            colorView?.let { binding.viewGravity.setBackgroundColor(getColor(colorView)) }
        }

        private fun getColor(@ColorRes idColor: Int): Int =
            ResourcesCompat.getColor(
                binding.root.context.resources,
                idColor,
                null
            )

    }
}