package br.com.pignata.tuhm.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.pignata.tuhm.R
import br.com.pignata.tuhm.databinding.FragmentReportProblemBinding
import br.com.pignata.tuhm.view.adapter.NewProblemAdapter

class ReportProblemFragment : Fragment() {
    private lateinit var binding: FragmentReportProblemBinding
    private val args: ReportProblemFragmentArgs by navArgs()
    private val newProblemAdapter by lazy { NewProblemAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReportProblemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val listGravity = context?.resources?.getStringArray(R.array.list_gravity)
        val listHeuristics = context?.resources?.getStringArray(R.array.list_title_heuristic)

        binding.txtDescription.text = args.problem.description
        binding.txtGravity.text = listGravity?.get(args.problem.gravity ?: 0)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = newProblemAdapter
        }

        val list = mutableListOf<String>()
        args.problem.listHeuristics?.forEach {
            list.add(listHeuristics?.get(it) ?: "")
        }
        newProblemAdapter.updateList(list)
    }
}