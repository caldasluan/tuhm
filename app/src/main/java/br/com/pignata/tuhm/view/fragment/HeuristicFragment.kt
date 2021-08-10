package br.com.pignata.tuhm.view.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import br.com.pignata.tuhm.R
import br.com.pignata.tuhm.databinding.FragmentHeuristicBinding
import br.com.pignata.tuhm.enum.HeuristicEnum

class HeuristicFragment : Fragment() {
    private val args: HeuristicFragmentArgs by navArgs()
    private lateinit var binding: FragmentHeuristicBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHeuristicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val listTitle = resources.getStringArray(R.array.list_title_heuristic)
        val listDefinition = resources.getStringArray(R.array.list_definition_heuristic)
        val listExplication = resources.getStringArray(R.array.list_explication_heuristic)
        val listBenefits = resources.getStringArray(R.array.list_benefits_heuristic)
        val listProblems = resources.getStringArray(R.array.list_problems_heuristic)
        val listSubHeuristics = resources.getStringArray(R.array.list_sub_heuristic)
        val heuristicEnum = HeuristicEnum.getHeuristicWithId(args.idHeuristic)

        binding.txtTitle.text = listTitle[args.idHeuristic]
        binding.txtDefinition.text = listDefinition[args.idHeuristic]
        binding.txtExplication.text = listExplication[args.idHeuristic]
        binding.txtBenefits.text = listBenefits[args.idHeuristic]
        binding.txtProblems.text = listProblems[args.idHeuristic]
        for (i in heuristicEnum.begin until heuristicEnum.end) {
            addText(i, listSubHeuristics)
        }
    }

    private fun addText(index: Int, listSubHeuristics: Array<String>) {
        val textView = TextView(context)
        textView.text = getString(
            R.string.txt_list_sub_heuristic,
            args.idHeuristic + 1,
            index + 1,
            listSubHeuristics[index]
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextAppearance(R.style.TextAppearance_MaterialComponents_Body2)
        } else {
            textView.setTextAppearance(context, R.style.TextAppearance_MaterialComponents_Body2)
        }

        val density = context?.resources?.displayMetrics?.density ?: 1f
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = (density * 4).toInt()
            leftMargin = (density * 16).toInt()
            rightMargin = (density * 16).toInt()
        }

        binding.layout.addView(textView, layoutParams)
    }
}