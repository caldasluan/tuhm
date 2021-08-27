package br.com.pignata.tuhm.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import br.com.pignata.tuhm.R
import br.com.pignata.tuhm.databinding.FragmentListHeuristicsBinding
import br.com.pignata.tuhm.databinding.ViewHolderHeuristicBinding
import br.com.pignata.tuhm.enum.HeuristicEnum
import br.com.pignata.tuhm.view.view.setOnSingleClickListener
import br.com.pignata.tuhm.view.view_model.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ListHeuristicsFragment : Fragment() {
    private lateinit var binding: FragmentListHeuristicsBinding
    private val navController: NavController by lazy { findNavController() }
    private val mainViewModel: MainViewModel by sharedViewModel()
    private val listBindingHeuristics: MutableList<ViewHolderHeuristicBinding> by lazy { mutableListOf() }
    private val listSearch: MutableList<View> by lazy { mutableListOf() }
    private var currentSearchItem = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListHeuristicsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listBindingHeuristics.clear()
        addHeuristics()

        binding.searchBefore.setOnClickListener {
            currentSearchItem =
                if (currentSearchItem <= 0) listSearch.size - 1 else currentSearchItem - 1
            binding.searchTotal.text = "${currentSearchItem + 1}/${listSearch.size}"

            scrollTo(listSearch[currentSearchItem])
        }

        binding.searchNext.setOnClickListener {
            currentSearchItem =
                if (currentSearchItem >= listSearch.size - 1) 0 else currentSearchItem + 1
            binding.searchTotal.text = "${currentSearchItem + 1}/${listSearch.size}"

            scrollTo(listSearch[currentSearchItem])
        }

        mainViewModel.heuristicsSelected.observe(viewLifecycleOwner) {
            listBindingHeuristics.forEachIndexed { index, heuristicBinding ->
                val check = it.getOrElse(index) { false }
                if (heuristicBinding.checkTitle.isChecked != check) heuristicBinding.checkTitle.isChecked =
                    check
            }
        }

        mainViewModel.textSearch.observe(viewLifecycleOwner) { search(it) }
    }

    private fun addHeuristics() {
        val listHeuristc = resources.getStringArray(R.array.list_title_heuristic)
        val listSubHeuristic = resources.getStringArray(R.array.list_sub_heuristic)

        listHeuristc.forEachIndexed { index, s ->
            val heuristicEnum = HeuristicEnum.getHeuristicWithId(index)
            val bindingHeuristic =
                ViewHolderHeuristicBinding.inflate(layoutInflater, binding.layoutHeuristic, true)
            listBindingHeuristics.add(bindingHeuristic)

            bindingHeuristic.checkTitle.text = s
            bindingHeuristic.checkTitle.setOnCheckedChangeListener { _, isChecked ->
                mainViewModel.checkHeuristic(index, isChecked)
            }

            bindingHeuristic.layoutCard.setOnClickListener {
                bindingHeuristic.checkTitle.isChecked = !bindingHeuristic.checkTitle.isChecked
            }
            bindingHeuristic.iconExpand.setOnClickListener {
                bindingHeuristic.iconExpand.animate().cancel()
                if (bindingHeuristic.layoutSubHeuristics.visibility == View.VISIBLE) {
                    bindingHeuristic.layoutSubHeuristics.visibility = View.GONE
                    bindingHeuristic.iconExpand.animate().rotation(0f).start()
                } else {
                    bindingHeuristic.layoutSubHeuristics.visibility = View.VISIBLE
                    bindingHeuristic.iconExpand.animate().rotation(180f).start()
                }
            }
            bindingHeuristic.btnDetails.contentDescription =
                getString(R.string.cont_desc_btn_detail_heuristic, index + 1)
            bindingHeuristic.btnDetails.setOnSingleClickListener {
                navController.navigate(
                    ListHeuristicsFragmentDirections.actionListHeuristicsFragmentToHeuristicFragment(
                        index
                    )
                )
            }
            bindingHeuristic.layoutSubHeuristics.setOnClickListener(null)

            listSubHeuristic.copyOfRange(heuristicEnum.begin, heuristicEnum.end)
                .forEachIndexed { indexSubHeuristic, str ->
                    addSubHeuristic(
                        index,
                        heuristicEnum.begin + indexSubHeuristic,
                        str,
                        bindingHeuristic.layoutSubHeuristics
                    )
                }
        }
    }

    private fun addSubHeuristic(
        indexHeuristc: Int,
        indexSubHeuristic: Int,
        textCheck: String,
        layout: ViewGroup
    ) {
        val density = context?.resources?.displayMetrics?.density ?: 1f
        val text = TextView(context)
        text.text = getString(
            R.string.txt_list_sub_heuristic,
            indexHeuristc + 1,
            indexSubHeuristic + 1,
            textCheck
        )
        text.setTextAppearance(R.style.TextAppearance_MaterialComponents_Body2)

        val divider = View(context, null, 0, R.style.App_Divider)

        val layoutCheck = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = (density * 8).toInt()
        }

        val layoutDivider = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            density.toInt()
        ).apply {
            topMargin = (density * 8).toInt()
        }

        layout.addView(text, layoutCheck)
        layout.addView(divider, layoutDivider)
    }

    private fun scrollTo(view: View) {
        val position = if (view is CheckBox) (view.parent.parent as? CardView)?.top ?: 0
        else if (view is TextView) {
            (view.parent as? LinearLayout)?.visibility = View.VISIBLE

            view.top
                .plus((view.parent as? LinearLayout)?.top ?: 0)
                .plus((view.parent.parent.parent as? CardView)?.top ?: 0)
        } else 0

        binding.scroll.smoothScrollTo(0, position)
    }

    private fun search(query: String?) {
        if (query?.isNotEmpty() == true) {
            listSearch.clear()

            listBindingHeuristics.forEach {
                if (it.checkTitle.text.contains(query, true))
                    listSearch.add(it.checkTitle)

                it.layoutSubHeuristics.children.forEach { v ->
                    if ((v as? TextView)?.text?.contains(query, true) == true)
                        listSearch.add(v)
                }
            }

            currentSearchItem = -1
            binding.searchTotal.text = "0/${listSearch.size}"
            binding.cardSearch.visibility = View.VISIBLE
        } else {
            binding.cardSearch.visibility = View.GONE
        }
    }
}